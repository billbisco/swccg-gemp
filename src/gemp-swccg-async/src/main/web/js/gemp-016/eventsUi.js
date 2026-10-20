/**
 * The hall's Events tab (includes/events.html), adapted from the LOTR GEMP Events tab:
 *
 *   Calendar     a month of current leagues (from /league), each league a coloured run across its days; click one to
 *                see its details under the calendar.  Running tournaments are listed under the month.
 *   Leagues      the current leagues, one row each with a details drawer (LeagueResultsUI list mode)
 *   Tournaments  running tournaments and the ones finished in the last week, one row each with a details drawer
 *
 * Every event has a hall link (#league-<type>, #tournament-<id>) and a link to the player's games in it on the
 * History tab (hallLinks.js).  Only existing endpoints are used: /league, /league/<type>, /tournament,
 * /tournament/history and /tournament/<id>.  League dates are server days (UTC), as everywhere else in the hall.
 */

var GempEventDates = {
    MONTHS: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
        "November", "December"],
    DAYS: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],

    // 20261019 for a UTC date
    toInt: function (date) {
        return date.getUTCFullYear() * 10000 + (date.getUTCMonth() + 1) * 100 + date.getUTCDate();
    },

    today: function () {
        return this.toInt(new Date());
    },

    // a stable colour index per league, so a league keeps its colour from month to month
    colorIndex: function (key) {
        var hash = 0;
        for (var i = 0; i < key.length; i++)
            hash = (hash * 31 + key.charCodeAt(i)) & 0xffff;
        return hash % 6;
    }
};

