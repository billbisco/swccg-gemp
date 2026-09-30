/**
 * Current leagues and one league's full detail.
 *
 *   new LeagueResultsUI(url)                       // legacy Events tab: #leagueResults / #myLeagueResults
 *   new LeagueResultsUI(url, joinCallback, {list: $container})
 *       // list mode (Play > League Join list): rows into $container with unique id prefixes so they share
 *       // no element ids with the Events tab.  Options:
 *       //   list:        container for the rows (required for list mode)
 *       //   autoLoad:    false = do not fetch from the constructor (default true)
 *       //   idPrefix:    prefix for drawer / tab ids (default "playLeague")
 *       //   onJoinError: function (leagueCode, message, status) for failed joins
 *
 * joinCallback(leagueCode), if given, runs after a successful join from any render of this object.
 *
 * Slice 1.5: list mode ported from LOTR leagueResultsUi (without EventDrawer — SWCCG uses simple
 * Details slideToggle drawers like the classic Events tab).
 */
var LeagueResultsUI = Class.extend({
    communication:null,
    questionDialog:null,
    formatDialog:null,
    joinCallback:null,
    options:null,
    list:null,
    idPrefix:null,
    drawerSerial:0,

    init:function (url, joinCallback, options) {
        this.communication = new GempSwccgCommunication(url,
            function (xhr, ajaxOptions, thrownError) {
            });

        // zIndex above .play-flow (10000): Join Yes/No was invisible behind Create Table overlay (Slice 1.5 FAIL)
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
        this.idPrefix = this.options.idPrefix || "playLeague";
        this.drawerSerial = 0;

        if (this.options.autoLoad !== false)
            this.loadResults();
    },

    loadResults:function () {
        var that = this;
        this.communication.getLeagues(
            function (xml) {
                that.loadedLeagueResults(xml);
            });
    },

    // options: {idPrefix, showName, hideId, compactJoin} — list-mode drawers mirror LOTR compact Cost/Join block
    loadedLeague:function (xml, leagueExtraInfoCssId, options) {
        var that = this;
        options = options || {};
        var tabPrefix = options.idPrefix || "league";
        var showName = options.showName !== false;
        var hideId = options.hideId === true;
        var compactJoin = options.compactJoin === true;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'league') {
            $(leagueExtraInfoCssId).html("");

            var league = root;

            var leagueName = league.getAttribute("name");
            var leagueType = league.getAttribute("type");
            var cost = parseInt(league.getAttribute("cost"));
            var start = league.getAttribute("start");
            var end = league.getAttribute("end");
            var member = league.getAttribute("member");
            var joinable = league.getAttribute("joinable");
            var isSoloDraft = league.getAttribute("isSoloDraft");
            var draftable = league.getAttribute("draftable");
            var invitationOnly = league.getAttribute("invitationOnly");
            var registrationInfo = league.getAttribute("registrationInfo");
            var lockedDeckType = league.getAttribute("lockedDeckType");
            var costStr = formatPrice(cost);

            // LOTR-style compact join block: Cost / membership+Join, then series lines (keep results tabs below)
            var joinBlock = $("<div class='league-join-block'></div>");
            if (compactJoin) {
                joinBlock.addClass("league-join-block-compact");
            }

            if (showName) {
                joinBlock.append("<div class='leagueName'>" + leagueName + "</div>");
            }
            if (!hideId) {
                joinBlock.append("<div class='leagueID'>League ID: " + leagueType + "</div>");
            }

            if (invitationOnly == "true") {
                if (registrationInfo != "" && registrationInfo != "null")
                    joinBlock.append("<div>Registration info: "+registrationInfo+"</div>");
                else
                    joinBlock.append("<div>Registration for this league by invitation only.</div>");
            } else {
                joinBlock.append("<div class='leagueCost'><b>Cost:</b> " + costStr + "</div>");
            }

            if (member == "true") {
                var memberDiv = $("<div class='leagueMembership'>You are already a member of this league. </div>");
                if(lockedDeckType === "when_first_played") {
                    memberDiv.append($("<div>Decks are locked-in automatically after first use and cannot be replaced.</div>"));
                    memberDiv.append($("<div><a target='_blank' href='/gemp-swccg-server/league/deck/html?leagueType="+leagueType+"'>View your locked-in decks here.</a></div>"));
                }
                if (draftable == "true") {
                    var draftBut = $("<button>--> Go to draft <--</button>").button();
                    var draftFunc = (function (leagueCode) {
                        return function() {
                            location.href = "soloDraft.html?leagueType="+leagueCode;
                        };
                    })(leagueType);
                    draftBut.click(draftFunc);
                    memberDiv.append(draftBut);
                } else if (isSoloDraft == "true") {
                    // Solo draft league but not yet draftable (hasn't started yet)
                    var grayedButton = $("<button disabled='disabled' class='draft-not-available'>Draft Available " + getDateString(start) + "</button>");
                    memberDiv.append(grayedButton);
                }
                joinBlock.append(memberDiv);
            }
            else if (joinable == "true" && invitationOnly != "true") {
                var joinBut = $("<button type='button'>Join league</button>").button();

                var joinFunc = (function (leagueCode, costString) {
                    return function () {
                        that.displayBuyAction("Do you want to join the league by paying " + costString + "?",
                            function () {
                                that.communication.joinLeague(leagueCode, function () {
                                    that.loadResults();
                                    if (that.joinCallback != null)
                                        that.joinCallback(leagueCode);
                                }, that.joinErrorMap(leagueCode));
                            });
                    };
                })(leagueType, costStr);
                joinBut.click(joinFunc);
                var joinDiv = $("<div class='leagueMembership'>You're not a member of this league. </div>");
                joinDiv.append(joinBut);
                joinBlock.append(joinDiv);
            } else if (joinable == "true" && invitationOnly == "true") {
                var joinDiv = $("<div class='leagueMembership'>You're not a member of this league. </div>");
                joinBlock.append(joinDiv);
            }

            $(leagueExtraInfoCssId).append(joinBlock);

            var tabDiv = $("<div width='100%'></div>");
            var tabNavigation = $("<ul></ul>");
            tabDiv.append(tabNavigation);

            var overallId = tabPrefix + "overall";
            var matchesId = tabPrefix + "matches";

            var tabContent = $("<div id='" + overallId + "'></div>");

            var standings = league.getElementsByTagName("leagueStanding");
            if (standings.length > 0)
                tabContent.append(this.createStandingsTable(standings));
            tabDiv.append(tabContent);

            tabNavigation.append("<li><a href='#" + overallId + "'>Overall results</a></li>");
            tabNavigation.append("<li><a href='#" + matchesId + "'>Your league matches</a></li>");

            var matchResults = $("<div id='" + matchesId + "'></div>");
            tabDiv.append(matchResults);

            var series = league.getElementsByTagName("serie");
            for (var j = 0; j < series.length; j++) {
                var serie = series[j];
                matchResults.append("<div>Serie " + (j + 1) + "</div>");
                var matchGroup = $("<table class='standings'><tr><th>Winner</th><th>Loser</th></tr></table>");
                var matches = serie.getElementsByTagName("match");
                for (var k = 0; k<matches.length; k++) {
                    var match = matches[k];
                    matchGroup.append("<tr><td>"+match.getAttribute("winner")+"</td><td>"+match.getAttribute("loser")+"</td></tr>");
                }

                matchResults.append(matchGroup);

                var serieTabId = tabPrefix + "serie" + j;
                var tabContentSerie = $("<div id='" + serieTabId + "'></div>");

                var serieName = serie.getAttribute("type");
                var serieStart = serie.getAttribute("start");
                var serieEnd = serie.getAttribute("end");
                var maxMatches = serie.getAttribute("maxMatches");
                var formatType = serie.getAttribute("formatType");
                var format = serie.getAttribute("format");
                var collection = serie.getAttribute("collection");
                var limited = serie.getAttribute("limited");

                var serieText = serieName + " - " + getDateString(serieStart) + " to " + getDateString(serieEnd);
                $(leagueExtraInfoCssId).append("<div class='serieName'>" + serieText + "</div>");

                var formatName = $("<span class='clickableFormat'>" + ((limited == "true") ? "" : "Constructed ") + format + "</span>");
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
                $(leagueExtraInfoCssId).append(formatDiv);
                $(leagueExtraInfoCssId).append("<div><b>Collection:</b> " + collection + "</div>");

                tabContentSerie.append("<div>Maximum ranked matches in serie: " + maxMatches + "</div>");

                var serieStandings = serie.getElementsByTagName("standing");
                if (serieStandings.length > 0)
                    tabContentSerie.append(this.createStandingsTable(serieStandings));
                tabDiv.append(tabContentSerie);

                tabNavigation.append("<li><a href='#" + serieTabId + "'>Serie " + (j + 1) + "</a></li>");
            }

            tabDiv.tabs();

            $(leagueExtraInfoCssId).append(tabDiv);
        }
    },

    loadedLeagueResults:function (xml) {
        var that = this;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName != 'leagues')
            return;

        // ---- list mode (Play > Join Leagues): own container, no Events tab ids ----
        if (this.list != null) {
            this.renderListMode(root);
            return;
        }

        // ---- legacy Events tab ----
        $("#myLeagueResults").html("");
        $("#leagueResults").html("");
        var myLeaguesCount = 0;

        var leagues = root.getElementsByTagName("league");
        for (var i = 0; i < leagues.length; i++) {
            var league = leagues[i];
            console.log(league);
            var leagueName = league.getAttribute("name");
            var leagueMember = league.getAttribute("member");
            var leagueType = league.getAttribute("type");
            var start = league.getAttribute("start");
            var end = league.getAttribute("end");
            var leagueExtraInfoCssId = "league-"+i+"-extra-info";

            if (leagueMember == "true") {
                myLeaguesCount = myLeaguesCount + 1;

                $("#myLeagueResults").append("<div id='my-league-"+i+"-name' class='leagueName'>" + leagueName + "</div>");

                var duration = '<span class="leagueDurationStart">' + getDateString(start) + '</span> to <span class="leagueDurationStop">' + getDateString(end) + '</span>';
                $("#myLeagueResults").append("<div id='my-league-"+i+"-duration' class='leagueDuration'><b>Duration (GMT+0):</b> " + duration + "</div>");

                var myDetailsBut = $('<button id="my-league-'+i+'-see-details-button" class="leagueSeeDetails">Display league details</button>').button();
                $("#myLeagueResults").append(myDetailsBut);
                $("#myLeagueResults").append("<div id='my-"+leagueExtraInfoCssId+"' class='leagueExtraInfo' style='display:none;'></div>");
                $("#myLeagueResults").append("<hr class='leagueHr' />");

                myDetailsBut.click(
                    (function (type, cssid, t) {
                        return function () {
                            that.communication.getLeague(type, function (xml) {
                                that.loadedLeague(xml, cssid);
                                $(cssid).slideToggle();
                                console.log("Showing MY details for: "+cssid);
                            });
                        };
                    })(leagueType, "#my-"+leagueExtraInfoCssId, ""));

            } else {

                $("#leagueResults").append("<div id='league-"+i+"-name' class='leagueName'>" + leagueName + "</div>");

                var durationAll = '<span class="leagueDurationStart">' + getDateString(start) + '</span> to <span class="leagueDurationStop">' + getDateString(end) + '</span>';
                $("#leagueResults").append("<div id='league-"+i+"-duration' class='leagueDuration'><b>Duration (GMT+0):</b> " + durationAll + "</div>");

                var detailsBut = $('<button id="league-'+i+'-see-details-button" class="leagueSeeDetails">Display league details</button>').button();
                $("#leagueResults").append(detailsBut);
                $("#leagueResults").append("<div id='"+leagueExtraInfoCssId+"' class='leagueExtraInfo' style='display:none;'></div>");
                $("#leagueResults").append("<hr class='leagueHr' />");

                detailsBut.click(
                    (function (type, cssid) {
                        return function () {
                            that.communication.getLeague(type, function (xml) {
                                that.loadedLeague(xml, cssid);
                                $(cssid).slideToggle();
                                console.log("Showing ALL details for: "+cssid);
                            });
                        };
                    })(leagueType, "#"+leagueExtraInfoCssId));

            }

        }
        if (myLeaguesCount == 0) {
            $("#myLeagueResults").html("You are not currently part of any leagues. Join one by expanding the league info in the <strong>All Leagues</strong> section and joining a league.");
        }
    },

    // Play panel join list: one row per league with + Details / Join inside the drawer
    renderListMode:function (root) {
        var that = this;
        this.list.empty();
        var leagues = root.getElementsByTagName("league");
        if (leagues.length == 0) {
            this.list.append($("<i></i>").text("There are no current leagues at the moment."));
            return;
        }
        for (var i = 0; i < leagues.length; i++) {
            var league = leagues[i];
            var leagueName = league.getAttribute("name");
            var leagueType = league.getAttribute("type");
            var start = league.getAttribute("start");
            var end = league.getAttribute("end");
            var member = league.getAttribute("member");

            var rowId = this.idPrefix + "-row-" + i;
            var drawerId = this.idPrefix + "-drawer-" + i;
            var row = $("<div class='play-league-row event-row'></div>").attr("id", rowId);
            var header = $("<div class='play-league-row-header event-row-header'></div>");

            var detailsBut = $("<button type='button' class='leagueSeeDetails play-league-details-btn'>+ Details</button>").button();
            header.append(detailsBut);
            header.append($("<span class='leagueName play-league-row-name'></span>").text(leagueName));
            if (member == "true") {
                header.append($("<span class='play-league-member-badge'></span>").text("Joined"));
            }
            var duration = getDateString(start) + " to " + getDateString(end);
            header.append($("<span class='leagueDuration play-league-row-dates'></span>").text(duration));
            row.append(header);

            var drawer = $("<div class='leagueExtraInfo play-league-drawer' style='display:none;'></div>").attr("id", drawerId);
            row.append(drawer);
            this.list.append(row);

            detailsBut.click(
                (function (type, cssid, btn) {
                    return function () {
                        var $drawer = $(cssid);
                        if ($drawer.is(":visible")) {
                            $drawer.slideUp();
                            btn.button("option", "label", "+ Details");
                            return;
                        }
                        that.communication.getLeague(type, function (xml) {
                            that.drawerSerial++;
                            that.loadedLeague(xml, cssid, {
                                idPrefix: that.idPrefix + "D" + that.drawerSerial + "-",
                                showName: false,
                                hideId: true,
                                compactJoin: true
                            });
                            $drawer.slideDown();
                            btn.button("option", "label", "- Details");
                        });
                    };
                })(leagueType, "#" + drawerId, detailsBut));
        }
    },

    joinErrorMap:function (leagueCode) {
        var report = this.options && this.options.onJoinError;
        if (typeof report != "function") {
            return {
                "409":function () {
                    alert("You don't have enough funds to join this league.");
                }
            };
        }
        return {
            "409":function () {
                report(leagueCode, "You don't have enough funds to join this league.", 409);
            },
            "0":function (xhr, status, error) {
                report(leagueCode, error || status || "Network error", 0);
            },
            "500":function () {
                report(leagueCode, "Server error joining the league.", 500);
            },
            "400":function () {
                report(leagueCode, "Could not join the league.", 400);
            }
        };
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

        var horSpace = 230;
        var vertSpace = 100;

        this.questionDialog.dialog({
            width:Math.min(horSpace, windowWidth),
            height:Math.min(vertSpace, windowHeight),
            zIndex:11050
        });
        // Ensure overlay + dialog stack above Create Table play-flow (z-index 10000)
        this.questionDialog.dialog("open");
        try {
            this.questionDialog.dialog("widget").css("z-index", 11050);
            $(".ui-widget-overlay").last().css("z-index", 11040);
        } catch (ignored) {}
    },

    createStandingsTable:function (standings) {
        var standingsTable = $("<table class='standings'></table>");

        standingsTable.append("<tr><th>Standing</th><th>Player</th><th>Points</th><th>Games played</th><th>W</th><th>L</th><th>Opp. Win %</th><th></th><th>Standing</th><th>Player</th><th>Points</th><th>Games played</th><th>W</th><th>L</th><th>Opp. Win %</th></tr>");

        var secondColumnBaseIndex = Math.ceil(standings.length / 2);

        for (var k = 0; k < secondColumnBaseIndex; k++) {
            var standing = standings[k];
            var currentStanding = standing.getAttribute("standing");
            var player = standing.getAttribute("player");
            var points = parseInt(standing.getAttribute("points"));
            var gamesPlayed = parseInt(standing.getAttribute("gamesPlayed"));
            var opponentWinPerc = standing.getAttribute("opponentWin");

            standingsTable.append("<tr><td>" + currentStanding + "</td><td>" + player + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + ((points-gamesPlayed)/2) + "</td><td>" + (gamesPlayed-(points-gamesPlayed)/2) + "</td><td>" + opponentWinPerc + "</td></tr>");
        }

        for (var k = secondColumnBaseIndex; k < standings.length; k++) {
            var standing = standings[k];
            var currentStanding = standing.getAttribute("standing");
            var player = standing.getAttribute("player");
            var points = parseInt(standing.getAttribute("points"));
            var gamesPlayed = parseInt(standing.getAttribute("gamesPlayed"));
            var opponentWinPerc = standing.getAttribute("opponentWin");

            $("tr:eq(" + (k - secondColumnBaseIndex + 1) + ")", standingsTable).append("<td></td><td>" + currentStanding + "</td><td>" + player + "</td><td>" + points + "</td><td>" + gamesPlayed + "</td><td>" + ((points-gamesPlayed)/2) + "</td><td>" + (gamesPlayed-(points-gamesPlayed)/2) + "</td><td>" + opponentWinPerc + "</td>");
        }

        return standingsTable;
    }
});
