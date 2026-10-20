/**
 * Current (or recent) tournaments and one tournament's detail.  Ported from the LOTR GEMP hall
 * (js/gemp-022/tournamentResultsUi.js) and adapted to this server's tournament XML and standings.
 *
 *   new TournamentResultsUI(url, {list: $container})
 *       // list mode (the Events tab): every live tournament is a header row ([+ Details] name ...... format · stage)
 *       // with its own drawer underneath (eventDrawer.js).  Options:
 *       //   list:        container for the rows (required)
 *       //   autoLoad:    false = do not fetch the list from the constructor (default true)
 *       //   eventLinks:  true = a drawer carries the tournament's hall links (hallLinks.js)
 *
 * createTournamentRow(tournament, options) builds one row + drawer; the calendar preview (calendarUi.js) uses it too.
 * loadedTournament(xml, target, options) renders one tournament's detail into any container; the completed-events
 * browser (eventHistoryUi.js) uses it.
 *
 * refreshList() (list mode) re-fetches the list and updates it in place, as LeagueResultsUI.refreshList does.
 */
var TournamentResultsUI = Class.extend({
    communication:null,
    options:null,
    list:null,           // the rows' container
    rows:null,           // tournament id -> {element, header, button, drawer}
    listLoaded:false,
    listPending:null,

    init:function (url, options) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });

        this.options = options || {};
        this.list = this.options.list || null;
        this.rows = {};

        if (this.options.autoLoad !== false)
            this.loadLiveTournaments();
    },

    loadLiveTournaments:function (expandTournamentId) {
        var that = this;
        this.loadList(expandTournamentId === undefined ? null : function () {
            that.revealTournament(expandTournamentId);
        });
    },

    // One tournament's detail: format, collection, stage, round and the standings.  options: {rounds: true} starts
    // with a "Rounds: n" line (the list-mode rows do not carry it).
    loadedTournament:function (xml, targetDiv, options) {
        options = options || {};
        targetDiv = $(targetDiv);
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'tournament') {
            var tournament = root;

            var tournamentId = tournament.getAttribute("id");
            var tournamentName = tournament.getAttribute("name");
            var tournamentFormat = tournament.getAttribute("format");
            var tournamentCollection = tournament.getAttribute("collection");
            var tournamentRound = tournament.getAttribute("round");
            var tournamentStage = tournament.getAttribute("stage");

            var line = function (label, value) {
                return $("<div></div>").append($("<b></b>").text(label + ":")).append(document.createTextNode(" " + (value == null ? "" : value)));
            };
            // "round" is the round reached: the current one while games are played, the rounds played once over
            if (options.rounds && tournamentStage != "Playing games")
                targetDiv.append(line("Rounds", tournamentRound).addClass("tournamentRound"));
            targetDiv.append(line("Format", tournamentFormat).addClass("tournamentFormat"));
            targetDiv.append(line("Collection", tournamentCollection).addClass("tournamentCollection"));
            targetDiv.append(line("Stage", tournamentStage).addClass("tournamentStage"));
            if (tournamentStage == "Playing games")
                targetDiv.append(line("Round", tournamentRound).addClass("tournamentRound"));

            if (options.eventLinks === true && window.GempLinks) {
                var links = $("<div class='event-links'></div>");
                links.append($("<a class='event-link'></a>").attr("href", "#tournament-" + tournamentId).text("Link to this tournament"));
                links.append($("<a class='event-link'></a>")
                    .attr("href", "#account/history?" + GempLinks.buildQuery({event: tournamentName}))
                    .text("My games in this tournament"));
                targetDiv.append(links);
            }

            var standings = tournament.getElementsByTagName("tournamentStanding");
            if (standings.length > 0)
                targetDiv.append(this.createStandingsTable(standings, tournamentId, tournamentStage));
            else
                targetDiv.append($("<i></i>").text("No standings yet."));

            targetDiv.show();
        }
    },

    loadedTournaments:function (xml, expandTournamentId) {
        log(xml);
        this.renderTournamentRows(xml.documentElement);
        if (expandTournamentId !== undefined)
            this.revealTournament(expandTournamentId);
    },

    // ---- list mode: one header row + drawer per tournament ----

    // Fetches and renders the list.  Concurrent callers share one request; each `then` runs after the render (or,
    // when the request fails, after the error is shown).
    loadList:function (then) {
        var that = this;
        if (this.list == null)
            return;
        if (this.listPending != null) {
            if (then)
                this.listPending.push(then);
            return;
        }
        this.listPending = then ? [then] : [];
        this.communication.getLiveTournaments(
            function (xml) {
                var waiting = that.listPending || [];
                that.listPending = null;
                that.renderTournamentRows(xml.documentElement);
                for (var i = 0; i < waiting.length; i++)
                    waiting[i]();
            },
            EventDrawer.errorMap(function (message) {
                var waiting = that.listPending || [];
                that.listPending = null;
                that.list.empty().append($("<div class='event-drawer-error'></div>")
                    .text("Could not load the current tournaments. " + message));
                for (var i = 0; i < waiting.length; i++)
                    waiting[i]();
            }));
    },

    // Re-fetches the list and updates it in place: open drawers are re-rendered where they are, shut ones re-fetch
    // when next opened.  A failed refresh keeps what is shown.
    refreshList:function () {
        var that = this;
        if (this.list == null)
            return;
        if (!this.listLoaded) {
            this.loadList();
            return;
        }
        if (this.listPending != null)
            return;
        this.listPending = [];
        this.communication.getLiveTournaments(
            function (xml) {
                var waiting = that.listPending || [];
                that.listPending = null;
                that.updateTournamentRows(xml.documentElement);
                for (var i = 0; i < waiting.length; i++)
                    waiting[i]();
            },
            EventDrawer.errorMap(function () {
                var waiting = that.listPending || [];
                that.listPending = null;
                for (var i = 0; i < waiting.length; i++)
                    waiting[i]();
            }));
    },

    // {id, name, start, format, stage, round} per <tournament> of a <tournaments> answer.  The list from this
    // server carries no start time, so the row's right-hand text is the format and the stage instead.
    tournamentList:function (root) {
        var data = [];
        if (root == null || root.tagName != 'tournaments')
            return data;
        var tournaments = root.getElementsByTagName("tournament");
        for (var i = 0; i < tournaments.length; i++) {
            var t = tournaments[i];
            data.push({
                id: t.getAttribute("id"),
                name: t.getAttribute("name"),
                start: t.getAttribute("start"),
                format: t.getAttribute("format"),
                stage: t.getAttribute("stage"),
                round: t.getAttribute("round")
            });
        }
        return data;
    },

    updateTournamentRows:function (root) {
        var that = this;
        if (root == null || root.tagName != 'tournaments')
            return;
        this.rows = EventDrawer.updateRows(this.list, this.rows, this.tournamentList(root), {
            key: function (tournament) { return tournament.id; },
            create: function (tournament) { return that.createTournamentRow(tournament); },
            title: function (tournament) { return tournament.name; },
            date: function (tournament) { return TournamentResultsUI.rowDate(tournament); },
            emptyText: "There are no running tournaments at the moment."
        });
        this.listLoaded = true;
    },

    renderTournamentRows:function (root) {
        this.list.empty();
        this.rows = {};
        var tournaments = this.tournamentList(root);
        for (var i = 0; i < tournaments.length; i++) {
            var row = this.createTournamentRow(tournaments[i]);
            this.rows[tournaments[i].id] = row;
            this.list.append(row.element);
        }
        if (tournaments.length == 0)
            this.list.append($("<i></i>").text("There are no running tournaments at the moment."));
        this.listLoaded = true;
    },

    // One tournament's header row and drawer.
    //   tournament: {id, name, start, format, stage, round}   (start, when given, is the text shown on the right)
    //   options: {
    //       action:   undefined = the + Details / - Details toggle; {label, click} = a fixed button (the
    //                 calendar's "Go to Tournament")
    //       open:     true = born open
    //       fetch:    false = do not ask the server (a tournament that has not started); the fallback is shown
    //                 straight away
    //       fallback: function (content, message, status) rendering something instead of an error when the
    //                 tournament cannot be fetched
    //       extras:   more jQuery elements for the header
    //   }
    // Returns {element, header, button, drawer}.
    createTournamentRow:function (tournament, options) {
        var that = this;
        options = options || {};
        var id = tournament.id == null ? "" : String(tournament.id);
        var extras = [];
        // admins and league admins see the tournament id, as text a click selects whole
        var type = EventDrawer.userType();
        if (id !== "" && (type.includes("l") || type.includes("a"))) {
            extras.push($("<span class='league-id event-row-code event-row-select'></span>")
                .attr("title", "Tournament id - click to select")
                .text(id)
                .click(function () { EventDrawer.selectText(this); }));
        }
        if (options.extras)
            extras = extras.concat(options.extras);
        var row = EventDrawer.row({
            kind: "tournament",
            title: tournament.name,
            date: TournamentResultsUI.rowDate(tournament),
            dateTitle: tournament.start ? "Server time (UTC)" : null,
            extras: extras,
            action: options.action,
            open: options.open,
            load: function (drawer, content) {
                if (options.fetch === false && options.fallback) {
                    drawer.settle(content, function (c) {
                        c.empty();
                        options.fallback(c, null, null);
                    });
                    return;
                }
                that.loadTournamentDrawer(id, drawer, content, options.fallback);
            }
        });
        row.element.attr("data-tournament-id", id);
        return row;
    },

    loadTournamentDrawer:function (id, drawer, content, fallback) {
        var that = this;
        this.communication.getTournament(id,
            function (xml) {
                drawer.settle(content, function (c) {
                    c.empty();
                    try {
                        that.loadedTournament(xml, c, {rounds: true, eventLinks: that.options.eventLinks === true});
                    } catch (e) {
                        c.empty().append($("<div class='event-drawer-error'></div>").text("Could not display the tournament."));
                        return false;
                    }
                    if (c.children().length == 0)
                        c.append($("<i></i>").text("No details are available for this tournament."));
                    return true;
                });
            },
            EventDrawer.errorMap(function (message, status) {
                if (fallback) {
                    drawer.settle(content, function (c) {
                        c.empty();
                        fallback(c, message, status);
                    });
                } else {
                    drawer.fail(content, "Could not load the tournament (HTTP " + status + "). " + message);
                }
            }));
    },

    revealTournament:function (id) {
        var row = this.rows[id];
        this.list.find(".event-missing").remove();
        if (row == null) {
            this.list.prepend($("<div class='event-missing'></div>")
                .text("That tournament is not running any more (look for it under Completed tournaments), or the link is wrong."));
            return false;
        }
        row.drawer.open();
        EventDrawer.scrollIntoView(row.element);
        return true;
    },

    // Opens that tournament's row (loading the list first if needed) and scrolls it into view.  A tournament that is
    // not in the live list (finished) is said so above the list.
    showTournament:function (id) {
        var that = this;
        if (this.list == null)
            return;
        if (this.listLoaded && this.listPending == null && this.revealTournament(id))
            return;
        this.loadList(function () { that.revealTournament(id); });
    },

    // Standing, Player, Points, Games played, Opp. Win %, in two columns of rows.  A finished tournament's players
    // link to the deck they played.  Every value goes in as text.
    createStandingsTable:function (xmlstandings, tournamentId, tournamentStage) {
        var standingsTable = $("<table class='standings'></table>");
        var headers = ["Standing", "Player", "Points", "Games played", "Opp. Win %"];
        var headerRow = $("<tr></tr>");
        for (var h = 0; h < 2; h++) {
            if (h == 1)
                headerRow.append("<th></th>");
            for (var c = 0; c < headers.length; c++)
                headerRow.append($("<th></th>").text(headers[c]));
        }
        standingsTable.append(headerRow);

        var standings = [];
        for (var k = 0; k < xmlstandings.length; k++) {
            var xmlstanding = xmlstandings[k];
            standings.push({
                currentStanding: parseInt(xmlstanding.getAttribute("standing")),
                player: xmlstanding.getAttribute("player"),
                points: parseInt(xmlstanding.getAttribute("points")),
                gamesPlayed: parseInt(xmlstanding.getAttribute("gamesPlayed")),
                opponentWinPerc: xmlstanding.getAttribute("opponentWin")
            });
        }

        standings.sort(function (a, b) { return a.currentStanding - b.currentStanding; });

        var cells = function (standing) {
            var player = $("<td></td>");
            if (tournamentStage == "Finished") {
                player.append($("<a target='_blank'></a>")
                    .attr("href", "/gemp-swccg-server/tournament/" + encodeURIComponent(tournamentId) + "/deck/"
                        + encodeURIComponent(standing.player) + "/html")
                    .attr("title", "The deck " + standing.player + " played")
                    .text(standing.player));
            } else {
                player.text(standing.player == null ? "" : standing.player);
            }
            return [
                $("<td></td>").text(String(standing.currentStanding)),
                player,
                $("<td></td>").text(String(standing.points)),
                $("<td></td>").text(String(standing.gamesPlayed)),
                $("<td></td>").text(standing.opponentWinPerc == null ? "" : standing.opponentWinPerc)
            ];
        };

        var secondColumnBaseIndex = Math.ceil(standings.length / 2);
        var rows = [];
        for (var k = 0; k < secondColumnBaseIndex; k++) {
            var row = $("<tr></tr>").append(cells(standings[k]));
            rows.push(row);
            standingsTable.append(row);
        }
        for (var k = secondColumnBaseIndex; k < standings.length; k++)
            rows[k - secondColumnBaseIndex].append("<td></td>").append(cells(standings[k]));

        return standingsTable;
    }
});

// The right-hand text of a tournament row: its start, when known (the calendar preview), else what the live list
// knows: the format and the stage ("Open · Playing games, round 2").
TournamentResultsUI.rowDate = function (tournament) {
    if (tournament.start)
        return String(tournament.start);
    var parts = [];
    if (tournament.format)
        parts.push(String(tournament.format));
    if (tournament.stage) {
        var stage = String(tournament.stage);
        if (stage == "Playing games" && tournament.round)
            stage += ", round " + tournament.round;
        parts.push(stage);
    }
    return parts.join(" · ");
};
