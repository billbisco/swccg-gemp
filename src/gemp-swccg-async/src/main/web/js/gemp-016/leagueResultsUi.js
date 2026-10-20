/**
 * Current leagues and one league's full detail.  Ported from the LOTR GEMP hall (js/gemp-022/leagueResultsUi.js)
 * and adapted to this server's league XML (type codes, yyyyMMdd days, invitation-only registration, locked decks,
 * solo drafts) and standings (with W / L columns).
 *
 *   new LeagueResultsUI(url, joinCallback, {list: $container})
 *       // list mode (the Events tab, and Play > League): every league is a header row ([+ Details] name ...... dates)
 *       // with its own drawer underneath (eventDrawer.js) holding the full detail, rendered where the viewer clicked.
 *       // Several drawers may be open at once.  Options:
 *       //   list:        container for the rows (required)
 *       //   autoLoad:    false = do not fetch the list from the constructor (default true)
 *       //   eventLinks:  true = a drawer carries the league's hall links (its own #league-<type> link and the
 *       //                viewer's games in it on My Game History; hallLinks.js)
 *       //   onJoinError: function (leagueCode, message, status) reporting a failed join (default: alert on 409)
 *
 * joinCallback(leagueCode), if given, runs after the viewer joins a league from any of this object's renders.  The
 * joined league's drawer (and any other open drawer showing that league, e.g. the calendar preview) is re-rendered in
 * place, still open.
 *
 * createLeagueRow(league, options) builds one row + drawer; the calendar preview (calendarUi.js) uses it too.
 * loadedLeague(xml, target, options) renders one league's detail into any container; the completed-events browser
 * (eventHistoryUi.js) uses it with {membership: false}.
 *
 * refreshList() re-fetches the list and updates it in place: rows keep their element and drawer, open drawers are
 * re-rendered where they are (keeping the results tab the viewer had chosen), shut ones re-fetch when next opened,
 * new leagues get rows and finished ones lose theirs.  The Events tab calls it when the viewer comes back.
 */
