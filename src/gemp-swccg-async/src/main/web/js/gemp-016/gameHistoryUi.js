/**
 * My Account > My Game History: the logged-in player's games, newest first, a page at a time, with server-side filters
 * (format, opponent, event, side, result, date range).  Data comes from GET /gameHistory (XML; see
 * GameHistoryRequestHandler) and the filter suggestions from GET /gameHistory/filters.
 *
 *   new GameHistoryUI(url, {container: $("#gameHistory"), comm: optionalCommunication, pageSize: 20, link: "#account/history?..."})
 *   ui.refresh();        // re-fetch the page shown, with the filters last applied (the Account tab calls it when the
 *                        // viewer comes back); what is typed in the filter form but not applied is left alone
 *   ui.applyLink(params) // show the filters and page a hall link names (hallLinks.js: #account/history?opponent=..&page=2)
 *
 * Ported from the LOTR GEMP hall (PlayersCouncil/gemp-lotr gameHistoryUi.js).  SWCCG additions: the Side (Dark /
 * Light) and Result (won / lost) filters and the Side column; the filters and page live in the hall link, so a
 * filtered history can be shared or opened from another page; the opponent, side, format and event in each row are
 * links that filter on that value (an opponent exactly, by name), and a league game's event also links to the league
 * on the Events tab.
 */
