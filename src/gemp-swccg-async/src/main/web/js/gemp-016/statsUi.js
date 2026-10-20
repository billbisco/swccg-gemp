// Server Info › Server Stats.  Ported from the LOTR hall's statsUi.js: the date / period fields and the stats'
// headings are in the page (includes/info.html), this fills them in from GET /stats (ServerStatsRequestHandler, XML:
// active players, games count and the casual games per format for the period).
var StatsUI = Class.extend({
    communication:null,
    paramsDiv:null,
    statsDiv:null,
    shown:null,   // {startDay, period} of the stats on screen

    init:function (url, paramControl, statControl) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });

        this.paramsDiv = paramControl;
        this.statsDiv = statControl;

        // the period shown first is the month up to today, not the month ahead of it
        var now = new Date();
        var d = now.getDate();
        now.setMonth(now.getMonth() - 1);
        if (now.getDate() != d) {
            now.setDate(0);
        }
        var nowStr = now.getFullYear() + "-" + StatsUI.twoDigits(1 + now.getMonth()) + "-" + StatsUI.twoDigits(now.getDate());

        $(".startDay", this.paramsDiv).val(nowStr);

        var that = this;

        $(".getStats", this.paramsDiv).click(
            function () {
                var startDay = $.trim($(".startDay", that.paramsDiv).val());
                var period = $("option:selected", $(".period", that.paramsDiv)).prop("value");
                if (!/^\d{4}-\d{1,2}-\d{1,2}$/.test(startDay)) {
                    that.status("Enter the start date as yyyy-mm-dd.");
                    return false;
                }
                that.status("Loading…");
                that.communication.getStats(startDay, period, function (xml) {
                    that.shown = {startDay: startDay, period: period};
                    that.status("");
                    that.loadedStats(xml);
                }, {
                    "0":function () { that.status("Could not reach the server."); },
                    "400":function () { that.status("That date is not valid; enter it as yyyy-mm-dd."); },
                    "401":function () { that.status("You are not logged in."); },
                    "500":function () { that.status("The stats could not be loaded. Try again later."); }
                });
                return false;
            });
    },

    status:function (text) {
        $(".stats-status", this.paramsDiv).text(text);
    },

    // A data refresh (Server Info calls it when the viewer comes back): the stats on screen are fetched again for the
    // same dates; the date / period fields are left as the viewer has them.  A failure keeps what is shown.
    refresh:function () {
        var that = this;
        if (this.shown == null)
            return;
        this.communication.getStats(this.shown.startDay, this.shown.period, function (xml) {
            that.loadedStats(xml);
        }, {"0": function () {}, "400": function () {}, "401": function () {}, "500": function () {}});
    },

    loadedStats:function (xml) {
        log(xml);
        var root = xml.documentElement;
        if (root.tagName != 'stats')
            return;

        var statsDiv = this.statsDiv;
        var text = function (id, value) {
            $("#" + id, statsDiv).text(value == null ? "" : value);
        };
        text("startDateSpan", root.getAttribute("start"));
        text("endDateSpan", root.getAttribute("end"));
        text("activePlayersStat", root.getAttribute("activePlayers"));
        text("gamesCountStat", root.getAttribute("gamesCount"));

        var formatStats = root.getElementsByTagName("formatStat");
        var container = $("#casualStatsTable", this.statsDiv).empty();
        if (formatStats.length == 0) {
            container.append($("<p class='stats-empty'></p>").text("No casual games in this period."));
            return;
        }
        var table = $("<table class='tables stats-format-table'></table>");
        table.append("<tr><th>Format name</th><th># of games</th><th>% of casual</th></tr>");
        for (var i = 0; i < formatStats.length; i++) {
            var formatStat = formatStats[i];
            var row = $("<tr></tr>");
            row.append($("<td></td>").text(formatStat.getAttribute("format")));
            row.append($("<td class='stats-number'></td>").text(formatStat.getAttribute("count")));
            row.append($("<td class='stats-number'></td>").text(formatStat.getAttribute("perc")));
            table.append(row);
        }
        container.append(table);
    }
});

StatsUI.twoDigits = function (n) {
    return (n < 10 ? "0" : "") + n;
};
