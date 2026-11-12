/* A tournament's custom card pool (swccg-guild/work#222). No framework beyond jQuery, ES5 syntax.
 *
 *   - CardPoolForm.Form: the Create Tournament form's "Custom card pool" section. The organizer starts from the chosen
 *     format's sets or types a set list, searches cards by title or GEMP id and allows or bans them. Cards with the same
 *     title are shown with side, set and id. A live preview asks the server (/tournament/poolPreview), which runs the
 *     same checks and writes the same lines as the saved event, so the preview and the event agree.
 *   - CardPoolForm.showRules: the rules summary of a player-made event (/tournament/rules/<id>) for players, in the
 *     join dialog and from the hall's queue and tournament rows. It reads the saved event, so it works after the host
 *     has left. The pool is locked once the tournament is open (Bill, 8 Nov 2026), so there is nothing to edit there.
 *
 * The server is the final word on every check; this file only spares the organizer a round trip.
 */
var CardPoolForm = (function () {
    var POOL_CHIP = "Custom card pool";

    /** "1, 2 7" -> [1, 2, 7] (sorted, no repeats); blank -> null (the format's own sets). Throws on anything else. */
    function parseSets(text) {
        if (text == null || String(text).trim() === "")
            return null;
        var parts = String(text).split(/[\s,;]+/), seen = {}, sets = [];
        for (var i = 0; i < parts.length; i++) {
            if (parts[i] === "")
                continue;
            if (!/^\d+$/.test(parts[i]))
                throw new Error("\"" + parts[i] + "\" is not a set number");
            var n = parseInt(parts[i], 10);
            if (n <= 0)
                throw new Error("\"" + parts[i] + "\" is not a set number");
            if (!seen[n]) {
                seen[n] = true;
                sets.push(n);
            }
        }
        sets.sort(function (a, b) { return a - b; });
        return sets;
    }

    /** A GEMP id as a pool names it: foil and alternate-image marks dropped ("1_80*" -> "1_80"). */
    function normalizeId(id) {
        return String(id == null ? "" : id).replace(/[*^]/g, "").trim();
    }

    /** Ids both allowed and banned (the server also checks other printings of the same card). */
    function conflicts(allow, ban) {
        var banned = {}, both = [];
        for (var i = 0; i < ban.length; i++)
            banned[normalizeId(ban[i])] = true;
        for (var j = 0; j < allow.length; j++)
            if (banned[normalizeId(allow[j])] && both.indexOf(normalizeId(allow[j])) < 0)
                both.push(normalizeId(allow[j]));
        return both;
    }

    /**
     * The JSON the server reads ({"sets":[..],"allow":[..],"ban":[..]}), or "" when nothing is customized, which is
     * the named format as it is. state: {enabled, setsText, allow: [ids], ban: [ids]}.
     */
    function poolJson(state) {
        if (!state || !state.enabled)
            return "";
        var sets = parseSets(state.setsText);
        var allow = uniqueIds(state.allow), ban = uniqueIds(state.ban);
        if (sets == null && allow.length === 0 && ban.length === 0)
            return "";
        var pool = {};
        if (sets != null)
            pool.sets = sets;
        pool.allow = allow;
        pool.ban = ban;
        return JSON.stringify(pool);
    }

    function uniqueIds(ids) {
        var result = [];
        for (var i = 0; ids && i < ids.length; i++) {
            var id = normalizeId(ids[i]);
            if (id !== "" && result.indexOf(id) < 0)
                result.push(id);
        }
        return result;
    }

    /**
     * Display names for search results: the title alone when it is unique in the list, else the title with side, set
     * and id ("Alderaan (Light, set 1, 1_121)"), so a Light and a Dark card or two versions can be told apart.
     * cards: [{id, title, side, set}].
     */
    function labelCards(cards) {
        var count = {};
        for (var i = 0; i < cards.length; i++)
            count[cards[i].title] = (count[cards[i].title] || 0) + 1;
        var labels = [];
        for (var j = 0; j < cards.length; j++) {
            var c = cards[j];
            labels.push(count[c.title] > 1 ? c.title + " (" + (c.side ? c.side + ", " : "") + "set " + c.set + ", " + c.id + ")" : c.title);
        }
        return labels;
    }

    /** <cards><card id title side set label/></cards> -> [{id, title, side, set, label}]. */
    function cardsFromXml(xml) {
        var root = xml && xml.documentElement, cards = [];
        if (!root || root.tagName !== "cards")
            return cards;
        var nodes = root.getElementsByTagName("card");
        for (var i = 0; i < nodes.length; i++)
            cards.push({id: nodes[i].getAttribute("id"), title: nodes[i].getAttribute("title"),
                side: nodes[i].getAttribute("side"), set: nodes[i].getAttribute("set"), label: nodes[i].getAttribute("label")});
        return cards;
    }

    /** <rules><rule text/></rules> -> ["..."]; <error message/> -> {error: "..."}. */
    function rulesFromXml(xml) {
        var root = xml && xml.documentElement;
        if (!root)
            return {error: "No answer from the server"};
        if (root.tagName === "error")
            return {error: root.getAttribute("message") || "The card pool was refused"};
        var lines = [], nodes = root.getElementsByTagName("rule");
        for (var i = 0; i < nodes.length; i++)
            lines.push(nodes[i].getAttribute("text"));
        return lines;
    }

    /** Whether a hall row's option chips say the event has a custom card pool. */
    function hasPool(optionsText) {
        return optionsText != null && String(optionsText).indexOf(POOL_CHIP) >= 0;
    }

    function renderLines(target, lines) {
        target.empty();
        if (lines && lines.error) {
            target.append($("<div class='card-pool-error'></div>").text(lines.error));
            return;
        }
        var list = $("<ul class='card-pool-rules'></ul>");
        for (var i = 0; i < lines.length; i++)
            list.append($("<li></li>").text(lines[i]));
        target.append(list);
    }

    /** Fills {@code target} with an event's rules summary (players' view, R2/R4). */
    function showRules(comm, eventId, target) {
        target.text("Loading the event's rules...");
        comm.getEventRules(eventId, function (xml) {
            renderLines(target, rulesFromXml(xml));
        }, {"0": function () { target.text("Could not load the event's rules."); }});
    }

    /** A small dialog with an event's rules, for the hall rows' "Rules" link. */
    function showRulesDialog(comm, eventId, title) {
        var dialog = $("<div class='card-pool-rules-dialog'></div>");
        var body = $("<div></div>");
        dialog.append(body);
        $("body").append(dialog);
        if (dialog.dialog)
            dialog.dialog({title: title || "Event rules", modal: true, width: 460, close: function () { dialog.remove(); }});
        showRules(comm, eventId, body);
        return dialog;
    }

    /**
     * The Create Tournament form's section. comm: the hall's communication object; container: where it goes;
     * formatCode(): the form's chosen format code.
     */
    function Form(comm, container, formatCode) {
        this.comm = comm;
        this.formatCode = formatCode;
        this.allow = [];
        this.ban = [];
        this.labels = {};
        this.build(container);
    }

    Form.prototype = {
        build: function (container) {
            var that = this;
            if ($("#card-pool-style").length === 0)
                $("<style id='card-pool-style'></style>").text(
                    ".card-pool-form{flex-wrap:wrap}.card-pool-body{width:100%;margin:4px 0 0 0}" +
                    ".card-pool-line{margin:3px 0}.card-pool-line>span{display:inline-block;min-width:64px;font-weight:bold}" +
                    ".card-pool-sets,.card-pool-search{width:70%}.card-pool-results{max-height:140px;overflow-y:auto}" +
                    ".card-pool-result button{font-size:11px;margin-left:3px}.card-pool-chip{display:inline-block;padding:1px 4px;" +
                    "margin:1px;border:1px solid #999;border-radius:3px}.card-pool-remove{border:0;background:none;cursor:pointer}" +
                    ".card-pool-error{color:#b00}.card-pool-rules{margin:4px 0;padding-left:18px;text-align:left}"
                ).appendTo("head");
            this.root = $("<div class='play-form-row card-pool-form'></div>");
            this.toggle = $("<input type='checkbox' id='tournament-card-pool-toggle'/>");
            this.root.append($("<label class='play-form-label' for='tournament-card-pool-toggle'></label>")
                .append(this.toggle).append(" Custom card pool"));
            this.body = $("<div class='card-pool-body' style='display:none'></div>");
            this.setsInput = $("<input type='text' class='card-pool-sets' placeholder='Sets, e.g. 1, 2, 7 (blank: the format&#39;s sets)'/>");
            this.body.append($("<div class='card-pool-line'></div>").append("<span>Sets</span> ").append(this.setsInput));
            this.searchInput = $("<input type='search' class='card-pool-search' placeholder='Find a card by title or id'/>");
            this.results = $("<div class='card-pool-results'></div>");
            this.body.append($("<div class='card-pool-line'></div>").append(this.searchInput)).append(this.results);
            this.allowList = $("<div class='card-pool-allow'></div>");
            this.banList = $("<div class='card-pool-ban'></div>");
            this.body.append($("<div class='card-pool-line'><span>Allowed</span></div>").append(this.allowList));
            this.body.append($("<div class='card-pool-line'><span>Banned</span></div>").append(this.banList));
            this.preview = $("<div class='card-pool-preview' role='status' aria-live='polite'></div>");
            this.body.append(this.preview);
            this.root.append(this.body);
            container.append(this.root);

            this.toggle.change(function () {
                that.body.css("display", that.toggle.is(":checked") ? "" : "none");
                that.refreshPreview();
            });
            this.setsInput.on("change blur", function () { that.refreshPreview(); });
            var searchTimer = null;
            this.searchInput.on("input", function () {
                clearTimeout(searchTimer);
                searchTimer = setTimeout(function () { that.search(that.searchInput.val()); }, 250);
            });
        },

        state: function () {
            return {enabled: this.toggle.is(":checked"), setsText: this.setsInput.val(), allow: this.allow, ban: this.ban};
        },

        /** The JSON for /tournament/create, or "" for the named format; throws on a bad set list. */
        json: function () {
            var both = conflicts(this.allow, this.ban);
            if (both.length > 0)
                throw new Error("Both allowed and banned: " + both.join(", ") + ". Choose one.");
            return poolJson(this.state());
        },

        show: function (visible) {
            this.root.css("display", visible ? "" : "none");
        },

        reset: function () {
            this.toggle.prop("checked", false);
            this.body.css("display", "none");
            this.setsInput.val("");
            this.allow = [];
            this.ban = [];
            this.renderExceptions();
            this.preview.empty();
        },

        search: function (query) {
            var that = this;
            this.results.empty();
            if (!query || String(query).trim().length < 2)
                return;
            this.comm.searchPoolCards(query, function (xml) {
                var cards = cardsFromXml(xml), labels = labelCards(cards);
                that.results.empty();
                if (cards.length === 0)
                    that.results.text("No card found");
                for (var i = 0; i < cards.length; i++)
                    that.results.append(that.resultRow(cards[i], labels[i]));
            }, {"0": function () { that.results.text("Card search is unavailable"); }});
        },

        resultRow: function (card, label) {
            var that = this;
            this.labels[card.id] = card.label || label;
            var row = $("<div class='card-pool-result'></div>").append($("<span></span>").text(label)).append(" ");
            row.append($("<button type='button'>Allow</button>").click(function () { that.add("allow", card.id); }));
            row.append($("<button type='button'>Ban</button>").click(function () { that.add("ban", card.id); }));
            return row;
        },

        add: function (kind, id) {
            id = normalizeId(id);
            var list = kind === "allow" ? this.allow : this.ban, other = kind === "allow" ? this.ban : this.allow;
            var at = other.indexOf(id);
            if (at >= 0)
                other.splice(at, 1);   // moving a card from one list to the other, never both
            if (list.indexOf(id) < 0)
                list.push(id);
            this.renderExceptions();
            this.refreshPreview();
        },

        remove: function (kind, id) {
            var list = kind === "allow" ? this.allow : this.ban, at = list.indexOf(id);
            if (at >= 0)
                list.splice(at, 1);
            this.renderExceptions();
            this.refreshPreview();
        },

        renderExceptions: function () {
            var that = this;
            var render = function (target, kind, ids) {
                target.empty();
                for (var i = 0; i < ids.length; i++) {
                    (function (id) {
                        var chip = $("<span class='card-pool-chip'></span>").text(that.labels[id] || id);
                        chip.append($("<button type='button' class='card-pool-remove' aria-label='Remove'>&times;</button>")
                            .click(function () { that.remove(kind, id); }));
                        target.append(chip).append(" ");
                    })(ids[i]);
                }
            };
            render(this.allowList, "allow", this.allow);
            render(this.banList, "ban", this.ban);
        },

        /** Asks the server what the saved event will show for this pool, or why it would be refused. */
        refreshPreview: function () {
            var that = this, json;
            try {
                json = this.json();
            } catch (e) {
                renderLines(this.preview, {error: e.message});
                return;
            }
            if (json === "") {
                this.preview.text(this.toggle.is(":checked") ? "No changes: the format's own card pool." : "");
                return;
            }
            var format = this.formatCode ? this.formatCode() : null;
            if (!format)
                return;
            this.comm.previewCardPool(format, json, function (xml) {
                renderLines(that.preview, rulesFromXml(xml));
            }, {"0": function () { that.preview.text("Preview unavailable"); }});
        }
    };

    var api = {parseSets: parseSets, normalizeId: normalizeId, conflicts: conflicts, poolJson: poolJson,
        labelCards: labelCards, cardsFromXml: cardsFromXml, rulesFromXml: rulesFromXml, hasPool: hasPool,
        showRules: showRules, showRulesDialog: showRulesDialog, Form: Form, POOL_CHIP: POOL_CHIP};
    if (typeof module === "object" && module.exports)
        module.exports = api;
    return api;
})();