var EventCalendarUI = Class.extend({
    comm: null,
    root: null,
    options: null,
    year: 0,
    month: 0,          // 0..11, UTC
    leagues: null,     // [{type, name, start, end, member}]
    tournaments: null, // [{id, name, format, stage, round}]
    selected: null,

    init: function (comm, root, options) {
        this.comm = comm;
        this.root = root;
        this.options = options || {};
        var now = new Date();
        this.year = now.getUTCFullYear();
        this.month = now.getUTCMonth();
        this.leagues = [];
        this.tournaments = [];
    },

    load: function () {
        var that = this;
        var pending = 2;
        var done = function () {
            if (--pending === 0)
                that.render();
        };
        this.comm.getLeagues(function (xml) {
            that.leagues = [];
            var root = xml.documentElement;
            var list = root != null && root.tagName == "leagues" ? root.getElementsByTagName("league") : [];
            for (var i = 0; i < list.length; i++) {
                that.leagues.push({
                    type: list[i].getAttribute("type"),
                    name: list[i].getAttribute("name"),
                    start: parseInt(list[i].getAttribute("start")),
                    end: parseInt(list[i].getAttribute("end")),
                    member: list[i].getAttribute("member") == "true"
                });
            }
            that.leagues.sort(function (a, b) {
                return a.start - b.start || (a.name < b.name ? -1 : 1);
            });
            done();
        }, {"0": done, "401": done, "500": done});
        this.comm.getLiveTournaments(function (xml) {
            that.tournaments = GempTournamentList.parse(xml);
            done();
        }, {"0": done, "401": done, "500": done});
    },

    shift: function (months) {
        var m = this.month + months;
        this.year += Math.floor(m / 12);
        this.month = ((m % 12) + 12) % 12;
        this.render();
    },

    goToday: function () {
        var now = new Date();
        this.year = now.getUTCFullYear();
        this.month = now.getUTCMonth();
        this.render();
    },

    render: function () {
        var that = this;
        this.root.empty();

        var header = $("<div class='cal-header'></div>");
        header.append($("<button type='button' class='cal-prev'>&lsaquo; Prev</button>").button().click(function () {
            that.shift(-1);
        }));
        header.append($("<span class='cal-title'></span>").text(GempEventDates.MONTHS[this.month] + " " + this.year));
        header.append($("<button type='button' class='cal-next'>Next &rsaquo;</button>").button().click(function () {
            that.shift(1);
        }));
        header.append($("<button type='button' class='cal-today'>Today</button>").button().click(function () {
            that.goToday();
        }));
        this.root.append(header);

        var table = $("<table class='cal-grid'></table>");
        var head = $("<tr></tr>");
        for (var d = 0; d < 7; d++)
            head.append($("<th></th>").text(GempEventDates.DAYS[d]));
        table.append(head);

        var first = new Date(Date.UTC(this.year, this.month, 1));
        var daysInMonth = new Date(Date.UTC(this.year, this.month + 1, 0)).getUTCDate();
        var lead = first.getUTCDay();
        var today = GempEventDates.today();
        var row = $("<tr></tr>");
        for (var i = 0; i < lead; i++)
            row.append("<td class='cal-empty'></td>");
        for (var day = 1; day <= daysInMonth; day++) {
            var dayInt = this.year * 10000 + (this.month + 1) * 100 + day;
            var cell = $("<td class='cal-day'></td>");
            if (dayInt === today)
                cell.addClass("cal-today-cell");
            cell.append($("<div class='cal-day-number'></div>").text(day));
            var weekStart = (lead + day - 1) % 7 === 0;
            for (var l = 0; l < this.leagues.length; l++) {
                var league = this.leagues[l];
                if (dayInt < league.start || dayInt > league.end)
                    continue;
                var chip = $("<div class='cal-event'></div>")
                    .addClass("cal-color-" + GempEventDates.colorIndex(league.type))
                    .attr("title", league.name + " (" + getDateString(String(league.start)) + " to "
                        + getDateString(String(league.end)) + ")")
                    .attr("data-league-type", league.type);
                if (dayInt === league.start)
                    chip.addClass("cal-event-start");
                if (dayInt === league.end)
                    chip.addClass("cal-event-end");
                // the name is written where a run starts: its first day, a week's first day, the month's first day
                if (dayInt === league.start || weekStart || day === 1)
                    chip.text(league.name);
                else
                    chip.html("&nbsp;");
                if (league.member)
                    chip.addClass("cal-event-member");
                if (this.selected != null && this.selected.kind == "league" && this.selected.id == league.type)
                    chip.addClass("cal-event-selected");
                chip.click((function (lg) {
                    return function () {
                        that.selectLeague(lg);
                    };
                })(league));
                cell.append(chip);
            }
            row.append(cell);
            if ((lead + day) % 7 === 0) {
                table.append(row);
                row = $("<tr></tr>");
            }
        }
        if (row.children().length > 0) {
            while (row.children().length < 7)
                row.append("<td class='cal-empty'></td>");
            table.append(row);
        }
        this.root.append(table);

        var running = $("<div class='cal-running'></div>");
        running.append($("<b></b>").text("Running tournaments: "));
        if (this.tournaments.length === 0)
            running.append($("<i></i>").text("none right now"));
        for (var t = 0; t < this.tournaments.length; t++) {
            running.append($("<a class='cal-tournament'></a>")
                .attr("href", "#tournament-" + this.tournaments[t].id)
                .text(this.tournaments[t].name + " (" + this.tournaments[t].format + ")"));
        }
        this.root.append(running);

        var legend = $("<div class='cal-legend'></div>")
            .text("All dates are server days (UTC). Leagues you have joined are shown in bold.");
        this.root.append(legend);
    },

    selectLeague: function (league) {
        var that = this;
        this.selected = {kind: "league", id: league.type};
        this.root.find(".cal-event").removeClass("cal-event-selected");
        this.root.find(".cal-event[data-league-type='" + league.type + "']").addClass("cal-event-selected");
        var details = this.options.details;
        if (!details)
            return;
        details.empty();
        var head = $("<div class='event-row-header'></div>");
        head.append($("<span class='leagueName'></span>").text(league.name));
        if (league.member)
            head.append($("<span class='play-league-member-badge'></span>").text("Joined"));
        head.append($("<span class='leagueDuration'></span>").text(getDateString(String(league.start)) + " to "
            + getDateString(String(league.end))));
        var go = $("<button type='button'>Go to League</button>").button().click(function () {
            if (typeof that.options.onLeague == "function")
                that.options.onLeague(league.type);
        });
        head.append(go);
        details.append(head);
        var body = $("<div class='leagueExtraInfo cal-details-body'></div>").attr("id", "calendarLeagueDetails");
        details.append(body);
        if (this.options.leagueUI) {
            this.comm.getLeague(league.type, function (xml) {
                that.options.leagueUI.loadedLeague(xml, "#calendarLeagueDetails", {
                    idPrefix: "calLeague-", showName: false, hideId: true, compactJoin: true, eventLinks: true
                });
            });
        }
    }
});

