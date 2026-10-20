/**
 * The hall's History tab (includes/history.html): the player's games a page at a time, filtered by format,
 * opponent, event, side, result and date.  Adapted from the LOTR GEMP "My Game History" page for SWCCG: each game
 * shows the side the player played (Dark / Light), how it ended, how long it lasted and a replay link when one was kept.
 *
 * The filters and page live in the hall link (#history?opponent=..&side=Dark&page=2, hallLinks.js), so a filtered
 * history can be shared or opened from another page.  The opponent, the event and the format in each row are links
 * that filter on that value; a league game's event also links to the league on the Events tab.
 *
 * Endpoints: GET /gameHistory (with the optional filters) and GET /gameHistory/filters.
 */
var GameHistoryPageUI = Class.extend({
    url: null,
    comm: null,
    root: null,
    pageSize: 25,
    params: null,
    loaded: null,
    requestSerial: 0,

    FILTER_KEYS: ["format", "opponent", "event", "side", "result", "from", "to"],

    init: function (url, root) {
        this.url = url;
        this.root = root;
        this.comm = new GempSwccgCommunication(url, function () {
        });
        this.params = {};
        this.buildForm();
    },

    // ---- requests (kept here so communication.js is untouched) ----

    fetchHistory: function (params, callback, errorMap) {
        var data = {start: params.start, count: this.pageSize, participantId: getUrlParam("participantId")};
        for (var i = 0; i < this.FILTER_KEYS.length; i++) {
            var key = this.FILTER_KEYS[i];
            if (params[key])
                data[key] = params[key];
        }
        $.ajax({
            type: "GET",
            url: this.url + "/gameHistory",
            cache: false,
            data: data,
            success: this.comm.deliveryCheck(callback),
            error: this.comm.errorCheck(errorMap),
            dataType: "xml"
        });
    },

    fetchFilterOptions: function (callback) {
        $.ajax({
            type: "GET",
            url: this.url + "/gameHistory/filters",
            cache: false,
            data: {participantId: getUrlParam("participantId")},
            success: callback,
            error: function () {
                // the filters still work as free text
            },
            dataType: "json"
        });
    },

    // ---- the form ----

    buildForm: function () {
        var that = this;
        var form = $("<form class='history-filters' autocomplete='off'></form>");
        var field = function (label, input) {
            return $("<label class='history-filter'></label>").append($("<span></span>").text(label)).append(input);
        };
        this.formatSelect = $("<select name='format'><option value=''>Any format</option></select>");
        this.opponentInput = $("<input type='text' name='opponent' maxlength='45' placeholder='Any opponent'>");
        this.eventSelect = $("<select name='event'><option value=''>Any game</option><option value='casual'>Casual games</option></select>");
        this.sideSelect = $("<select name='side'><option value=''>Either side</option><option value='Dark'>Dark Side</option><option value='Light'>Light Side</option></select>");
        this.resultSelect = $("<select name='result'><option value=''>Won or lost</option><option value='W'>Won</option><option value='L'>Lost</option></select>");
        this.fromInput = $("<input type='date' name='from'>");
        this.toInput = $("<input type='date' name='to'>");

        form.append(field("Format", this.formatSelect));
        form.append(field("Opponent", this.opponentInput));
        form.append(field("Event", this.eventSelect));
        form.append(field("Side", this.sideSelect));
        form.append(field("Result", this.resultSelect));
        form.append(field("From", this.fromInput));
        form.append(field("To", this.toInput));
        var apply = $("<button type='submit'>Filter</button>").button();
        var clear = $("<button type='button'>Clear</button>").button().click(function () {
            that.show({});
        });
        form.append($("<span class='history-filter-buttons'></span>").append(apply).append(clear));
        form.submit(function (e) {
            e.preventDefault();
            that.show(that.readForm());
            return false;
        });
        this.formatSelect.add(this.eventSelect).add(this.sideSelect).add(this.resultSelect).change(function () {
            that.show(that.readForm());
        });

        this.summary = $("<div class='history-summary'></div>");
        this.table = $("<div class='history-table'></div>");
        this.pager = $("<div class='history-pager'></div>");
        this.root.empty().append(form).append(this.summary).append(this.table).append(this.pager);

        this.fetchFilterOptions(function (json) {
            that.addOptions(that.formatSelect, json && json.formats);
            that.addOptions(that.eventSelect, json && json.events);
            that.writeForm(that.params);
        });
    },

    addOptions: function (select, values) {
        if (!values)
            return;
        for (var i = 0; i < values.length; i++) {
            if (values[i] && select.find("option").filter(function () {
                return $(this).val() === values[i];
            }).length === 0)
                select.append($("<option></option>").val(values[i]).text(values[i]));
        }
    },

    // an option that is not in the list (a link to an event the player has no games in yet) is added
    ensureOption: function (select, value) {
        if (value && select.find("option").filter(function () {
            return $(this).val() === value;
        }).length === 0)
            select.append($("<option></option>").val(value).text(value));
    },

    readForm: function () {
        return {
            format: this.formatSelect.val() || "",
            opponent: $.trim(this.opponentInput.val() || ""),
            event: this.eventSelect.val() || "",
            side: this.sideSelect.val() || "",
            result: this.resultSelect.val() || "",
            from: this.fromInput.val() || "",
            to: this.toInput.val() || ""
        };
    },

    writeForm: function (params) {
        this.ensureOption(this.formatSelect, params.format);
        this.ensureOption(this.eventSelect, params.event);
        this.formatSelect.val(params.format || "");
        this.opponentInput.val(params.opponent || "");
        this.eventSelect.val(params.event || "");
        this.sideSelect.val(params.side || "");
        this.resultSelect.val(params.result || "");
        this.fromInput.val(params.from || "");
        this.toInput.val(params.to || "");
    },

    // ---- links ----

    linkFor: function (params) {
        var query = {};
        for (var i = 0; i < this.FILTER_KEYS.length; i++) {
            var key = this.FILTER_KEYS[i];
            if (params[key])
                query[key] = params[key];
        }
        if (params.page && params.page > 1)
            query.page = params.page;
        var q = GempLinks.buildQuery(query);
        return q ? "#account/history?" + q : "#account/history";
    },

    sameAs: function (params) {
        var keys = this.FILTER_KEYS.concat(["page"]);
        for (var i = 0; i < keys.length; i++) {
            if (String(params[keys[i]] || "") !== String(this.params[keys[i]] || ""))
                return false;
        }
        return this.loaded != null;
    },

    // From a #history?.. link (hallLinks.js); the same filters as already shown do nothing.
    applyLink: function (params) {
        params.page = Math.max(1, parseInt(params.page) || 1);
        if (this.sameAs(params))
            return;
        this.show(params);
    },

    // ---- loading ----

    show: function (params) {
        var that = this;
        params = $.extend({}, params);
        params.page = Math.max(1, parseInt(params.page) || 1);
        this.params = params;
        this.writeForm(params);
        if (window.GempLinks)
            GempLinks.setLink(this.linkFor(params));
        var serial = ++this.requestSerial;
        this.summary.text("Loading…");
        var request = $.extend({}, params, {start: (params.page - 1) * this.pageSize});
        this.fetchHistory(request, function (xml) {
            if (serial !== that.requestSerial)
                return;
            that.loaded = params;
            that.render(xml);
        }, {
            "400": function (xhr) {
                if (serial === that.requestSerial)
                    that.summary.text((xhr && xhr.responseText) || "Those filters are not valid.");
            },
            "401": function () {
                that.summary.text("You are not logged in.");
            }
        });
    },

    render: function (xml) {
        var that = this;
        var root = xml.documentElement;
        if (root == null || root.tagName != "gameHistory")
            return;
        var me = root.getAttribute("playerId");
        var matching = parseInt(root.getAttribute("count")) || 0;
        var total = parseInt(root.getAttribute("total")) || matching;
        var start = parseInt(root.getAttribute("start")) || 0;
        var entries = root.getElementsByTagName("historyEntry");

        var filtered = false;
        for (var f = 0; f < this.FILTER_KEYS.length; f++)
            filtered = filtered || !!this.params[this.FILTER_KEYS[f]];

        if (matching === 0) {
            this.summary.text(filtered ? "No games match these filters (" + total + " games in all)." : "You have not played any games yet.");
        } else {
            this.summary.text("Games " + (start + 1) + "–" + (start + entries.length) + " of " + matching
                + (filtered ? " matching (" + total + " in all)" : "") + ", newest first. Times are your browser's local time.");
        }

        var table = $("<table class='gameHistory history-games'></table>");
        table.append("<tr><th>Ended</th><th>Result</th><th>Side</th><th>Opponent</th><th>Format</th><th>Event</th>"
            + "<th>My deck</th><th>How it ended</th><th>Length</th><th>Replay</th></tr>");
        for (var i = 0; i < entries.length; i++)
            table.append(this.renderRow(entries[i], me));
        this.table.empty();
        if (entries.length > 0)
            this.table.append(table);
        this.renderPager(matching);
    },

    filterLink: function (text, change) {
        var params = $.extend({}, this.params, change, {page: 1});
        return $("<a class='history-filter-link'></a>").attr("href", this.linkFor(params)).text(text)
            .attr("title", "Show only these games");
    },

    renderRow: function (entry, me) {
        var result = entry.getAttribute("result") || (entry.getAttribute("winner") == me ? "W" : "L");
        var opponent = entry.getAttribute("opponent") || (result == "W" ? entry.getAttribute("loser") : entry.getAttribute("winner"));
        var side = entry.getAttribute("mySide");
        var format = entry.getAttribute("formatName");
        var event = entry.getAttribute("tournament");
        var leagueType = entry.getAttribute("leagueType");
        var deck = entry.getAttribute("deckName");
        var startMs = parseInt(entry.getAttribute("startTime"));
        var endMs = parseInt(entry.getAttribute("endTime"));
        var recording = entry.getAttribute("gameRecordingId");

        var row = $("<tr></tr>").addClass(result == "W" ? "history-won" : "history-lost");
        row.append($("<td class='history-date'></td>").text(isNaN(endMs) ? "" : formatDate(new Date(endMs))));
        row.append($("<td></td>").append($("<span class='history-result'></span>")
            .addClass(result == "W" ? "history-result-w" : "history-result-l").text(result == "W" ? "Won" : "Lost")));
        var sideCell = $("<td></td>");
        if (side)
            sideCell.append(this.filterLink(side == "Dark" ? "DS" : "LS", {side: side})
                .addClass(side == "Dark" ? "history-side-dark" : "history-side-light"));
        row.append(sideCell);
        row.append($("<td></td>").append(this.filterLink(opponent, {opponent: opponent})));
        row.append($("<td></td>").append(format ? this.filterLink(format, {format: format}) : ""));

        var eventCell = $("<td></td>");
        if (!event || /^Casual/.test(event)) {
            eventCell.append(this.filterLink(event || "Casual", {event: "casual"}));
        } else {
            eventCell.append(this.filterLink(event, {event: event}));
            if (leagueType)
                eventCell.append(" ").append($("<a class='history-event-link'></a>").attr("href", "#league-" + leagueType)
                    .attr("title", "Open this league on the Events tab").text("league ›"));
        }
        row.append(eventCell);
        row.append($("<td></td>").text(deck || ""));

        var how = result == "W" ? entry.getAttribute("winReason") : entry.getAttribute("loseReason");
        row.append($("<td class='history-how'></td>").text(how || ""));
        row.append($("<td class='history-length'></td>").text(this.formatLength(startMs, endMs)));

        var replayCell = $("<td></td>");
        if (recording)
            replayCell.append($("<a target='_blank'>replay</a>").attr("href", "game.html?replayId=" + me + "$" + recording));
        else
            replayCell.append($("<i>not stored</i>"));
        row.append(replayCell);
        return row;
    },

    formatLength: function (startMs, endMs) {
        if (isNaN(startMs) || isNaN(endMs) || endMs < startMs)
            return "";
        var minutes = Math.round((endMs - startMs) / 60000);
        if (minutes < 60)
            return minutes + " min";
        return Math.floor(minutes / 60) + " h " + formatToTwoDigits(minutes % 60) + " min";
    },

    renderPager: function (matching) {
        var that = this;
        this.pager.empty();
        var pages = Math.max(1, Math.ceil(matching / this.pageSize));
        if (pages <= 1)
            return;
        var page = this.params.page;
        var go = function (label, target, enabled) {
            var b = $("<button type='button'></button>").text(label).button({disabled: !enabled});
            b.click(function () {
                that.show($.extend({}, that.params, {page: target}));
                var top = that.root.offset() ? that.root.offset().top : 0;
                $("html, body").scrollTop(Math.max(0, top - 60));
            });
            return b;
        };
        this.pager.append(go("« First", 1, page > 1));
        this.pager.append(go("‹ Newer", page - 1, page > 1));
        this.pager.append($("<span class='history-page-number'></span>").text("Page " + page + " of " + pages));
        this.pager.append(go("Older ›", page + 1, page < pages));
        this.pager.append(go("Last »", pages, page < pages));
    }
});