var LeagueResultsUI = Class.extend({
    communication:null,
    questionDialog:null,
    formatDialog:null,
    joinCallback:null,
    options:null,
    list:null,           // the rows' container
    rows:null,           // league type -> {element, header, button, drawer}
    listLoaded:false,
    listPending:null,    // callbacks waiting on the in-flight list request
    drawers:null,        // league type -> [EventDrawer] rendered by this object (list rows and calendar previews)

    init:function (url, joinCallback, options) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });

        // zIndex above .play-flow (10000): the Join Yes/No would otherwise sit behind the Play overlay
        this.questionDialog = $("<div></div>")
            .dialog({
                autoOpen:false,
                closeOnEscape:true,
                resizable:false,
                modal:true,
                title:"League operation",
                zIndex:11050
            });

        this.formatDialog = $("<div></div>")
            .dialog({
                autoOpen:false,
                closeOnEscape:true,
                resizable:false,
                modal:true,
                title:"Format description",
                zIndex:11050
            });

        this.joinCallback = joinCallback || null;
        this.options = options || {};
        this.list = this.options.list || null;
        this.rows = {};
        this.drawers = {};

        if (this.options.autoLoad !== false)
            this.loadResults();
    },

    // then: runs once the list has been (re)rendered
    loadResults:function (then) {
        this.loadList(then);
    },

    // Renders one league's full detail (cost, membership, registration, per-serie format / collection and the
    // results tabs) into "target".
    //   target:  a jQuery container (or a selector)
    //   options: {
    //       idPrefix:   prefix for the tab panel ids (<prefix>overall, <prefix>matches, <prefix>serie<j>).  Defaults
    //                   to "league".  Any render on a page with another league must pass its own prefix so two
    //                   leagues on one page do not produce duplicate ids.
    //       membership: false hides the membership / join / draft block (used for finished leagues, whatever the
    //                   server says about "joinable").  Defaults to true.
    //       showName:   false leaves out the big league name (a drawer's header row already shows it).  Defaults to true.
    //       eventLinks: true adds the league's hall links (see the header comment).
    //       onJoined:   function (leagueCode) run after a successful join instead of the default, which reloads the
    //                   list.  joinCallback runs either way.
    //   }
    loadedLeague:function (xml, target, options) {
        var that = this;
        target = $(target);
        options = options || {};
        var idPrefix = (options.idPrefix != null) ? String(options.idPrefix) : "league";
        var showMembership = options.membership !== false;
        var onJoined = (typeof options.onJoined == "function") ? options.onJoined : null;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'league') {
            target.html("");

            var league = root;

            var leagueName = league.getAttribute("name");
            var leagueType = league.getAttribute("type");
            var cost = parseInt(league.getAttribute("cost"));
            var start = league.getAttribute("start");
            var member = league.getAttribute("member");
            var joinable = league.getAttribute("joinable");
            var isSoloDraft = league.getAttribute("isSoloDraft");
            var draftable = league.getAttribute("draftable");
            var invitationOnly = league.getAttribute("invitationOnly");
            var registrationInfo = league.getAttribute("registrationInfo");
            var lockedDeckType = league.getAttribute("lockedDeckType");
            var costStr = formatPrice(cost);

            if (options.showName !== false)
                target.append($("<div class='leagueName'></div>").text(leagueName));

            var joinBlock = $("<div class='league-join-block league-join-block-compact'></div>");
            if (invitationOnly == "true") {
                // registrationInfo is admin-written text; a bare starwarsccg.org address arrives as a link
                if (registrationInfo != "" && registrationInfo != "null")
                    joinBlock.append($("<div></div>").append("<b>Registration info:</b> ").append(LeagueResultsUI.registrationInfo(registrationInfo)));
                else
                    joinBlock.append("<div>Registration for this league is by invitation only.</div>");
            } else {
                joinBlock.append("<div class='leagueCost'><b>Cost:</b> " + costStr + "</div>");
            }

            if (!showMembership) {
                // finished league (event history): no join / draft controls
            } else if (member == "true") {
                var memberDiv = $("<div class='leagueMembership'>You are already a member of this league. </div>");
                if (lockedDeckType === "when_first_played") {
                    memberDiv.append($("<div>Decks are locked-in automatically after first use and cannot be replaced.</div>"));
                    memberDiv.append($("<div></div>").append($("<a target='_blank'>View your locked-in decks here.</a>")
                        .attr("href", "/gemp-swccg-server/league/deck/html?leagueType=" + encodeURIComponent(leagueType))));
                }
                if (draftable == "true") {
                    var draftBut = $("<button type='button'>--> Go to draft <--</button>").button();
                    draftBut.click((function (leagueCode) {
                        return function () {
                            location.href = "soloDraft.html?leagueType=" + encodeURIComponent(leagueCode);
                        };
                    })(leagueType));
                    memberDiv.append(draftBut);
                } else if (isSoloDraft == "true") {
                    // a solo draft league the viewer joined before it started
                    memberDiv.append($("<button type='button' disabled='disabled' class='draft-not-available'></button>")
                        .text("Draft available " + getDateString(start)));
                }
                joinBlock.append(memberDiv);
            } else if (joinable == "true" && invitationOnly != "true") {
                var joinBut = $("<button type='button' class='league-join-button'>Join league</button>").button();

                var joinFunc = (function (leagueCode, costString) {
                    return function () {
                        that.displayBuyAction("Do you want to join the league by paying " + costString + "?",
                            function () {
                                that.communication.joinLeague(leagueCode, function () {
                                    if (onJoined != null)
                                        onJoined(leagueCode);
                                    else
                                        that.refreshList();
                                    if (that.joinCallback != null)
                                        that.joinCallback(leagueCode);
                                }, that.joinErrorMap(leagueCode));
                            });
                    };
                })(leagueType, costStr);

                joinBut.click(joinFunc);
                var joinDiv = $("<div class='leagueMembership'></div>");
                joinDiv.append($("<span class='league-membership-label'>You're not a member of this league. </span>"));
                joinDiv.append(joinBut);
                joinBlock.append(joinDiv);
            } else if (joinable == "true" && invitationOnly == "true") {
                joinBlock.append($("<div class='leagueMembership'>You're not a member of this league. </div>"));
            }
            target.append(joinBlock);

            if (options.eventLinks === true && window.GempLinks) {
                var links = $("<div class='event-links'></div>");
                links.append($("<a class='event-link'></a>").attr("href", "#league-" + leagueType).text("Link to this league"));
                links.append($("<a class='event-link'></a>")
                    .attr("href", "#account/history?" + GempLinks.buildQuery({event: leagueName}))
                    .text("My games in this league"));
                target.append(links);
            }

            var tabDiv = $("<div width='100%'></div>");
            var tabNavigation = $("<ul></ul>");
            tabDiv.append(tabNavigation);

            // Overall tab
            var tabContent = $("<div></div>").attr("id", idPrefix + "overall");

            var standings = league.getElementsByTagName("leagueStanding");
            if (standings.length > 0)
                tabContent.append(this.createStandingsTable(standings));
            tabDiv.append(tabContent);

            tabNavigation.append($("<li></li>").append($("<a>Overall results</a>").attr("href", "#" + idPrefix + "overall")));
            tabNavigation.append($("<li></li>").append($("<a>Your league matches</a>").attr("href", "#" + idPrefix + "matches")));

            var matchResults = $("<div></div>").attr("id", idPrefix + "matches");
            tabDiv.append(matchResults);

            var series = league.getElementsByTagName("serie");
            for (var j = 0; j < series.length; j++) {
                var serie = series[j];
                matchResults.append("<div>Serie " + (j + 1) + "</div>");
                var matchGroup = $("<table class='standings'><tr><th>Winner</th><th>Loser</th></tr></table>");
                var matches = serie.getElementsByTagName("match");
                for (var k = 0; k < matches.length; k++) {
                    var match = matches[k];
                    matchGroup.append($("<tr></tr>")
                        .append($("<td></td>").text(match.getAttribute("winner")))
                        .append($("<td></td>").text(match.getAttribute("loser"))));
                }

                matchResults.append(matchGroup);

                var serieContent = $("<div></div>").attr("id", idPrefix + "serie" + j);

                var serieName = serie.getAttribute("type");
                var serieStart = serie.getAttribute("start");
                var serieEnd = serie.getAttribute("end");
                var maxMatches = serie.getAttribute("maxMatches");
                var formatType = serie.getAttribute("formatType");
                var format = serie.getAttribute("format");
                var collection = serie.getAttribute("collection");
                var limited = serie.getAttribute("limited");

                target.append($("<div class='serieName'></div>").text(serieName + " - " + getDateString(serieStart) + " to " + getDateString(serieEnd)));

                var formatName = $("<span class='clickableFormat'></span>").text(((limited == "true") ? "" : "Constructed ") + format);
                var formatDiv = $("<div><b>Format:</b> </div>");
                formatDiv.append(formatName);
                formatName.click(
                    (function (ft) {
                        return function () {
                            that.formatDialog.html("");
                            that.formatDialog.dialog("option", "zIndex", 11050);
                            that.formatDialog.dialog("open");
                            try {
                                that.formatDialog.dialog("widget").css("z-index", 11050);
                                $(".ui-widget-overlay").last().css("z-index", 11040);
                            } catch (ignored) {}
                            that.communication.getFormat(ft,
                                function (html) {
                                    that.formatDialog.html(html);
                                });
                        };
                    })(formatType));
                target.append(formatDiv);
                target.append($("<div><b>Collection:</b> </div>").append(document.createTextNode(collection == null ? "" : collection)));

                serieContent.append("<div>Maximum ranked matches in serie: " + parseInt(maxMatches) + "</div>");

                var serieStandings = serie.getElementsByTagName("standing");
                if (serieStandings.length > 0)
                    serieContent.append(this.createStandingsTable(serieStandings));
                tabDiv.append(serieContent);

                tabNavigation.append($("<li></li>").append($("<a></a>").attr("href", "#" + idPrefix + "serie" + j).text("Serie " + (j + 1))));
            }

            tabDiv.tabs();

            target.append(tabDiv);
        }
    },

    loadedLeagueResults:function (xml) {
        log(xml);
        this.renderLeagueRows(xml.documentElement);
    },

    // ---- list mode: one header row + drawer per league ----

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
        this.communication.getLeagues(
            function (xml) {
                var waiting = that.listPending || [];
                that.listPending = null;
                that.loadedLeagueResults(xml);
                for (var i = 0; i < waiting.length; i++)
                    waiting[i]();
            },
            EventDrawer.errorMap(function (message) {
                var waiting = that.listPending || [];
                that.listPending = null;
                that.list.empty().append($("<div class='event-drawer-error'></div>")
                    .text("Could not load the current leagues. " + message));
                for (var i = 0; i < waiting.length; i++)
                    waiting[i]();
            }));
    },

    // Re-fetches the list and updates it in place (see the header comment).  A failed refresh keeps what is shown.
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
        this.communication.getLeagues(
            function (xml) {
                var waiting = that.listPending || [];
                that.listPending = null;
                that.updateLeagueRows(xml.documentElement);
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

    // {code, name, start, end, member} per <league> of a <leagues> answer; days as yyyy-MM-dd
    leagueList:function (root) {
        var data = [];
        if (root == null || root.tagName != 'leagues')
            return data;
        var leagues = root.getElementsByTagName("league");
        for (var i = 0; i < leagues.length; i++) {
            data.push({
                code: leagues[i].getAttribute("type"),
                name: leagues[i].getAttribute("name"),
                start: LeagueResultsUI.day(leagues[i].getAttribute("start")),
                end: LeagueResultsUI.day(leagues[i].getAttribute("end")),
                member: leagues[i].getAttribute("member") == "true"
            });
        }
        return data;
    },

    updateLeagueRows:function (root) {
        var that = this;
        if (root == null || root.tagName != 'leagues')
            return;
        var leagues = this.leagueList(root);
        this.rows = EventDrawer.updateRows(this.list, this.rows, leagues, {
            key: function (league) { return league.code; },
            create: function (league) { return that.createLeagueRow(league); },
            title: function (league) { return league.name; },
            date: function (league) { return LeagueResultsUI.dateRange(league.start, league.end); },
            emptyText: "There are no current leagues at the moment."
        });
        // the "Joined" marks of the rows kept follow the fresh list
        for (var i = 0; i < leagues.length; i++) {
            var row = this.rows[leagues[i].code];
            if (row != null)
                LeagueResultsUI.markJoined(row.element, leagues[i].member);
        }
        this.listLoaded = true;
    },

    renderLeagueRows:function (root) {
        this.list.empty();
        this.rows = {};
        var leagues = this.leagueList(root);
        for (var i = 0; i < leagues.length; i++) {
            var row = this.createLeagueRow(leagues[i]);
            this.rows[leagues[i].code] = row;
            this.list.append(row.element);
        }
        if (leagues.length == 0)
            this.list.append($("<i></i>").text("There are no current leagues at the moment."));
        this.listLoaded = true;
    },

    // One league's header row and drawer.
    //   league:  {code, name, start, end, member}   (days as yyyy-MM-dd)
    //   options: {
    //       action:   undefined = the + Details / - Details toggle; {label, click} = a fixed button (the
    //                 calendar's "Go to League"), in which case the drawer is meant to stay open
    //       open:     true = born open
    //       fallback: function (content, message, status) rendering something instead of an error when the league
    //                 cannot be fetched (the calendar's own facts)
    //       extras:   more jQuery elements for the header
    //   }
    // Returns {element, header, button, drawer}.
    createLeagueRow:function (league, options) {
        var that = this;
        options = options || {};
        var code = league.code == null ? "" : String(league.code);
        var extras = [];
        // league admins see the league code (as before), as text a click selects whole
        if (code !== "" && EventDrawer.userType().includes("l")) {
            extras.push($("<span class='league-id event-row-code event-row-select'></span>")
                .attr("title", "League code - click to select")
                .text(code)
                .click(function () { EventDrawer.selectText(this); }));
        }
        if (options.extras)
            extras = extras.concat(options.extras);
        var row = EventDrawer.row({
            kind: "league",
            title: league.name,
            date: LeagueResultsUI.dateRange(league.start, league.end),
            dateTitle: "Server time (UTC)",
            extras: extras,
            action: options.action,
            open: options.open,
            load: function (drawer, content) {
                that.loadLeagueDrawer(code, drawer, content, options.fallback);
            }
        });
        row.element.attr("data-league-type", code);
        LeagueResultsUI.markJoined(row.element, league.member === true);
        return row;
    },

    loadLeagueDrawer:function (code, drawer, content, fallback) {
        var that = this;
        this.rememberDrawer(code, drawer);
        this.communication.getLeague(code,
            function (xml) {
                drawer.settle(content, function (c) {
                    return that.renderLeagueDrawer(xml, code, drawer, c);
                });
            },
            EventDrawer.errorMap(function (message, status) {
                if (fallback) {
                    drawer.settle(content, function (c) {
                        c.empty();
                        fallback(c, message, status);
                    });
                } else {
                    drawer.fail(content, "Could not load the league (HTTP " + status + "). " + message);
                }
            }));
    },

    // Returns false when the league could not be displayed.
    renderLeagueDrawer:function (xml, code, drawer, content) {
        var that = this;
        // a re-render (refresh, join) keeps the results tab the viewer had chosen
        var tabs = content.find(".ui-tabs").first();
        var activeTab = tabs.length > 0 && tabs.tabs("instance") ? tabs.tabs("option", "active") : null;
        content.empty();
        try {
            this.loadedLeague(xml, content, {
                idPrefix: "leagueDrawer" + (++LeagueResultsUI.drawerRenders) + "-",
                showName: false,
                eventLinks: this.options.eventLinks === true,
                onJoined: function () { that.leagueJoined(code); }
            });
        } catch (e) {
            content.empty().append($("<div class='event-drawer-error'></div>").text("Could not display the league."));
            return false;
        }
        if (content.children().length == 0)
            content.append($("<i></i>").text("No details are available for this league."));
        if (typeof activeTab == "number" && activeTab > 0) {
            var newTabs = content.find(".ui-tabs").first();
            if (newTabs.length > 0 && activeTab < newTabs.find("> ul > li").length)
                newTabs.tabs("option", "active", activeTab);
        }
        // the row's "Joined" mark follows the detail just fetched
        LeagueResultsUI.markJoined(content.closest(".event-row"), xml.documentElement.getAttribute("member") == "true");
        return true;
    },

    rememberDrawer:function (code, drawer) {
        var list = this.drawers[code] || (this.drawers[code] = []);
        if ($.inArray(drawer, list) < 0)
            list.push(drawer);
    },

    // After a join: every drawer this object rendered for that league that is still on the page is re-rendered in
    // place (membership, draft button, standings), keeping its open / closed state.
    leagueJoined:function (code) {
        var list = this.drawers[code] || [];
        var live = [];
        for (var i = 0; i < list.length; i++) {
            var drawer = list[i];
            if (drawer.content != null && $.contains(document.documentElement, drawer.content[0])) {
                live.push(drawer);
                drawer.refresh();
            }
        }
        this.drawers[code] = live;
    },

    // Opens that league's row (loading the list first if needed) and scrolls it into view.  A league that is not
    // in the list (finished, or a wrong link) is said so above the list.
    showLeague:function (code) {
        var that = this;
        if (this.list == null)
            return;
        var reveal = function () {
            var row = that.rows[code];
            that.list.find(".event-missing").remove();
            if (row == null) {
                that.list.prepend($("<div class='event-missing'></div>")
                    .text("That league is not running any more, or the link is wrong."));
                return false;
            }
            row.drawer.open();
            EventDrawer.scrollIntoView(row.element);
            return true;
        };
        if (this.listLoaded && this.listPending == null && reveal())
            return;
        this.loadList(reveal);
    },

    // options.onJoinError(leagueCode, message, status), if given, reports every failed join (the Play > League
    // panel shows it inline); otherwise only "not enough funds" is reported, with an alert, as before.
    joinErrorMap:function (leagueCode) {
        var report = this.options.onJoinError;
        if (typeof report != "function") {
            return {
                "409":function () {
                    alert("You don't have enough funds to join this league.");
                }
            };
        }
        return EventDrawer.errorMap(function (message, status) {
            report(leagueCode, status == 409 ? "You don't have enough funds to join this league." : message, status);
        });
    },

    displayBuyAction:function (text, yesFunc) {
        var that = this;
        this.questionDialog.html("");
        this.questionDialog.html("<div style='scroll: auto'></div>");
        var questionDiv = $("<div>" + text + "</div>");
        questionDiv.append("<br/>");
        questionDiv.append($("<button>Yes</button>").button().click(
            function () {
                that.questionDialog.dialog("close");
                yesFunc();
            }));
        questionDiv.append($("<button>No</button>").button().click(
            function () {
                that.questionDialog.dialog("close");
            }));
        this.questionDialog.append(questionDiv);

        var windowWidth = $(window).width();
        var windowHeight = $(window).height();

        var horSpace = 250;
        var vertSpace = 120;

        this.questionDialog.dialog({
            width:Math.min(horSpace, windowWidth),
            height:Math.min(vertSpace, windowHeight),
            zIndex:11050
        });
        // the overlay and the dialog stack above the Play overlay (z-index 10000)
        this.questionDialog.dialog("open");
        try {
            this.questionDialog.dialog("widget").css("z-index", 11050);
            $(".ui-widget-overlay").last().css("z-index", 11040);
        } catch (ignored) {}
    },

    // Standing, Player, Points, Games played, W, L, Opp. Win %, in two columns of rows.  Every value goes in as text.
    createStandingsTable:function (xmlstandings) {
        var standingsTable = $("<table class='standings'></table>");
        var headers = ["Standing", "Player", "Points", "Games played", "W", "L", "Opp. Win %"];
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
            var points = parseInt(xmlstanding.getAttribute("points"));
            var gamesPlayed = parseInt(xmlstanding.getAttribute("gamesPlayed"));
            // 3 points a win, 1 a loss (LeagueService): wins = (points - games) / 2
            var wins = (points - gamesPlayed) / 2;
            standings.push({
                currentStanding: parseInt(xmlstanding.getAttribute("standing")),
                player: xmlstanding.getAttribute("player"),
                points: points,
                gamesPlayed: gamesPlayed,
                wins: wins,
                losses: gamesPlayed - wins,
                opponentWinPerc: xmlstanding.getAttribute("opponentWin")
            });
        }

        standings.sort(function (a, b) { return a.currentStanding - b.currentStanding; });

        var cells = function (standing) {
            var out = [];
            var values = [standing.currentStanding, standing.player, standing.points, standing.gamesPlayed, standing.wins,
                standing.losses, standing.opponentWinPerc];
            for (var v = 0; v < values.length; v++)
                out.push($("<td></td>").text(values[v] == null ? "" : String(values[v])));
            return out;
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
LeagueResultsUI.drawerRenders = 0;   // makes each drawer's tab ids unique on the page

// "2026-09-01 to 2026-09-30"; a missing end shows as the start alone.
LeagueResultsUI.dateRange = function (start, end) {
    var from = start == null ? "" : String(start);
    var to = end == null ? "" : String(end);
    if (to === "" || to === from)
        return from;
    return from + " to " + to;
};

// A yyyyMMdd day from the server as yyyy-MM-dd; anything else is passed through.
LeagueResultsUI.day = function (value) {
    if (value == null)
        return "";
    var text = String(value);
    return /^\d{8}$/.test(text) ? getDateString(text) : text;
};

// The "Joined" mark on a league row's header (the calendar bolds joined leagues the same way).
LeagueResultsUI.markJoined = function (rowElement, joined) {
    var header = rowElement.children(".event-row-header");
    // the calendar preview says "You are signed up" in the same header already
    if (header.length == 0 || header.find(".calendar-details-joined").length > 0)
        return;
    var badge = header.children(".event-row-joined");
    if (joined && badge.length == 0)
        header.children(".event-row-title").after($("<span class='event-row-joined'></span>").text("Joined"));
    else if (!joined)
        badge.remove();
};

// Admin-written registration info, as text; a bare address becomes a link (the server wraps starwarsccg.org
// addresses in an anchor, which arrives as markup and is turned back into text here).
LeagueResultsUI.registrationInfo = function (info) {
    var text = String(info);
    var href = text.match(/href=['"]([^'"]+)['"]/);
    if (href) {
        var url = href[1];
        if (/^https?:\/\//i.test(url))
            return $("<a target='_blank' rel='noopener noreferrer'></a>").attr("href", url).text(url);
    }
    if (/^https?:\/\/\S+$/i.test(text))
        return $("<a target='_blank' rel='noopener noreferrer'></a>").attr("href", text).text(text);
    return $("<span></span>").text(text);
};