/** Tournament rows with a details drawer each (Events > Tournaments). */
var GempTournamentList = Class.extend({
    comm: null,
    list: null,
    rows: null,
    pendingShow: null,
    title: null,

    init: function (comm, list, title) {
        this.comm = comm;
        this.list = list;
        this.title = title;
    },

    render: function (tournaments, emptyText) {
        var that = this;
        this.list.empty();
        this.rows = {};
        if (this.title)
            this.list.append($("<h3 class='event-list-title'></h3>").text(this.title));
        if (tournaments.length === 0)
            this.list.append($("<i></i>").text(emptyText));
        for (var i = 0; i < tournaments.length; i++)
            this.addRow(tournaments[i]);
        if (this.pendingShow != null) {
            var id = this.pendingShow;
            this.pendingShow = null;
            this.show(id);
        }
    },

    addRow: function (t, prepend) {
        var that = this;
        var row = $("<div class='event-row'></div>").attr("data-tournament-id", t.id);
        var header = $("<div class='event-row-header'></div>");
        var button = $("<button type='button' class='leagueSeeDetails'>+ Details</button>").button();
        header.append(button);
        header.append($("<span class='leagueName'></span>").text(t.name));
        header.append($("<span class='leagueDuration'></span>").text(t.format + " · " + t.stage
            + (t.stage == "Playing games" ? " · round " + t.round : "")));
        row.append(header);
        var drawer = $("<div class='leagueExtraInfo' style='display:none;'></div>");
        row.append(drawer);
        if (prepend)
            this.list.prepend(row);
        else
            this.list.append(row);
        this.rows[t.id] = {row: row, button: button, drawer: drawer};
        button.click(function () {
            if (drawer.is(":visible")) {
                drawer.slideUp();
                button.button("option", "label", "+ Details");
                return;
            }
            that.comm.getTournament(t.id, function (xml) {
                that.renderDetails(xml, drawer);
                drawer.slideDown();
                button.button("option", "label", "- Details");
            });
        });
        return this.rows[t.id];
    },

    renderDetails: function (xml, drawer) {
        drawer.empty();
        var root = xml.documentElement;
        if (root == null || root.tagName != "tournament")
            return;
        var id = root.getAttribute("id");
        var name = root.getAttribute("name");
        var stage = root.getAttribute("stage");
        drawer.append($("<div></div>").html("<b>Format:</b> ").append(document.createTextNode(root.getAttribute("format"))));
        drawer.append($("<div></div>").html("<b>Collection:</b> ").append(document.createTextNode(root.getAttribute("collection"))));
        drawer.append($("<div></div>").html("<b>Stage:</b> ").append(document.createTextNode(stage)));
        var links = $("<div class='event-links'></div>");
        links.append($("<a class='event-link'></a>").attr("href", "#tournament-" + id).text("Link to this tournament"));
        links.append($("<a class='event-link'></a>").attr("href", "#account/history?" + GempLinks.buildQuery({event: name}))
            .text("My games in this tournament"));
        drawer.append(links);
        var standings = root.getElementsByTagName("tournamentStanding");
        if (standings.length > 0)
            drawer.append(TournamentResultsUI.prototype.createStandingsTable.call(null, standings, id, stage));
        else
            drawer.append($("<i></i>").text("No standings yet."));
    },

    // Opens the tournament's drawer; one not in this list (an older tournament) gets a row of its own on top.
    show: function (id) {
        var that = this;
        if (this.rows == null) {
            this.pendingShow = id;
            return;
        }
        var entry = this.rows[id];
        if (entry == null) {
            this.comm.getTournament(id, function (xml) {
                var root = xml.documentElement;
                if (root == null || root.tagName != "tournament")
                    return;
                var t = {id: id, name: root.getAttribute("name"), format: root.getAttribute("format"),
                    stage: root.getAttribute("stage"), round: root.getAttribute("round")};
                var added = that.addRow(t, true);
                that.open(added);
            }, {"404": function () {
                that.list.prepend($("<div class='event-missing'></div>").text("No tournament with that link was found."));
            }});
            return;
        }
        this.open(entry);
    },

    open: function (entry) {
        $(".event-row").removeClass("event-row-selected");
        entry.row.addClass("event-row-selected");
        if (!entry.drawer.is(":visible"))
            entry.button.click();
        var top = entry.row.offset() ? entry.row.offset().top : 0;
        $("html, body").scrollTop(Math.max(0, top - 80));
    }
});

// [{id, name, format, stage, round}] from a <tournaments> answer
GempTournamentList.parse = function (xml) {
    var result = [];
    var root = xml.documentElement;
    if (root == null || root.tagName != "tournaments")
        return result;
    var list = root.getElementsByTagName("tournament");
    for (var i = 0; i < list.length; i++) {
        result.push({
            id: list[i].getAttribute("id"),
            name: list[i].getAttribute("name"),
            format: list[i].getAttribute("format"),
            stage: list[i].getAttribute("stage"),
            round: list[i].getAttribute("round")
        });
    }
    return result;
};