var GameHistoryUI = Class.extend({
    communication:null,
    container:null,
    itemStart:0,
    pageSize:20,
    matching:0,
    total:0,
    filters:null,
    requestNumber:0,

    formSelect:null,
    opponentInput:null,
    eventInput:null,
    eventList:null,
    sideSelect:null,
    resultSelect:null,
    fromInput:null,
    toInput:null,
    statusDiv:null,
    tableDiv:null,
    pagers:null,

    // the filters a hall link may carry, in the order they are written into it
    LINK_KEYS: ["format", "opponent", "opponentExact", "event", "side", "result", "from", "to"],

    init:function (url, options) {
        options = options || {};
        this.communication = options.comm || new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });
        this.container = options.container || $("#gameHistory");
        if (options.pageSize)
            this.pageSize = options.pageSize;
        this.filters = {};
        this.buildLayout();
        this.loadFilterOptions();
        var initial = options.link ? this.parseLink(options.link) : null;
        if (initial != null)
            this.showState(this.linkState(initial));
        else
            this.loadHistory();
    },

    buildLayout:function () {
        var that = this;
        this.container.empty().addClass("game-history");

        var form = $("<form class='game-history-filters' autocomplete='off'></form>");
        var field = function (label, input) {
            var id = "game-history-" + label.toLowerCase().replace(/[^a-z]+/g, "-");
            input.attr("id", id);
            return $("<span class='game-history-field'></span>")
                .append($("<label></label>").attr("for", id).text(label))
                .append(input);
        };

        this.formSelect = $("<select name='format'></select>").append($("<option value=''></option>").text("Any format"));
        this.opponentInput = $("<input type='text' name='opponent' maxlength='45' placeholder='Name or start of name'>");
        this.eventList = $("<datalist id='game-history-event-options'></datalist>");
        this.eventInput = $("<input type='text' name='event' maxlength='255' list='game-history-event-options' placeholder='League, tournament or \"casual\"'>");
        this.sideSelect = $("<select name='side'></select>")
            .append($("<option value=''></option>").text("Either side"))
            .append($("<option value='Dark'></option>").text("Dark Side"))
            .append($("<option value='Light'></option>").text("Light Side"));
        this.resultSelect = $("<select name='result'></select>")
            .append($("<option value=''></option>").text("Won or lost"))
            .append($("<option value='W'></option>").text("Won"))
            .append($("<option value='L'></option>").text("Lost"));
        this.fromInput = $("<input type='date' name='from'>");
        this.toInput = $("<input type='date' name='to'>");

        form.append(field("Format", this.formSelect))
            .append(field("Opponent", this.opponentInput))
            .append(field("Event", this.eventInput))
            .append(this.eventList)
            .append(field("Side", this.sideSelect))
            .append(field("Result", this.resultSelect))
            .append(field("From", this.fromInput))
            .append(field("To", this.toInput));

        var apply = $("<button type='submit' class='game-history-apply'>Apply</button>").button();
        var clear = $("<button type='button' class='game-history-clear'>Clear</button>").button();
        form.append($("<span class='game-history-buttons'></span>").append(apply).append(clear));

        form.on("submit", function (e) {
            e.preventDefault();
            that.applyFilters();
        });
        clear.on("click", function () {
            that.clearFilters();
        });

        this.statusDiv = $("<div class='game-history-status' role='status' aria-live='polite'></div>");
        this.tableDiv = $("<div class='game-history-table'></div>");
        this.pagers = [this.createPager(), this.createPager()];

        this.container.append(form)
            .append(this.pagers[0].element)
            .append(this.statusDiv)
            .append(this.tableDiv)
            .append(this.pagers[1].element);
    },

    createPager:function () {
        var that = this;
        var element = $("<div class='game-history-pager'></div>");
        var mk = function (label, cls, ariaLabel) {
            return $("<button type='button'></button>").addClass(cls).text(label).attr("aria-label", ariaLabel).button();
        };
        var first = mk("« First", "pager-first", "First page");
        var prev = mk("‹ Previous", "pager-prev", "Previous page");
        var info = $("<span class='pager-info'></span>");
        var next = mk("Next ›", "pager-next", "Next page");
        var last = mk("Last »", "pager-last", "Last page");
        first.on("click", function () { that.goToStart(0); });
        prev.on("click", function () { that.goToStart(that.itemStart - that.pageSize); });
        next.on("click", function () { that.goToStart(that.itemStart + that.pageSize); });
        last.on("click", function () { that.goToStart((that.pageCount() - 1) * that.pageSize); });
        element.append(first).append(prev).append(info).append(next).append(last);
        return {element: element, first: first, prev: prev, info: info, next: next, last: last};
    },

    pageCount:function () {
        return Math.max(1, Math.ceil(this.matching / this.pageSize));
    },

    goToStart:function (start) {
        var maxStart = (this.pageCount() - 1) * this.pageSize;
        start = Math.max(0, Math.min(maxStart, start));
        if (start === this.itemStart)
            return;
        this.itemStart = start;
        this.loadHistory();
    },

    readFilters:function () {
        return {
            format: this.formSelect.val() || "",
            opponent: $.trim(this.opponentInput.val() || ""),
            event: $.trim(this.eventInput.val() || ""),
            side: this.sideSelect.val() || "",
            result: this.resultSelect.val() || "",
            from: this.fromInput.val() || "",
            to: this.toInput.val() || ""
        };
    },

    writeFilters:function (filters) {
        this.ensureOption(this.formSelect, filters.format);
        this.formSelect.val(filters.format || "");
        this.opponentInput.val(filters.opponent || "");
        this.eventInput.val(filters.event || "");
        this.sideSelect.val(filters.side || "");
        this.resultSelect.val(filters.result || "");
        this.fromInput.val(filters.from || "");
        this.toInput.val(filters.to || "");
    },

    // a value that is not in the list (a link to a format the player has no games in yet, or the suggestions not
    // loaded yet) is added so the select can show it
    ensureOption:function (select, value) {
        if (!value)
            return;
        var found = select.find("option").filter(function () {
            return $(this).val() === value;
        }).length > 0;
        if (!found)
            select.append($("<option></option>").attr("value", value).text(value));
    },

    applyFilters:function () {
        var filters = this.readFilters();
        if (filters.from && filters.to && filters.from > filters.to) {
            this.statusDiv.text("The 'From' date is after the 'To' date.");
            return;
        }
        this.filters = filters;
        this.itemStart = 0;
        this.loadHistory();
    },

    clearFilters:function () {
        this.writeFilters({});
        this.filters = {};
        this.itemStart = 0;
        this.loadHistory();
    },

    hasFilters:function () {
        var f = this.filters || {};
        return !!(f.format || f.opponent || f.event || f.side || f.result || f.from || f.to);
    },

    // ---- hall links (hallLinks.js): #account/history?format=..&opponent=..&page=2 ----

    // the link naming the filters applied and the page shown
    currentLink:function () {
        var query = {};
        var f = this.filters || {};
        for (var i = 0; i < this.LINK_KEYS.length; i++) {
            var key = this.LINK_KEYS[i];
            if (f[key])
                query[key] = f[key] === true ? "true" : f[key];
        }
        var page = Math.floor(this.itemStart / this.pageSize) + 1;
        if (page > 1)
            query.page = page;
        var q = window.GempLinks ? GempLinks.buildQuery(query) : "";
        return q ? "#account/history?" + q : "#account/history";
    },

    // {filters, page} for a history link, or null when it is not one
    parseLink:function (hash) {
        var m = /^#(?:account\/history|history)(?:\?(.*))?$/.exec(hash || "");
        if (m == null || !window.GempLinks)
            return null;
        return GempLinks.parseQuery(m[1] || "");
    },

    // the filters and page a link's query names, normalized
    linkState:function (params) {
        params = params || {};
        var filters = {};
        for (var i = 0; i < this.LINK_KEYS.length; i++) {
            var key = this.LINK_KEYS[i];
            if (key === "opponentExact")
                continue;
            if (params[key] != null && String(params[key]) !== "")
                filters[key] = String(params[key]);
        }
        if (filters.opponent && String(params.opponentExact) === "true")
            filters.opponentExact = true;
        var page = Math.max(1, parseInt(params.page, 10) || 1);
        return {filters: filters, start: (page - 1) * this.pageSize};
    },

    // the same filters and page as shown (or loading) do nothing: the link was written by this page, or the page was
    // opened on it already
    sameAsShown:function (state) {
        if (state.start !== this.itemStart)
            return false;
        var mine = this.filters || {};
        for (var i = 0; i < this.LINK_KEYS.length; i++) {
            var key = this.LINK_KEYS[i];
            if (String(mine[key] || "") !== String(state.filters[key] || ""))
                return false;
        }
        return true;
    },

    // From a hall link (#account/history?.., hallLinks.js) or a filter link in the table.
    applyLink:function (params) {
        var state = this.linkState(params);
        if (!this.sameAsShown(state))
            this.showState(state);
    },

    showState:function (state) {
        this.filters = state.filters;
        this.itemStart = state.start;
        this.writeFilters(state.filters);
        this.loadHistory();
    },

    // A link that filters on one value from a row, keeping the other filters; the page starts over.
    filterLink:function (text, change) {
        var query = {};
        var f = this.filters || {};
        for (var i = 0; i < this.LINK_KEYS.length; i++) {
            var key = this.LINK_KEYS[i];
            if (f[key])
                query[key] = f[key] === true ? "true" : f[key];
        }
        if (change.opponent !== undefined)
            delete query.opponentExact;
        $.each(change, function (key, value) {
            if (value == null || value === "")
                delete query[key];
            else
                query[key] = value === true ? "true" : value;
        });
        var q = window.GempLinks ? GempLinks.buildQuery(query) : "";
        return $("<a class='gh-filter-link'></a>").text(text).attr("title", "Show only these games")
            .attr("href", q ? "#account/history?" + q : "#account/history");
    },

    loadFilterOptions:function () {
        var that = this;
        if (!this.communication.getGameHistoryFilters)
            return;
        this.communication.getGameHistoryFilters(function (json) {
            var formats = (json && json.formats) || [];
            var events = (json && json.events) || [];
            var current = that.formSelect.val();
            that.formSelect.find("option:not(:first)").remove();
            $.each(formats, function (i, format) {
                that.formSelect.append($("<option></option>").attr("value", format).text(format));
            });
            that.ensureOption(that.formSelect, current);
            that.formSelect.val(current);
            that.eventList.empty();
            that.eventList.append($("<option value='casual'></option>").text("Casual games"));
            $.each(events, function (i, event) {
                that.eventList.append($("<option></option>").attr("value", event));
            });
        }, {
            // the filters still work as free text without suggestions
            "0": function () {}, "400": function () {}, "401": function () {}, "500": function () {}
        });
    },

    // A data refresh: the same page and applied filters, fetched again without the "Loading…" line; a failure
    // keeps the page shown.  Suggestions for the filters are fetched again too (the typed values stay).
    refresh:function () {
        this.loadFilterOptions();
        this.loadHistory(true);
    },

    // quiet: a data refresh (see refresh)
    loadHistory:function (quiet) {
        var that = this;
        var requestNumber = ++this.requestNumber;
        if (quiet) {
            this.communication.getGameHistory(this.itemStart, this.pageSize,
                function (xml) {
                    if (requestNumber === that.requestNumber)
                        that.loadedGameHistory(xml);
                }, {
                    "0": function () {}, "400": function () {}, "401": function () {}, "500": function () {}
                }, this.filters);
            return;
        }
        if (window.GempLinks)
            GempLinks.setLink(this.currentLink());
        this.statusDiv.text("Loading…");
        this.setPagerEnabled(false);
        this.communication.getGameHistory(this.itemStart, this.pageSize,
            function (xml) {
                if (requestNumber !== that.requestNumber)
                    return;     // an older request answering after a newer one
                that.loadedGameHistory(xml);
            }, {
                "0": function () { that.showError("Could not reach the server. Check your connection and try again."); },
                "400": function (xhr) {
                    var message = xhr && xhr.getResponseHeader ? xhr.getResponseHeader("message") : null;
                    that.showError(message || "The server could not use those filters.");
                },
                "401": function () { that.showError("You are not logged in."); },
                "500": function () { that.showError("Could not load your game history. Please try again later."); }
            }, this.filters);
    },

    // A page that could not be loaded shows only the message: the rows and pager of the page before would not match
    // the filters and page the form and link now name.
    showError:function (text) {
        this.statusDiv.text(text);
        this.tableDiv.empty();
        this.matching = 0;
        this.setPagerEnabled(true);
        this.updatePager();
    },

    setPagerEnabled:function (enabled) {
        $.each(this.pagers, function (i, pager) {
            pager.element.find("button").button("option", "disabled", !enabled);
        });
    },

    updatePager:function () {
        var that = this;
        var pages = this.pageCount();
        var page = Math.floor(this.itemStart / this.pageSize) + 1;
        var atStart = this.itemStart <= 0;
        var atEnd = page >= pages;
        $.each(this.pagers, function (i, pager) {
            pager.first.button("option", "disabled", atStart);
            pager.prev.button("option", "disabled", atStart);
            pager.next.button("option", "disabled", atEnd);
            pager.last.button("option", "disabled", atEnd);
            pager.info.text("Page " + page + " of " + pages);
            pager.element.toggle(that.matching > that.pageSize);
        });
    },

    // One historyEntry element -> plain row object for the table
    entryToRow:function (entry, playerId) {
        var attr = function (name) { return entry.getAttribute(name); };
        var winner = attr("winner");
        var result = attr("result") || (winner === playerId ? "W" : "L");
        var opponent = attr("opponent") || (result === "W" ? attr("loser") : winner);
        // startTime / endTime are epoch milliseconds (GameHistoryRequestHandler)
        var startMs = attr("startTime") != null ? parseInt(attr("startTime"), 10) : null;
        var endMs = attr("endTime") != null ? parseInt(attr("endTime"), 10) : null;
        return {
            result: result,
            winReason: attr("winReason"),
            loseReason: attr("loseReason"),
            opponent: opponent,
            side: attr("mySide"),
            deck: attr("deckName"),
            format: attr("formatName"),
            event: attr("tournament"),
            leagueType: attr("leagueType"),
            startMs: startMs,
            endMs: endMs,
            duration: startMs != null && endMs != null ? endMs - startMs : null,
            recordingId: attr("gameRecordingId")
        };
    },

    loadedGameHistory:function (xml) {
        var root = xml && xml.documentElement;
        if (!root || root.tagName != 'gameHistory')
            return;

        var playerId = root.getAttribute("playerId");
        this.matching = parseInt(root.getAttribute("count"), 10) || 0;
        this.total = root.getAttribute("total") != null ? (parseInt(root.getAttribute("total"), 10) || 0) : this.matching;
        var start = root.getAttribute("start");
        if (start != null)
            this.itemStart = parseInt(start, 10) || 0;

        // a link to a page past the last one (the games have changed since it was written): show the last page
        var maxStart = (this.pageCount() - 1) * this.pageSize;
        if (this.matching > 0 && this.itemStart > maxStart) {
            this.itemStart = maxStart;
            this.loadHistory();
            return;
        }

        var rows = [];
        var entries = root.getElementsByTagName("historyEntry");
        for (var i = 0; i < entries.length; i++)
            rows.push(this.entryToRow(entries[i], playerId));

        this.tableDiv.empty().append(this.renderRows(rows, playerId));

        var filtered = this.hasFilters();
        if (this.matching === 0) {
            this.statusDiv.text(filtered ? "No games match these filters." : "You have not finished any games yet.");
        } else {
            var first = this.itemStart + 1;
            var last = this.itemStart + rows.length;
            var text = "Showing " + first + "–" + last + " of " + this.matching + (this.matching == 1 ? " game" : " games");
            if (filtered)
                text += " matching the filters (" + this.total + " in all)";
            this.statusDiv.text(text + ".");
        }
        this.setPagerEnabled(true);
        this.updatePager();
    },

    renderRows:function (rows, playerId) {
        var that = this;
        // Header text is centred; cell text is centred except in the name-like columns (gh-left: Format, Event,
        // My Deck), and My Deck wraps early so the other columns keep their room.
        var columns = [
            {key: "endMs", title: "Date", className: "gh-when",
                headerTitle: "The day the game ended, in server time (UTC)",
                render: function (row) {
                    var day = GameHistoryUI.serverDate(row);
                    var cell = $("<span></span>").text(day);
                    if (row.endMs != null)
                        cell.attr("title", "Ended " + formatTime(row.endMs) + " your time");
                    return cell;
                }},
            {key: "duration", title: "Length", className: "gh-duration",
                render: function (row) { return formatDuration(row.duration); }},
            {key: "result", title: "Result", className: "gh-result",
                render: function (row) {
                    var won = row.result === "W";
                    return $("<span></span>").addClass(won ? "gh-win" : "gh-loss").text(won ? "W" : "L")
                        .attr("title", won ? "Won" : "Lost");
                }},
            {key: "side", title: "Side", className: "gh-side",
                headerTitle: "The side you played",
                render: function (row) {
                    if (!row.side)
                        return "—";
                    var dark = row.side === "Dark";
                    return that.filterLink(dark ? "DS" : "LS", {side: row.side})
                        .addClass(dark ? "gh-side-dark" : "gh-side-light")
                        .attr("title", (dark ? "Dark Side" : "Light Side") + ": show only these games");
                }},
            // the loser's reason for both wins and losses: it names the timeout, concession, lost Force and so on
            {key: "loseReason", title: "Reason", className: "gh-reason",
                headerTitle: "Why the losing player lost: a timeout, a concession, losing all Force and so on",
                render: function (row) {
                    var cell = $("<span></span>").text(row.loseReason || "—");
                    if (row.winReason)
                        cell.attr("title", "Winner: " + row.winReason);
                    return cell;
                }},
            {key: "opponent", title: "Opponent", className: "gh-opponent",
                render: function (row) {
                    if (!row.opponent)
                        return "—";
                    return that.filterLink(row.opponent, {opponent: row.opponent, opponentExact: true});
                }},
            {key: "format", title: "Format", className: "gh-format gh-left",
                render: function (row) {
                    if (!row.format)
                        return "—";
                    return that.filterLink(row.format, {format: row.format});
                }},
            {key: "event", title: "Event", className: "gh-event gh-left",
                render: function (row) {
                    var casual = !row.event || /^Casual/.test(row.event);
                    var cell = $("<span></span>");
                    cell.append(that.filterLink(row.event || "Casual", {event: casual ? "casual" : row.event}));
                    if (!casual && row.leagueType)
                        cell.append(" ").append($("<a class='gh-event-link'></a>").attr("href", "#league-" + row.leagueType)
                            .attr("title", "Open this league on the Events tab").text("league ›"));
                    return cell;
                }},
            {key: "deck", title: "My Deck", className: "gh-deck gh-left",
                render: function (row) { return $("<div class='gh-deck-name'></div>").text(row.deck || "—"); }},
            {key: "recordingId", title: "Replay", className: "gh-replay",
                render: function (row) {
                    if (!row.recordingId)
                        return $("<i></i>").text("not stored");
                    return $("<a target='_blank' rel='noopener'></a>").text("Replay")
                        .attr("title", "Opens the replay in a new tab")
                        .attr("href", "game.html?replayId=" + playerId + "$" + row.recordingId);  // same link as before
                }}
        ];
        // Pages come from the server newest first; sorting inside one page would only mislead, so no sort headers.
        return renderTable(columns, rows, {
            sortable: false,
            className: "gameHistory",
            emptyText: this.hasFilters() ? "No games match these filters." : "No games yet."
        });
    }
});

/** The day a game ended, "yyyy-MM-dd" in server time (UTC), from its epoch ms. */
GameHistoryUI.serverDate = function (row) {
    if (row.endMs != null && !isNaN(row.endMs)) {
        var date = new Date(row.endMs);
        if (!isNaN(date.getTime()))
            return date.toISOString().substring(0, 10);
    }
    return "";
};
