// Slice 1.3: Hall connection readout — mirrored from PlayersCouncil/gemp-lotr
// gemp-lotr-async/src/main/web/js/gemp-022/hallUi.js (HallConnectionIndicator).
// States: connecting / connected / reconnecting / disconnected. Hover/focus/click opens details popup.
// Wired into SWCCG's existing getHall/updateHall/hallErrorMap (no full LOTR poll rewrite).
// NOTE: SWCCG hall.html ships jQuery 1.6.2 — use .bind() not .on() (added in 1.7).
var HallConnectionIndicator = Class.extend({
	root: null,
	button: null,
	label: null,
	live: null,
	popup: null,

	state: null,
	lastUpdate: null,
	detail: null,
	pinned: false,

	LABELS: {
		connecting: "Connecting",
		connected: "Connected",
		reconnecting: "Reconnecting",
		disconnected: "Disconnected",
		loggedout: "Not logged in"
	},

	STATUS: {
		connecting: "Connecting to the Game Hall…",
		connected: "Connected: the Game Hall is updating live.",
		reconnecting: "Reconnecting: the Game Hall lost contact with the server and is retrying.",
		disconnected: "Disconnected: the Game Hall is not updating.",
		loggedout: "Not logged in: log in to see the Game Hall's tables and chat and to play."
	},

	init: function (root) {
		var that = this;
		this.root = root;
		this.button = root.find(".hall-connection-readout");
		this.label = root.find(".hall-connection-label");
		this.live = root.find(".hall-connection-live");
		this.popup = root.find(".hall-connection-popup");
		this.signin = root.find(".hall-connection-signin");
		this.signin.bind("click", function () {
			this.href = HallConnectionIndicator.loginUrl();
		});

		root.bind("mouseenter", function () {
			that.open(false);
		});
		root.bind("mouseleave", function () {
			if (!that.pinned && !that.keyboardFocusInside())
				that.close();
		});
		root.bind("focusin", function () {
			if (that.keyboardFocusInside())
				that.open(false);
		});
		root.bind("focusout", function (event) {
			if (!that.pinned && !(event.relatedTarget && $.contains(root[0], event.relatedTarget)))
				that.close();
		});
		this.button.bind("click", function () {
			if (that.pinned)
				that.close();
			else
				that.open(true);
		});
		root.bind("keydown", function (event) {
			var isEsc = event.key === "Escape" || event.keyCode === 27;
			if (isEsc && that.isOpen()) {
				that.close();
				that.button.trigger("focus");
			}
		});
		$(document).bind("click", function (event) {
			if (that.pinned && root.length && !$.contains(root[0], event.target) && root[0] !== event.target)
				that.close();
		});

		this.set("connecting");
	},

	keyboardFocusInside: function () {
		var active = document.activeElement;
		if (active == null || !this.root.length || !$.contains(this.root[0], active))
			return false;
		try {
			return active.matches(":focus-visible");
		} catch (e) {
			return true;
		}
	},

	isOpen: function () {
		return this.popup.length > 0 && !this.popup.prop("hidden");
	},

	open: function (pin) {
		this.pinned = this.pinned || pin;
		this.render();
		this.popup.prop("hidden", false);
		this.button.attr("aria-expanded", "true");
	},

	close: function () {
		this.pinned = false;
		this.popup.prop("hidden", true);
		this.button.attr("aria-expanded", "false");
	},

	set: function (state, detail) {
		var changed = state !== this.state;
		this.state = state;
		this.detail = detail || null;
		this.root.attr("data-state", state);
		this.label.text(this.LABELS[state] || state);
		if (this.signin != null)
			this.signin.prop("hidden", state !== "loggedout");
		if (changed) {
			var spoken = state === "loggedout" ? "Not logged in." : "Game Hall " + (this.LABELS[state] || state).toLowerCase() + ".";
			if (state === "disconnected" && this.detail != null && this.detail.message)
				spoken += " " + this.detail.message;
			this.live.text(spoken);
		}
		this.render();
	},

	updated: function (detail, serverTime) {
		this.lastUpdate = serverTime || null;
		if (this.state === "connected" && detail == null)
			detail = this.detail;
		this.set("connected", detail);
	},

	render: function () {
		if (!this.popup.length)
			return;
		this.popup.find(".hall-connection-popup-status").text(this.STATUS[this.state] || "");
		this.popup.find(".hall-connection-popup-updated").text("Last update: "
			+ (this.lastUpdate == null ? "none yet" : this.lastUpdate + " (server time)"))
			.prop("hidden", this.state === "loggedout" && this.lastUpdate == null);

		var message = this.popup.find(".hall-connection-popup-message").empty();
		if (this.detail != null && this.detail.message)
			message.append($("<span></span>").text(this.detail.message));
		if (this.detail != null && this.detail.action === "reload") {
			message.append(" ", $("<a class='hall-connection-reload'></a>")
				.attr("href", window.location.href)
				.text("Reload the page")
				.bind("click", function (event) {
					event.preventDefault();
					window.location.reload();
				}));
		} else if (this.detail != null && this.detail.action === "login") {
			message.append(" ", $("<a class='hall-connection-login'></a>")
				.attr("href", HallConnectionIndicator.loginUrl())
				.text(this.state === "loggedout" ? "Log in or register" : "Go to the main page to log in")
				.bind("click", function () {
					this.href = HallConnectionIndicator.loginUrl();
				}));
		}
		message.prop("hidden", message.is(":empty"));
	}
});

HallConnectionIndicator.loginUrl = function () {
	return "/gemp-swccg/";
};

var GempSwccgHallUI = Class.extend({
    div:null,
    comm:null,
    chat:null,
    supportedFormatsInitialized:false,
    supportedFormatsSelect:null,
    decksSelect:null,
    opponentSelect:null,
    aiSkillSelect:null,
    aiDeckSelect:null,
    aiControlsDiv:null,
    aiTablesEnabled:true,
    lastAiDeckPlayerSide:null,
    deckOptions:[],
    tableDescInput:null,
    createTableButton:null,
    playerDeckSelect:null,
    libraryDeckSelect:null,
    playerDeckLabel:null,
    libraryDeckLabel:null,
    botPlayerDeckSelect:null,
    botLibraryDeckSelect:null,
    timerSelect:null,
    isInviteOnlyCheckbox:null,
    inviteeInput:null,
    invitePicker:null,
    inviteRow:null,
    inviteeRow:null,
    descRow:null,
    timerRow:null,
    privateRow:null,
    keepOpenCheckbox:null,
    defaultFlowCheckbox:null,
    aiSkillRow:null,
    botPlayerDeckRow:null,
    botLibraryDeckRow:null,
    playerDeckRow:null,
    libraryDeckRow:null,
    playFormResult:null,
    hallPlayerNames:null,

    tablesDiv:null,
    buttonsDiv:null,
    controlsLeft:null,
    controlsRight:null,
    isPrivateCheckbox:null,
    adminTab:null,
    userInfo:null,

    pocketDiv:null,
    pocketValue:null,
    hallChannelId: null,

    // Slice 1.3: LOTR-mirrored connection + Server Time on primary bar
    connection:null,
    connectionDiv:null,
    serverTimeDiv:null,
    serverTimeValue:null,

    // Slice 1: Deck-builder + Play primary chrome / overlay
    deckBuilderButton:null,
    playButton:null,
    playOverlay:null,
    playSelectionPanel:null,
    playFormPanel:null,
    playBotPanel:null,
    playBotFieldsHost:null,
    playFormTitle:null,
    playFormFields:null,
    playCasualChoice:null,
    playAiChoice:null,
    playLeagueChoice:null,
    playTournamentChoice:null,
    playTournamentPanel:null,
    playLeagueEmpty:null,
    playLeaguePanel:null,
    leagueFormatSelect:null,
    leagueDecksSelect:null,
    leagueLibraryDecksSelect:null,
    leagueLibraryRow:null,
    leagueLibraryHelp:null,
    _leagueLibraryDeckVisible:false,
    leagueCreateButton:null,
    leagueResultDiv:null,
    playLeagueNextSteps:null,
    playLeagueList:null,
    playLeagueUI:null,
    leagueCache:null, // [{type,name,member,start,end}, ...]
    playBackButton:null,
    playMode:null, // "casual" | "ai" | "league" | "tournament"
    openedGameIds:null,
    pendingBotWin:null,
    leagueTypesLoaded:false,
    privateGamesAllowed:false,

    // Slice 1.1: Join table / queue deck picker overlay
    joinOverlay:null,
    joinPlayerDeckSelect:null,
    joinLibraryDeckSelect:null,
    joinLibraryDeckRow:null,
    _joinLibraryDeckVisible:true,
    joinSubmitButton:null,
    joinResultDiv:null,
    joinContextDiv:null,
    joinTitleEl:null,
    joinPending:null, // {kind:"table"|"queue", id, formatName, formatCode, collectionCode, contextLabel}
    deckLoadGen:0,
    joinDeckLoadGen:0,
    lastServerTime:null,
    serverClockOffset:null,
    AGE_REFRESH_MS:1000,

    init:function (div, url, chat) {
        this.div = div;
        if (typeof Card !== "undefined" && Card.applyFoilPresentation)
            Card.applyFoilPresentation();
        this.comm = new GempSwccgCommunication(url, function (xhr, ajaxOptions, thrownError) {
            if (thrownError != "abort") {
                if (xhr != null) {
                    if (xhr.status == 401) {
                        chat.appendMessage("Game hall problem - You're not logged in, go to the <a href='index.html'>main page</a> to log in", "warningMessage");
                        return;
                    } else {
                        chat.appendMessage("The game hall had a problem communicating with the server (" + xhr.status + "), no new updates will be displayed.", "warningMessage");
                        chat.appendMessage("Reload the browser page (press F5) to resume the game hall functionality.", "warningMessage");
                        return;
                    }
                }
                chat.appendMessage("The game hall had a problem communicating with the server, no new updates will be displayed.", "warningMessage");
                chat.appendMessage("Reload the browser page (press F5) to resume the game hall functionality.", "warningMessage");
            }
        });
        this.chat = chat;
        this.readyChecks = {};
        this.readyModals = {};
        this.offeredGames = {};
        this.signedUpEvents = {};
        this.inTournament = false;
        var thatHall = this;
        this.chat.tournamentCallback = function(from, message) {
            var thisName = (thatHall.userInfo && thatHall.userInfo.name) ? thatHall.userInfo.name : null;
            if (from == "TournamentSystem" && thatHall.inTournament) {
                thatHall.showDialog("Tournament Update", message, 400);
            } else if (from && from.indexOf("TournamentSystemTo:") == 0) {
                var users = from.substring("TournamentSystemTo:".length).split(";");
                if (thisName != null && $.inArray(thisName, users) >= 0)
                    thatHall.showDialog("Tournament Update", message, 400);
            }
        };
        this.hallPlayerNames = [];
        var thatChat = this;
        this.chat.playerListener = function (players) {
            var names = [];
            var i;
            for (i = 0; i < (players || []).length; i++) {
                var bare = thatChat.bareHallPlayerName(players[i]);
                if (bare)
                    names.push(bare);
            }
            thatChat.hallPlayerNames = names;
            if (thatChat.invitePicker)
                thatChat.invitePicker.setPlayers(names);
        };

        var width = $(div).width();
        var height = $(div).height();

        this.tablesDiv = $("<div></div>");
        this.tablesDiv.css({overflow:"auto", left:"0px", top:"0px", width:width + "px", height:(height - 56) + "px"});
        
        var hallSettingsStr = $.cookie("hallSettings");
        if (hallSettingsStr == null)
            hallSettingsStr = "0|0|1|1|0";
        var hallSettings = hallSettingsStr.split("|");

        // TODO: Comment out for now      this.addQueuesTable(hallSettings[0] == "1");
        // TODO: Comment out for now      this.addTournamentsTable(hallSettings[1] == "1");
        this.addWaitingTablesTable(hallSettings[2] == "1");
        this.addPlayingTablesTable(hallSettings[3] == "1");
        this.addFinishedTablesTable(hallSettings[4] == "1");

        this.div.append(this.tablesDiv);

        // Slice 1.3 primary bar — three zones mirrored from LOTR hall.html .buttons-gutter:
        // left #hall-connection | centered Deck-builder+Play | right .server-time (with "Server Time" label)
        this.buttonsDiv = $("<div class='hall-primary-bar flex-horiz'></div>");
        this.buttonsDiv.css({left:"0px", top:(height - 56) + "px", width:width + "px", backgroundColor:"#000000", "border-top-width":"1px", "border-top-color":"#ffffff", "border-top-style":"solid", "box-sizing":"border-box", padding:"6px 8px", display:"flex", "flex-direction":"row", "align-items":"center", "justify-content":"center", gap:"4px"});

        var that = this;

        // Left: connection readout (markup mirrors PlayersCouncil/gemp-lotr hall.html #hall-connection)
        this.connectionDiv = $("<div id='hall-connection' class='hall-connection' data-state='connecting'></div>");
        var connBtn = $("<button type='button' class='hall-connection-readout' aria-expanded='false' aria-controls='hall-connection-popup'></button>");
        connBtn.append("<span class='hall-connection-dot' aria-hidden='true'></span>");
        connBtn.append("<span class='hall-connection-label'>Connecting</span>");
        this.connectionDiv.append(connBtn);
        this.connectionDiv.append("<a class='hall-connection-signin' href='/gemp-swccg/' hidden>Log in</a>");
        this.connectionDiv.append("<span class='hall-connection-live visually-hidden' role='status' aria-live='polite'></span>");
        var connPopup = $("<div id='hall-connection-popup' class='hall-connection-popup' hidden></div>");
        connPopup.append("<div class='hall-connection-popup-status'></div>");
        connPopup.append("<div class='hall-connection-popup-updated'></div>");
        connPopup.append("<div class='hall-connection-popup-message' hidden></div>");
        this.connectionDiv.append(connPopup);

        // Center: Deck-builder + Play (LOTR icons from Slice 1.1.2/1.1.3 kept)
        this.controlsLeft = $("<div class='hall-primary-actions flex-horiz'></div>");
        this.deckBuilderButton = $("<a id='deckbuilder-button' class='hall-play-button hall-play-button-deckbuilder' href='deckBuild.html' target='_blank'><span class='bigger-icon icon-deckbuilder' aria-hidden='true'></span><span class='hall-play-button-label'><b>Deck-builder</b></span></a>");
        this.playButton = $("<button type='button' id='open-table-button' class='hall-play-button hall-play-button-play'><span class='bigger-icon icon-play' aria-hidden='true'></span><span class='hall-play-button-label'><b>Play</b></span></button>");
        this.controlsLeft.append(this.deckBuilderButton);
        this.controlsLeft.append(this.playButton);
        $(this.playButton).button().click(function () {
            that.openPlayOverlay();
        });
        $(this.deckBuilderButton).button();

        // Right: Server Time (LOTR .server-time pattern + explicit "Server Time" label per Bill)
        this.serverTimeDiv = $("<div class='server-time hall-server-time'></div>");
        this.serverTimeDiv.append("<div class='hall-server-time-label'>Server Time</div>");
        this.serverTimeValue = $("<div class='hall-server-time-value'></div>");
        this.serverTimeDiv.append(this.serverTimeValue);

        this.buttonsDiv.append(this.connectionDiv);
        this.buttonsDiv.append(this.controlsLeft);
        this.buttonsDiv.append(this.serverTimeDiv);

        this.connection = new HallConnectionIndicator(this.connectionDiv);
        // Server Time and waiting/playing table clocks tick locally every second.
        setInterval(function () {
            that.refreshTableAges();
        }, this.AGE_REFRESH_MS);
        // Currency / pocket removed from primary bar (still tracked for merchant elsewhere via pocketValue)

        // Create-table form controls live in the Play overlay (not the always-visible strip)
        this.supportedFormatsSelect = $("<select class='play-form-select' style='width: 175px'></select>");
        this.supportedFormatsSelect.hide();
        this.supportedFormatsSelect.change(function () {
            var fmt = that.supportedFormatsSelect.val();
            if (fmt)
                that.updateDecks(fmt, "default");
        });

        this.createTableButton = $("<button type='button' class='play-submit-button'>Create table</button>");
        $(this.createTableButton).button().click(function () {
            that.submitCreateTable();
        });
        this.createTableButton.hide();

        this.isPrivateCheckbox = $("<label class='play-private-label'><input type='checkbox' id='isPrivateCheckbox1'> Private Mode</label>");

        this.decksSelect = $("<select class='play-form-select' style='width: 300px'></select>");
        this.decksSelect.hide();

        this.playerDeckSelect = $("<select class='play-form-select'></select>");
        this.libraryDeckSelect = $("<select class='play-form-select'></select>");
        this.botPlayerDeckSelect = $("<select class='play-form-select'></select>");
        this.botLibraryDeckSelect = $("<select class='play-form-select'></select>");
        this.aiDeckSelect = $("<select class='play-form-select' style='width: 280px'></select>");
        this.aiDeckSelect.hide();

        this.bindExclusiveDeckPair(this.playerDeckSelect, this.libraryDeckSelect, function () {
            that.updateBotDeckSelects();
        });
        this.bindExclusiveDeckPair(this.botPlayerDeckSelect, this.botLibraryDeckSelect, null);

        // Kept as hidden state for the shared createTable API (submenu picks human vs AI)
        this.opponentSelect = $("<select style='width: 110px'></select>");
        this.opponentSelect.append("<option value='human'>vs Human</option>");
        this.opponentSelect.append("<option value='ai'>vs Bot</option>");
        this.opponentSelect.hide();
        this.opponentSelect.change(function () { that.updateAiDecksForSelection(); });

        this.aiSkillSelect = $("<select class='play-form-select' style='width: 160px'></select>");
        this.aiSkillSelect.append("<option value='BEGINNER'>Beginner (OzzelBot)</option>");
        this.aiSkillSelect.append("<option value='ADVANCED'>Advanced (YodaBot)</option>");
        this.aiSkillSelect.append("<option value='RANDO'>Elite (Rando_Cal)</option>");

        this.aiControlsDiv = $("<div class='play-ai-controls'></div>");
        this.aiSkillRow = $("<div class='play-form-row'></div>");
        this.aiSkillRow.append("<span class='play-form-label'>Bot skill</span>");
        this.aiSkillRow.append(this.aiSkillSelect);
        this.aiControlsDiv.append(this.aiSkillRow);
        this.aiControlsDiv.hide();

        this.timerSelect = $("<select class='play-form-select'></select>");
        this.timerSelect.append("<option value='default'>Default (45m/6m)</option>");
        this.timerSelect.append("<option value='blitz'>Blitz! (25m/3m)</option>");
        this.timerSelect.append("<option value='WC'>Championship (20m/10m)</option>");
        this.timerSelect.append("<option value='slow'>Slow (80m/10m)</option>");
        this.timerSelect.append("<option value='glacial'>Glacial (1d/1d)</option>");
        var lastTimer = $.cookie("unranked-table-last-timer");
        if (lastTimer)
            this.timerSelect.val(lastTimer);
        this.timerSelect.change(function () {
            $.cookie("unranked-table-last-timer", that.timerSelect.val(), { expires:365 });
        });

        this.isInviteOnlyCheckbox = $("<label class='play-private-label'><input type='checkbox' id='isInviteOnlyCheckbox'> Invite Only</label>");
        this.inviteeInput = $("<input id='inviteeInput' class='play-form-input' type='text' maxlength='50' placeholder='Player to invite'>");
        this.isInviteOnlyCheckbox.find("input").change(function () {
            that.syncInviteeRowVisibility();
        });

        this.keepOpenCheckbox = $("<label class='play-private-label'><input type='checkbox' id='keepWindowOpenCheckbox'> Keep this Window Open</label>");
        this.defaultFlowCheckbox = $("<label class='play-private-label'><input type='checkbox' id='defaultFlowCheckbox'> Open this Flow by Default</label>");
        this.defaultFlowCheckbox.find("input").change(function () {
            if (this.checked) {
                $.cookie("play-default-flow", that.playMode, { expires:365 });
            } else if ($.cookie("play-default-flow") === that.playMode) {
                $.cookie("play-default-flow", "", { expires:365 });
            }
        });

        this.tableDescInput = $("<input id='tableDescInput' class='play-form-input' type='text' maxlength='50' style='width: 220px;' placeHolder='Description (optional)'>");

        this.buildPlayOverlay();
        this.buildJoinOverlay();

        this.adminTab = $("#admin-tab");
        this.adminTab.hide();
        
        this.comm.getPlayerInfo(function(json)
            { 
                that.userInfo = json;
                if(    that.userInfo.type.includes("a")      //admin
                    || that.userInfo.type.includes("l")  //league admin
                    || that.userInfo.type.includes("p")  //playtesting admin
                    || that.userInfo.type.includes("m")) //commentator admin
                {
                    that.adminTab.show();
                }
                else
                {
                    that.adminTab.hide();
                }
            });

        this.updateCreateTableLabel();
        this.div.append(this.buttonsDiv);
        this.hallResized(width, height);

        this.getHall();
    },


    addQueuesTable: function(displayed) {
        var header = $("<div class='eventHeader queues'></div>");

        var content = $("<div></div>");

        var toggleContent = $("<div>Toggle tournament queues</div>").button({
            icons: {
                primary: "ui-icon-circlesmall-minus"
            },
            text: false
        });

        var that = this;
        var toggle = function() {
            if (toggleContent.button("option", "icons")["primary"] == "ui-icon-circlesmall-minus")
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            else
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-minus"});
            content.toggle("blind", {}, 200);
            that.updateHallSettings();
        };
        toggleContent.css({width: "13px", height: "15px"});
        toggleContent.click(toggle);

        header.append(toggleContent);
        header.append(" Tournament queues");
        header.append(" <span class='count'>(0)</span>");

        var table = $("<table class='tables queues'></table>");
        table.append("<tr><th width='10%'>Format</th><th width='8%'>Collection</th><th width='20%'>Queue name</th><th width='16%'>Starts</th><th width='10%'>System</th><th width='6%'>Players</th><th width='8%'>Cost</th><th width='12%'>Prizes</th><th width='10%'>Actions</th></tr>");
        content.append(table);

        this.tablesDiv.append(header);
        this.tablesDiv.append(content);

        if (!displayed) {
            toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            content.css({"display":"none"});
        }
    },

    addTournamentsTable: function(displayed) {
        var header = $("<div class='eventHeader tournaments'></div>");

        var content = $("<div></div>");

        var toggleContent = $("<div>Toggle tournaments in progress</div>").button({
            icons: {
                primary: "ui-icon-circlesmall-minus"
            },
            text: false
        });

        var that = this;
        var toggle = function() {
            if (toggleContent.button("option", "icons")["primary"] == "ui-icon-circlesmall-minus")
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            else
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-minus"});
            content.toggle("blind", {}, 200);
            that.updateHallSettings();
        };
        toggleContent.css({width: "13px", height: "15px"});
        toggleContent.click(toggle);
        header.append(toggleContent);
        header.append(" Tournaments in progress");
        header.append(" <span class='count'>(0)</span>");

        var table = $("<table class='tables tournaments'></table>");
        table.append("<tr><th width='10%'>Format</th><th width='10%'>Collection</th><th width='25%'>Tournament name</th><th width='15%'>System</th><th width='10%'>Stage</th><th width='10%'>Round</th><th width='10%'>Players</th><th width='10%'>Actions</th></tr>");
        content.append(table);

        this.tablesDiv.append(header);
        this.tablesDiv.append(content);

        if (!displayed) {
            toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            content.css({"display":"none"});
        }
    },

    addWaitingTablesTable: function(displayed) {
        var header = $("<div class='eventHeader waitingTables'></div>");

        var content = $("<div></div>");

        var toggleContent = $("<div>Toggle waiting tables</div>").button({
            icons: {
                primary: "ui-icon-circlesmall-minus"
            },
            text: false
        });

        var that = this;
        var toggle = function() {
            if (toggleContent.button("option", "icons")["primary"] == "ui-icon-circlesmall-minus")
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            else
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-minus"});
            content.toggle("blind", {}, 200);
            that.updateHallSettings();
        };
        toggleContent.css({width: "13px", height: "15px"});
        toggleContent.click(toggle);
        header.append(toggleContent);
        header.append(" Waiting tables");
        header.append(" <span class='count'>(0)</span>");

        var table = $("<table class='tables waitingTables'></table>");
        table.append("<tr><th width='20%'>Format</th><th width='30%'>Tournament</th><th width='10%'>Status</th><th width='30%'>Players</th><th width='10%'>Actions</th></tr>");
        content.append(table);

        this.tablesDiv.append(header);
        this.tablesDiv.append(content);

        if (!displayed) {
            toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            content.css({"display":"none"});
        }
    },

    addPlayingTablesTable: function(displayed) {
        var header = $("<div class='eventHeader playingTables'></div>");

        var content = $("<div></div>");

        var toggleContent = $("<div>Toggle playing tables</div>").button({
            icons: {
                primary: "ui-icon-circlesmall-minus"
            },
            text: false
        });

        var that = this;
        var toggle = function() {
            if (toggleContent.button("option", "icons")["primary"] == "ui-icon-circlesmall-minus")
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            else
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-minus"});
            content.toggle("blind", {}, 200);
            that.updateHallSettings();
        };
        toggleContent.css({width: "13px", height: "15px"});
        toggleContent.click(toggle);
        header.append(toggleContent);
        header.append(" Playing tables");
        header.append(" <span class='count'>(0)</span>");

        var table = $("<table class='tables playingTables'></table>");
        table.append("<tr><th width='20%'>Format</th><th width='30%'>Tournament</th><th width='10%'>Status</th><th width='30%'>Players</th><th width='10%'>Actions</th></tr>");
        content.append(table);

        this.tablesDiv.append(header);
        this.tablesDiv.append(content);

        if (!displayed) {
            toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            content.css({"display":"none"});
        }
    },

    addFinishedTablesTable: function(displayed) {
        var header = $("<div class='eventHeader finishedTables'></div>");

        var content = $("<div></div>");

        var toggleContent = $("<div>Toggle finished tables</div>").button({
            icons: {
                primary: "ui-icon-circlesmall-minus"
            },
            text: false
        });

        var that = this;
        var toggle = function() {
            if (toggleContent.button("option", "icons")["primary"] == "ui-icon-circlesmall-minus")
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            else
                toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-minus"});
            content.toggle("blind", {}, 200);
            that.updateHallSettings();
        };
        toggleContent.css({width: "13px", height: "15px"});
        toggleContent.click(toggle);
        header.append(toggleContent);
        header.append(" Finished tables");
        header.append(" <span class='count'>(0)</span>");

        var table = $("<table class='tables finishedTables'></table>");
        table.append("<tr><th width='20%'>Format</th><th width='30%'>Tournament</th><th width='10%'>Status</th><th width='30%'>Players</th><th width='10%'>Winner</th></tr>");
        content.append(table);

        this.tablesDiv.append(header);
        this.tablesDiv.append(content);

        if (!displayed) {
            toggleContent.button("option", "icons", {primary: "ui-icon-circlesmall-plus"});
            content.css({"display":"none"});
        }
    },

    updateHallSettings: function() {
        var icons = $(".ui-button-icon-primary", this.tablesDiv);
        var getSettingValue =
            function(index) {
                return $(icons[index]).hasClass("ui-icon-circlesmall-minus") ? "1" : "0";
            };

        var newHallSettings = getSettingValue(0) + "|" + getSettingValue(1) + "|" + getSettingValue(2) + "|" + getSettingValue(3) + "|" + getSettingValue(4);
        $.cookie("hallSettings", newHallSettings, { expires:365 });
    },

    buildPlayOverlay:function() {
        var that = this;

        // Prefer markup in hall.html when present; otherwise build the overlay at runtime
        var existing = $("#create-table-popup");
        if (existing.length > 0) {
            this.playOverlay = existing;
            this.playOverlay.empty();
        } else {
            this.playOverlay = $("<div id='create-table-popup' class='play-flow' style='display:none'></div>");
            $("body").append(this.playOverlay);
        }

        var backdrop = $("<div class='play-flow-backdrop' aria-hidden='true'></div>");
        backdrop.click(function () { that.closePlayOverlay(); });

        var panel = $("<div class='play-flow-panel' role='dialog' aria-modal='true' aria-label='Create Table'></div>");

        var header = $("<div class='play-flow-header'></div>");
        this.playBackButton = $("<button type='button' id='create-table-back-button' class='play-back-button'>&lt; Back</button>");
        this.playBackButton.click(function () {
            if (that.playFormPanel.is(":visible")
                    || (that.playBotPanel != null && that.playBotPanel.is(":visible"))
                    || (that.playTournamentPanel != null && that.playTournamentPanel.is(":visible"))
                    || (that.playLeaguePanel != null && that.playLeaguePanel.is(":visible"))) {
                that.showPlaySelection();
            } else {
                that.closePlayOverlay();
            }
        });
        header.append(this.playBackButton);
        this.playFlowTitle = $("<span class='play-flow-title'>Create Table</span>");
        header.append(this.playFlowTitle);
        header.append($("<button type='button' class='play-close-button' title='Close'>×</button>").click(function () {
            that.closePlayOverlay();
        }));
        panel.append(header);

        // Slice 1.4: LOTR-mirrored 4-way Create Table selection (Bot, Casual, League, Tournament)

        this.playSelectionPanel = $("<div id='create-table-selection' class='play-selection'></div>");
        this.playAiChoice = $("<button type='button' id='create-bot-table-button' class='play-choice-button'><span class='play-choice-title'><span class='bigger-icon icon-bot' aria-hidden='true'></span><span>Play Against Bots (Beta)</span></span><span class='play-subtitle'>A practice game against a computer opponent.</span></button>");
        this.playCasualChoice = $("<button type='button' id='create-unranked-table-button' class='play-choice-button'><span class='play-choice-title'><span class='bigger-icon icon-unranked' aria-hidden='true'></span><span>Open Casual Table</span></span><span class='play-subtitle'>A 1-on-1 game against another player.</span></button>");
        this.playLeagueChoice = $("<button type='button' id='create-league-table-button' class='play-choice-button'><span class='play-choice-title'><span class='bigger-icon icon-league' aria-hidden='true'></span><span>Open League Table</span></span><span class='play-subtitle'>Join a multi-week league and participate in themed Sealed or Constructed events of all kinds.</span></button>");
        this.playTournamentChoice = $("<button type='button' id='create-tournament-button' class='play-choice-button'><span class='play-choice-title'><span class='bigger-icon icon-tournament' aria-hidden='true'></span><span>Create Tournament</span></span><span class='play-subtitle'>Host an event that other players sign up for.</span></button>");
        this.playAiChoice.button().click(function () { that.showPlayForm("ai"); });
        this.playCasualChoice.button().click(function () { that.showPlayForm("casual"); });
        this.playLeagueChoice.button().click(function () { that.showLeaguePanel(); });
        this.playTournamentChoice.button().click(function () { that.showTournamentInfo(); });
        // LOTR order: Bot, Casual, League, Tournament
        this.playSelectionPanel.append(this.playAiChoice);
        this.playSelectionPanel.append(this.playCasualChoice);
        this.playSelectionPanel.append(this.playLeagueChoice);
        this.playSelectionPanel.append(this.playTournamentChoice);
        panel.append(this.playSelectionPanel);

        this.playFormPanel = $("<div id='create-unranked-table' class='table-form' style='display:none'></div>");
        this.playFormTitle = $("<h1 class='play-form-heading'>Open Casual Table</h1>");
        this.playFormPanel.append(this.playFormTitle);

        this.playBotPanel = $("<div id='create-bot-table' class='table-form' style='display:none'></div>");
        this.playBotPanel.append("<h1 class='play-form-heading'>Open Bot Table</h1>");
        var botBlurb = $("<div class='flex-vert table-blurb'></div>");
        botBlurb.append(document.createTextNode("Welcome to the future! Here you may open a match against a computer opponent. This is an experimental feature, and so can go wrong in many ways."));
        botBlurb.append(document.createTextNode(" Remember:"));
        var botList = $("<ul></ul>");
        botList.append("<li>Bots will concede if they get confused or have an error.</li>");
        botList.append("<li>Bots will sometimes make dumb decisions that a human never would.</li>");
        botBlurb.append(botList);
        botBlurb.append(document.createTextNode("Please report a bug for any matches where you encounter a crash or other concession by the bot. Don't bother reporting bad gameplay; we already know they are dumb. :)"));
        this.playBotPanel.append(botBlurb);
        this.playBotFieldsHost = $("<div id='bot-table-options' class='inner-table-form'></div>");
        this.playBotPanel.append(this.playBotFieldsHost);

        this.playFormFields = $("<div class='inner-table-form'></div>");

        var formatRow = $("<div class='play-form-row'></div>");
        formatRow.append("<span class='play-form-label'>Format</span>");
        formatRow.append(this.supportedFormatsSelect);

        this.botPlayerDeckRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.botPlayerDeckRow.append("<span class='play-form-label'>Bot Deck from your Decks</span>");
        this.botPlayerDeckRow.append(this.botPlayerDeckSelect);

        this.botLibraryDeckRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.botLibraryDeckRow.append("<span class='play-form-label'>Bot Deck from Library</span>");
        this.botLibraryDeckRow.append(this.botLibraryDeckSelect);

        this.playerDeckRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.playerDeckLabel = $("<span class='play-form-label'>Your Deck</span>");
        this.playerDeckRow.append(this.playerDeckLabel);
        this.playerDeckRow.append(this.playerDeckSelect);

        this.libraryDeckRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.libraryDeckLabel = $("<span class='play-form-label'>Library Deck</span>");
        this.libraryDeckRow.append(this.libraryDeckLabel);
        this.libraryDeckRow.append(this.libraryDeckSelect);

        this.descRow = $("<div class='play-form-row'></div>");
        this.descRow.append("<span class='play-form-label'>Description</span>");
        this.descRow.append(this.tableDescInput);

        this.timerRow = $("<div class='play-form-row'></div>");
        this.timerRow.append("<span class='play-form-label'>Game Timer</span>");
        this.timerRow.append(this.timerSelect);

        this.inviteRow = $("<div class='play-form-row play-form-row-check'></div>");
        this.inviteRow.append(this.isInviteOnlyCheckbox);

        this.inviteeRow = $("<div class='play-form-row'></div>");
        this.inviteeRow.append("<span class='play-form-label'>Invitee</span>");
        this.inviteeRow.append(this.inviteeInput);
        this.inviteeRow.hide();

        this.privateRow = $("<div class='play-form-row play-form-row-check'></div>");
        this.privateRow.append(this.isPrivateCheckbox);
        this.privateRow.append("<span class='info-toggle' data-for='help-unranked-private' role='button' tabindex='0' title='What is this?' aria-expanded='false'>i</span>");
        this.privateHelp = $("<div id='help-unranked-private' class='info-text' style='display:none'>If checked, nobody can join your game to watch from the sidelines (except administrators).</div>");

        var keepOpenRow = $("<div class='play-form-row play-form-row-check'></div>");
        keepOpenRow.append(this.keepOpenCheckbox);

        var defaultFlowRow = $("<div class='play-form-row play-form-row-check'></div>");
        defaultFlowRow.append(this.defaultFlowCheckbox);

        this.playFormResult = $("<div class='play-form-result' role='status' aria-live='polite'></div>");

        var submitRow = $("<div class='play-form-row play-form-actions'></div>");
        submitRow.append(this.createTableButton);

        this.playFormFields.append(formatRow);
        this.playFormFields.append(this.aiControlsDiv);
        this.playFormFields.append(this.botPlayerDeckRow);
        this.playFormFields.append(this.botLibraryDeckRow);
        this.playFormFields.append(this.playerDeckRow);
        this.playFormFields.append(this.libraryDeckRow);
        this.playFormFields.append(this.descRow);
        this.playFormFields.append(this.timerRow);
        this.playFormFields.append(this.inviteRow);
        this.playFormFields.append(this.inviteeRow);
        this.playFormFields.append(this.privateRow);
        this.playFormFields.append(this.privateHelp);
        this.playFormFields.append(keepOpenRow);
        this.playFormFields.append(defaultFlowRow);
        this.playFormFields.append(this.playFormResult);
        this.playFormFields.append(submitRow);
        this.playFormPanel.append(this.playFormFields);

        this.invitePicker = new PlayerPicker(this.inviteeInput, {
            search:function (prefix, limit, done, failed) {
                that.comm.searchHallPlayers(prefix, limit, function (json) {
                    var names = (json && json.players) ? json.players : [];
                    done(names);
                }, {
                    "0":function () { failed(); },
                    "401":function () { failed(); },
                    "403":function () { failed(); },
                    "500":function () { failed(); }
                });
            },
            players:function () { return that.hallPlayerNames || []; },
            self:function () { return (that.userInfo && that.userInfo.name) ? that.userInfo.name : ""; }
        });
        if (this.hallPlayerNames && this.hallPlayerNames.length)
            this.invitePicker.setPlayers(this.hallPlayerNames);

        this.playLeagueEmpty = $("<div class='play-league-empty' style='display:none'></div>");
        this.playLeagueEmpty.append("<p class='play-subtitle'>You are not in any active league that offers a table format right now.</p>");
        this.playLeagueEmpty.append("<p class='play-subtitle'>Join a league from the <b>Events</b> tab (league results / join), then return here to open a league table.</p>");
        this.playFormPanel.append(this.playLeagueEmpty);

        panel.append(this.playFormPanel);
        panel.append(this.playBotPanel);

        // Slice 1.5b: LOTR CreateLeagueTable parity (hall.html #create-league-table + tables.css)
        this.playLeaguePanel = $("<div id='create-league-table' class='table-form play-league-panel-root flex-vert' style='display:none'></div>");

        var leagueOptions = $("<div id='league-table-options' class='inner-table-form'></div>");
        leagueOptions.append("<h1 class='play-form-heading play-league-heading'>Open League Table <span class='info-toggle' data-for='help-league' role='button' tabindex='0' title='What is this?' aria-expanded='false'>i</span></h1>");
        leagueOptions.append("<div id='help-league' class='info-text' style='display:none'>League games are ranked: each win or loss counts toward the league's standings, within the league's limits on games per serie and per opponent. They always use the Competitive timer and cannot be private or invite-only.</div>");

        var blurb = $("<div class='flex-vert table-blurb'></div>");
        blurb.append(document.createTextNode("Leagues are multi-week or multi-month events where players compete in many matches across days to improve their skills and hone their craft."));
        blurb.append($("<br/>"));
        blurb.append($("<br/>"));
        blurb.append(document.createTextNode("Join leagues below or using the Events tab at the top of the main hall. If you enter a Limited league (Sealed or Draft), you will need to receive your cards and construct a new deck using those cards before you can participate."));
        leagueOptions.append(blurb);

        this.leagueFormatSelect = $("<select id='league-format' class='flex-fill play-form-select'></select>");
        // Rebuild full multi next-steps on dropdown change (membership-driven; does not drop other leagues)
        this.leagueFormatSelect.change(function () {
            that.restoreLeagueNextSteps();
            that.refreshLeagueDecksForSelectedLeague();
        });
        var leagueFmtRow = $("<div class='flex-horiz play-form-row'></div>");
        leagueFmtRow.append("<div class='label-column'>League: </div>");
        leagueFmtRow.append(this.leagueFormatSelect);
        leagueOptions.append(leagueFmtRow);

        var playerDeckBlock = $("<div class='player-deck flex-vert'></div>");
        this.leagueDecksSelect = $("<select id='league-deck' class='player-deck-dropdown flex-fill play-form-select'></select>");
        var leagueDeckRow = $("<div class='flex-horiz play-form-row play-form-deck-row'></div>");
        leagueDeckRow.append("<div class='label-column'>Deck: </div>");
        leagueDeckRow.append(this.leagueDecksSelect);
        playerDeckBlock.append(leagueDeckRow);

        // SWCCG has getLibraryDecks / sample decks — wire Select Library Deck like LOTR SelectDeck.
        // Hidden for sealed / draft / cube (issued-card leagues); shown for constructed.
        this.leagueLibraryDecksSelect = $("<select id='league-library-deck' class='library-deck-dropdown flex-fill play-form-select'></select>");
        this.leagueLibraryRow = $("<div class='flex-horiz play-form-row play-form-deck-row'></div>");
        this.leagueLibraryRow.append("<div class='label-column'>Library Deck: <span class='info-toggle' data-for='help-library-league' role='button' tabindex='0' title='What is this?' aria-expanded='false'>i</span></div>");
        this.leagueLibraryRow.append(this.leagueLibraryDecksSelect);
        playerDeckBlock.append(this.leagueLibraryRow);
        this.leagueLibraryHelp = $("<div id='help-library-league' class='info-text' style='display:none'>The Deck Library contains sample decks you can use, including starter decks, past championship decks, and more.</div>");
        playerDeckBlock.append(this.leagueLibraryHelp);
        this.setLeagueLibraryDeckVisible(false);
        leagueOptions.append(playerDeckBlock);

        this.bindExclusiveDeckPair(this.leagueDecksSelect, this.leagueLibraryDecksSelect, null);

        this.leagueCreateButton = $("<button type='button' id='submit-league-table-button' class='table-create-button'>Create Table</button>");
        $(this.leagueCreateButton).button().click(function () {
            that.submitLeagueTable();
        });
        leagueOptions.append(this.leagueCreateButton);

        var resultRow = $("<div id='league-result-label' class='flex-horiz result-label'></div>");
        resultRow.append("<div class='label'><b>Result:</b></div>");
        this.leagueResultDiv = $("<div id='league-result' class='flex-fill result-box' role='status' aria-live='polite'>Ready.</div>");
        resultRow.append(this.leagueResultDiv);
        leagueOptions.append(resultRow);

        this.playLeaguePanel.append(leagueOptions);

        this.playLeaguePanel.append("<h1 class='play-form-heading'>Join Leagues</h1>");
        var playLeagueInner = $("<div id='play-league-panel' class='inner-table-form play-league-join-panel'></div>");
        this.playLeagueNextSteps = $("<div id='play-league-next-steps' class='play-next-steps' style='display:none'></div>");
        playLeagueInner.append(this.playLeagueNextSteps);
        playLeagueInner.append("<div class='page-hint'>All times are server time (UTC / GMT). Open a league for its details, standings and Join button.</div>");
        this.playLeagueList = $("<div id='play-league-list' class='event-list play-league-list'></div>");
        playLeagueInner.append(this.playLeagueList);
        this.playLeaguePanel.append(playLeagueInner);

        this.bindLeagueInfoToggles(this.playLeaguePanel);
        this.bindLeagueInfoToggles(this.playFormFields);
        this.bindLeagueInfoToggles(this.playBotPanel);

        panel.append(this.playLeaguePanel);

        this.playTournamentPanel = $("<div id='create-tournament-info' class='table-form play-tournament-info' style='display:none'></div>");
        this.playTournamentPanel.append("<h2 class='play-form-heading'>Create Tournament</h2>");
        var tFields = $("<div class='inner-table-form'></div>");

        var typeRow = $("<div class='play-form-row'></div>");
        typeRow.append("<span class='play-form-label'>Type</span>");
        this.tournamentTypeSelect = $("<select id='tournament-type' class='play-form-select'></select>");
        this.tournamentTypeSelect.append("<option value='constructed' selected='selected'>Constructed</option>");
        this.tournamentTypeSelect.append("<option value='sealed'>Sealed</option>");
        this.tournamentTypeSelect.append("<option value='draft'>Draft</option>");
        this.tournamentTypeSelect.append("<option value='cube'>Cube</option>");
        typeRow.append(this.tournamentTypeSelect);
        tFields.append(typeRow);

        var tFormatRow = $("<div class='play-form-row'></div>");
        tFormatRow.append("<span class='play-form-label'>Format</span>");
        this.tournamentFormatSelect = $("<select id='tournament-format' class='play-form-select'></select>");
        tFormatRow.append(this.tournamentFormatSelect);
        tFields.append(tFormatRow);

        this.tournamentProductRow = $("<div class='play-form-row' style='display:none'></div>");
        this.tournamentProductRow.append("<span class='play-form-label'>Product</span>");
        this.tournamentProductSelect = $("<select id='tournament-product' class='play-form-select'></select>");
        this.tournamentProductRow.append(this.tournamentProductSelect);
        tFields.append(this.tournamentProductRow);

        this.tournamentModeRow = $("<div class='play-form-row' style='display:none'></div>");
        this.tournamentModeRow.append("<span class='play-form-label'>Mode</span>");
        this.tournamentModeSelect = $("<select id='tournament-draft-mode' class='play-form-select'></select>");
        this.tournamentModeSelect.append("<option value='solo' selected='selected'>Solo</option>");
        this.tournamentModeSelect.append("<option value='live'>Live</option>");
        this.tournamentModeRow.append(this.tournamentModeSelect);
        tFields.append(this.tournamentModeRow);

        this.tournamentPacksRow = $("<div class='play-form-row' style='display:none'></div>");
        this.tournamentPacksRow.append("<span class='play-form-label'>Packs per side <span class='info-toggle' data-for='help-tournament-packs' role='button' tabindex='0' title='What is this?' aria-expanded='false'>i</span></span>");
        this.tournamentPacksSelect = $("<select id='tournament-pack-count' class='play-form-select'></select>");
        this.tournamentPacksRow.append(this.tournamentPacksSelect);
        tFields.append(this.tournamentPacksRow);
        tFields.append("<div id='help-tournament-packs' class='info-text' style='display:none'>Recommended pack count is selected by default. Cube uses 9-card packs. Live Cube has no AI seats.</div>");

        var pairRow = $("<div class='play-form-row'></div>");
        pairRow.append("<span class='play-form-label'>Pairing</span>");
        this.tournamentPairingSelect = $("<select id='tournament-pairing' class='play-form-select'></select>");
        this.tournamentPairingSelect.append("<option value='swiss'>Swiss</option>");
        this.tournamentPairingSelect.append("<option value='matchPlay'>Single Elimination Match Play</option>");
        pairRow.append(this.tournamentPairingSelect);
        tFields.append(pairRow);

        this.tournamentGamesRow = $("<div class='play-form-row'></div>");
        this.tournamentGamesRow.append("<span class='play-form-label'>Total Games <span class='info-toggle' data-for='help-tournament-games' role='button' tabindex='0' title='What is this?' aria-expanded='false'>i</span></span>");
        this.tournamentGamesSelect = $("<select id='tournament-total-games' class='play-form-select'></select>");
        var gi;
        for (gi = 2; gi <= 14; gi += 2)
            this.tournamentGamesSelect.append("<option value='" + gi + "'" + (gi === 4 ? " selected='selected'" : "") + ">" + gi + "</option>");
        this.tournamentGamesRow.append(this.tournamentGamesSelect);
        tFields.append(this.tournamentGamesRow);
        tFields.append("<div id='help-tournament-games' class='info-text' style='display:none'>SWCCG Tournaments need to have an equal amount of Dark Side and Light Side games played by each player to be fair.</div>");

        var maxRow = $("<div class='play-form-row'></div>");
        maxRow.append("<span class='play-form-label'>Max players</span>");
        this.tournamentMaxSelect = $("<select id='tournament-max-players' class='play-form-select'></select>");
        this.tournamentMaxSelect.append("<option value='2'>2</option>");
        this.tournamentMaxSelect.append("<option value='4' selected='selected'>4</option>");
        this.tournamentMaxSelect.append("<option value='8'>8</option>");
        this.tournamentMaxSelect.append("<option value='16'>16</option>");
        this.tournamentMaxSelect.append("<option value='32'>32</option>");
        this.tournamentMaxSelect.append("<option value='64'>64</option>");
        this.tournamentMaxSelect.append("<option value='128'>128</option>");
        maxRow.append(this.tournamentMaxSelect);
        tFields.append(maxRow);

        var readyRow = $("<div class='play-form-row'></div>");
        readyRow.append("<span class='play-form-label'>Ready check</span>");
        this.tournamentReadySelect = $("<select id='tournament-ready-check' class='play-form-select'></select>");
        this.tournamentReadySelect.append("<option value='0' selected='selected'>Off</option>");
        this.tournamentReadySelect.append("<option value='30'>30 seconds</option>");
        this.tournamentReadySelect.append("<option value='60'>60 seconds</option>");
        this.tournamentReadySelect.append("<option value='120'>2 minutes</option>");
        readyRow.append(this.tournamentReadySelect);
        tFields.append(readyRow);

        var titleRow = $("<div class='play-form-row'></div>");
        titleRow.append("<span class='play-form-label'>Title prefix</span>");
        this.tournamentTitleInput = $("<input type='text' id='tournament-title-prefix' class='play-form-select' maxlength='80' placeholder=\"optional, e.g. Bill's Friday Night SWCCG\" />");
        titleRow.append(this.tournamentTitleInput);
        tFields.append(titleRow);

        this.tournamentPrivateRow = $("<div class='play-form-row play-form-row-check'></div>");
        this.tournamentPrivateCheckbox = $("<input type='checkbox' id='tournament-private' />");
        this.tournamentPrivateRow.append(this.tournamentPrivateCheckbox);
        this.tournamentPrivateRow.append("<span class='play-form-label'>Private (no spectators)</span>");
        tFields.append(this.tournamentPrivateRow);

        this.tournamentLightPlayerSelect = $("<select class='play-form-select'></select>");
        this.tournamentLightLibrarySelect = $("<select class='play-form-select'></select>");
        this.bindExclusiveDeckPair(this.tournamentLightPlayerSelect, this.tournamentLightLibrarySelect, null);
        this.tournamentLightPlayerRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.tournamentLightPlayerRow.append("<span class='play-form-label'>Your Light Deck</span>");
        this.tournamentLightPlayerRow.append(this.tournamentLightPlayerSelect);
        tFields.append(this.tournamentLightPlayerRow);
        this.tournamentLightLibraryRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.tournamentLightLibraryRow.append("<span class='play-form-label'>Library Light Deck</span>");
        this.tournamentLightLibraryRow.append(this.tournamentLightLibrarySelect);
        tFields.append(this.tournamentLightLibraryRow);

        this.tournamentDarkPlayerSelect = $("<select class='play-form-select'></select>");
        this.tournamentDarkLibrarySelect = $("<select class='play-form-select'></select>");
        this.bindExclusiveDeckPair(this.tournamentDarkPlayerSelect, this.tournamentDarkLibrarySelect, null);
        this.tournamentDarkPlayerRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.tournamentDarkPlayerRow.append("<span class='play-form-label'>Your Dark Deck</span>");
        this.tournamentDarkPlayerRow.append(this.tournamentDarkPlayerSelect);
        tFields.append(this.tournamentDarkPlayerRow);
        this.tournamentDarkLibraryRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.tournamentDarkLibraryRow.append("<span class='play-form-label'>Library Dark Deck</span>");
        this.tournamentDarkLibraryRow.append(this.tournamentDarkLibrarySelect);
        tFields.append(this.tournamentDarkLibraryRow);

        this.tournamentResultDiv = $("<div class='play-form-result' role='status' aria-live='polite'></div>");
        tFields.append(this.tournamentResultDiv);

        var tSubmitRow = $("<div class='play-form-row play-form-actions'></div>");
        this.tournamentCreateButton = $("<button type='button' id='submit-tournament-button' class='table-create-button'>Create Tournament</button>");
        tSubmitRow.append(this.tournamentCreateButton);
        tFields.append(tSubmitRow);

        this.playTournamentPanel.append(tFields);
        this.bindLeagueInfoToggles(this.playTournamentPanel);
        panel.append(this.playTournamentPanel);

        var thatTourney = this;
        this.tournamentPairingSelect.change(function () {
            thatTourney.syncTournamentPairingUi();
        });
        this.tournamentMaxSelect.change(function () {
            thatTourney.prefillTournamentGamesFromMax();
        });
        this.tournamentTypeSelect.change(function () {
            thatTourney.syncTournamentTypeUi();
        });
        this.tournamentProductSelect.change(function () {
            thatTourney.syncTournamentProductUi();
        });
        this.tournamentModeSelect.change(function () {
            thatTourney.syncTournamentProductUi();
        });
        this.tournamentFormatSelect.change(function () {
            var fmt = thatTourney.tournamentFormatSelect.val();
            thatTourney.updateDecks(fmt, "default");
        });
        $(this.tournamentCreateButton).button().click(function () {
            thatTourney.submitCreateTournament();
        });
        this.loadTournamentProducts();

        this.playOverlay.append(backdrop);
        this.playOverlay.append(panel);
    },


    buildJoinOverlay:function() {
        var that = this;
        var existing = $("#join-table-popup");
        if (existing.length > 0) {
            this.joinOverlay = existing;
            this.joinOverlay.empty();
        } else {
            this.joinOverlay = $("<div id='join-table-popup' class='play-flow' style='display:none'></div>");
            $("body").append(this.joinOverlay);
        }

        var backdrop = $("<div class='play-flow-backdrop' aria-hidden='true'></div>");
        backdrop.click(function () { that.closeJoinOverlay(); });

        var panel = $("<div class='play-flow-panel' role='dialog' aria-modal='true' aria-label='Join'></div>");

        var header = $("<div class='play-flow-header'></div>");
        header.append($("<button type='button' class='play-back-button'>&lt; Back</button>").click(function () {
            that.closeJoinOverlay();
        }));
        header.append($("<button type='button' class='play-close-button' title='Close'>×</button>").click(function () {
            that.closeJoinOverlay();
        }));
        panel.append(header);

        var form = $("<div class='table-form join-table-form'></div>");
        this.joinTitleEl = $("<h2 class='play-form-heading'>Join Table</h2>");
        form.append(this.joinTitleEl);

        this.joinContextDiv = $("<div class='play-subtitle join-context'></div>");
        form.append(this.joinContextDiv);

        this.joinPlayerDeckSelect = $("<select class='play-form-select'></select>");
        this.joinLibraryDeckSelect = $("<select class='play-form-select'></select>");
        this.bindExclusiveDeckPair(this.joinPlayerDeckSelect, this.joinLibraryDeckSelect, null);

        this.joinSingleDeckBlock = $("<div class='join-single-deck'></div>");
        var deckRow = $("<div class='play-form-row play-form-deck-row'></div>");
        deckRow.append("<span class='play-form-label'>Your Deck</span>");
        deckRow.append(this.joinPlayerDeckSelect);
        this.joinSingleDeckBlock.append(deckRow);

        this.joinLibraryDeckRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.joinLibraryDeckRow.append("<span class='play-form-label'>Library Deck</span>");
        this.joinLibraryDeckRow.append(this.joinLibraryDeckSelect);
        this.joinSingleDeckBlock.append(this.joinLibraryDeckRow);
        form.append(this.joinSingleDeckBlock);

        this.joinDualDeckBlock = $("<div class='join-dual-deck' style='display:none'></div>");
        this.joinLightPlayerSelect = $("<select class='play-form-select'></select>");
        this.joinLightLibrarySelect = $("<select class='play-form-select'></select>");
        this.bindExclusiveDeckPair(this.joinLightPlayerSelect, this.joinLightLibrarySelect, null);
        this.joinDarkPlayerSelect = $("<select class='play-form-select'></select>");
        this.joinDarkLibrarySelect = $("<select class='play-form-select'></select>");
        this.bindExclusiveDeckPair(this.joinDarkPlayerSelect, this.joinDarkLibrarySelect, null);

        var lightPlayerRow = $("<div class='play-form-row play-form-deck-row'></div>");
        lightPlayerRow.append("<span class='play-form-label'>Your Light Deck</span>");
        lightPlayerRow.append(this.joinLightPlayerSelect);
        this.joinDualDeckBlock.append(lightPlayerRow);
        this.joinLightLibraryRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.joinLightLibraryRow.append("<span class='play-form-label'>Library Light Deck</span>");
        this.joinLightLibraryRow.append(this.joinLightLibrarySelect);
        this.joinDualDeckBlock.append(this.joinLightLibraryRow);

        var darkPlayerRow = $("<div class='play-form-row play-form-deck-row'></div>");
        darkPlayerRow.append("<span class='play-form-label'>Your Dark Deck</span>");
        darkPlayerRow.append(this.joinDarkPlayerSelect);
        this.joinDualDeckBlock.append(darkPlayerRow);
        this.joinDarkLibraryRow = $("<div class='play-form-row play-form-deck-row'></div>");
        this.joinDarkLibraryRow.append("<span class='play-form-label'>Library Dark Deck</span>");
        this.joinDarkLibraryRow.append(this.joinDarkLibrarySelect);
        this.joinDualDeckBlock.append(this.joinDarkLibraryRow);
        form.append(this.joinDualDeckBlock);

        this.joinResultDiv = $("<div class='join-result warningMessage' style='display:none'></div>");
        form.append(this.joinResultDiv);

        var submitRow = $("<div class='play-form-row play-form-actions'></div>");
        this.joinSubmitButton = $("<button type='button' class='play-submit-button table-create-button'>Join table</button>");
        $(this.joinSubmitButton).button().click(function () {
            that.submitJoin();
        });
        submitRow.append(this.joinSubmitButton);
        form.append(submitRow);

        panel.append(form);
        this.joinOverlay.append(backdrop);
        this.joinOverlay.append(panel);
    },

    parseHostSideFromPlayersStr:function(playersStr) {
        // Server embeds host side as "name (DARK)" / "name (LIGHT: Archetype)" / "(DARK)" when hidden.
        if (playersStr == null || playersStr === "")
            return null;
        if (/\(LIGHT/i.test(playersStr))
            return "light";
        if (/\(DARK/i.test(playersStr))
            return "dark";
        return null;
    },

    oppositeForceSide:function(side) {
        if (side === "light")
            return "dark";
        if (side === "dark")
            return "light";
        return null;
    },

    setJoinSubmitEnabled:function(enabled) {
        var button = $(this.joinSubmitButton);
        if (button == null || button.length === 0)
            return;
        if (button.hasClass("ui-button")) {
            button.button(enabled ? "enable" : "disable");
        } else {
            button.prop("disabled", !enabled);
        }
    },

    openJoinTablePopup:function(tableId, formatName, playersStr, formatCode, collectionCode) {
        var hostSide = this.parseHostSideFromPlayersStr(playersStr);
        var requiredSide = this.oppositeForceSide(hostSide);
        this.openJoinPopup({
            kind: "table",
            id: tableId,
            formatName: formatName || "",
            formatCode: formatCode || null,
            collectionCode: collectionCode || null,
            hostSide: hostSide,
            requiredSide: requiredSide,
            contextLabel: this.buildJoinContextLabel(formatName, playersStr, null, hostSide, requiredSide)
        });
    },

    openJoinQueuePopup:function(queueId, formatName, queueName, formatCode) {
        // Queues have no host side — show all decks legal for the queue format.
        this.openJoinPopup({
            kind: "queue",
            id: queueId,
            formatName: formatName || "",
            formatCode: formatCode || null,
            collectionCode: null,
            hostSide: null,
            requiredSide: null,
            contextLabel: this.buildJoinContextLabel(formatName, null, queueName, null, null)
        });
    },

    buildJoinContextLabel:function(formatName, playersStr, queueName, hostSide, requiredSide) {
        var parts = [];
        if (formatName)
            parts.push("Format: <b>" + $("<div>").text(formatName).html() + "</b>");
        if (playersStr)
            parts.push("Waiting: <b>" + $("<div>").text(playersStr).html() + "</b>");
        if (queueName)
            parts.push("Queue: <b>" + $("<div>").text(queueName).html() + "</b>");
        if (hostSide != null && requiredSide != null) {
            parts.push("Host: <b>" + hostSide.toUpperCase() + "</b> · you must play <b>" + requiredSide.toUpperCase() + "</b>");
        }
        return parts.join(" · ");
    },

    openJoinPopup:function(pending) {
        if (this.joinOverlay == null) {
            return;
        }
        this.joinPending = pending;
        this.joinResultDiv.hide().empty();
        this.setJoinSubmitEnabled(false);
        this.setJoinLibraryVisible(pending.kind !== "lockDecks");
        if (this.joinLightLibraryRow != null)
            pending.kind === "lockDecks" ? this.joinLightLibraryRow.hide() : this.joinLightLibraryRow.show();
        if (this.joinDarkLibraryRow != null)
            pending.kind === "lockDecks" ? this.joinDarkLibraryRow.hide() : this.joinDarkLibraryRow.show();
        this.joinPlayerDeckSelect.empty();
        this.joinLibraryDeckSelect.empty();
        var loading = $("<option></option>");
        loading.attr("value", "");
        loading.attr("disabled", "disabled");
        loading.attr("selected", "selected");
        loading.text("Loading decks...");
        this.joinPlayerDeckSelect.append(loading);
        this.joinLibraryDeckSelect.append($("<option></option>").attr("value", "").text("Select a Library Deck"));

        var isQueue = pending.kind === "queue" || pending.kind === "playerTournament" || pending.kind === "lockDecks";
        var isDual = pending.kind === "playerTournament" || pending.kind === "lockDecks";
        this.joinTitleEl.text(pending.kind === "lockDecks" ? "Lock Tournament Decks" : (isDual ? "Join Tournament" : (isQueue ? "Join Queue" : "Join Table")));
        var button = $(this.joinSubmitButton);
        var label = pending.kind === "lockDecks" ? "Lock decks" : (isDual ? "Join tournament" : (isQueue ? "Join queue" : "Join table"));
        if (this.joinSingleDeckBlock != null) {
            if (isDual)
                this.joinSingleDeckBlock.hide();
            else
                this.joinSingleDeckBlock.css("display", "flex");
        }
        if (this.joinDualDeckBlock != null) {
            if (isDual)
                this.joinDualDeckBlock.css("display", "flex");
            else
                this.joinDualDeckBlock.hide();
        }
        if (button.hasClass("ui-button")) {
            button.button("option", "label", label);
        } else {
            button.text(label);
        }

        if (pending.contextLabel) {
            this.joinContextDiv.html(pending.contextLabel).show();
        } else {
            this.joinContextDiv.hide().empty();
        }

        this.joinOverlay.css("display", "flex");
        $("body").addClass("play-flow-open");
        if (isDual)
            this.joinLightPlayerSelect.focus();
        else
            this.joinPlayerDeckSelect.focus();
        this.refreshExclusiveDeckPairHighlight(this.joinPlayerDeckSelect, this.joinLibraryDeckSelect);
        this.loadJoinDecks(pending.formatCode, pending.collectionCode, pending.requiredSide);
    },

    setJoinLibraryVisible:function(visible) {
        this._joinLibraryDeckVisible = !!visible;
        if (this.joinLibraryDeckRow != null) {
            if (visible) {
                this.joinLibraryDeckRow.show();
            } else {
                this.joinLibraryDeckRow.hide();
                if (this.joinLibraryDeckSelect != null)
                    this.joinLibraryDeckSelect.val("");
            }
        }
        this.refreshExclusiveDeckPairHighlight(this.joinPlayerDeckSelect, this.joinLibraryDeckSelect);
    },

    resolveJoinLibraryVisible:function(format, callback) {
        if (this.joinPending != null && this.joinPending.kind === "queue") {
            callback(true);
            return;
        }
        if (format == null || format === "" || this.selectHasValue(this.supportedFormatsSelect, format)) {
            callback(true);
            return;
        }
        var that = this;
        this.comm.getLeague(format, function (xml) {
            callback(!that.leagueUsesIssuedCards(xml));
        }, {
            "0": function() { callback(true); },
            "404": function() { callback(true); }
        });
    },

    loadJoinDecks:function(format, collection, requiredSide) {
        var that = this;
        var gen = (this.joinDeckLoadGen || 0) + 1;
        this.joinDeckLoadGen = gen;
        var errorMap = {
            "0": function() {
                if (gen !== that.joinDeckLoadGen)
                    return;
                that.showJoinError("Could not load decks. Try again.");
            }
        };
        var fetchDecks = function(showLibrary) {
            if (gen !== that.joinDeckLoadGen)
                return;
            that.setJoinLibraryVisible(showLibrary);
            that.comm.getDecks(function (xml) {
                if (gen !== that.joinDeckLoadGen)
                    return;
                if (!showLibrary) {
                    that.applyJoinDeckXml(xml, null, requiredSide, false);
                    return;
                }
                that.comm.getLibraryDecks(function (xml2) {
                    if (gen !== that.joinDeckLoadGen)
                        return;
                    that.applyJoinDeckXml(xml, xml2, requiredSide, true);
                }, errorMap, format, collection);
            }, errorMap, format, collection);
        };
        this.resolveJoinLibraryVisible(format, fetchDecks);
    },

    applyJoinDeckXml:function(playerXml, libraryXml, requiredSide, showLibrary) {
        if (this.joinOverlay == null || !this.joinOverlay.is(":visible") || this.joinPending == null) {
            return;
        }
        if (showLibrary == null)
            showLibrary = this._joinLibraryDeckVisible !== false;
        var playerSelect = this.joinPlayerDeckSelect;
        var libSelect = this.joinLibraryDeckSelect;
        playerSelect.empty();
        libSelect.empty();
        libSelect.append($("<option></option>").attr("value", "").text("Select a Library Deck"));
        var playerCount = 0;
        var libCount = 0;
        if (playerXml != null) {
            var root = playerXml.documentElement;
            if (root != null && root.tagName == "decks") {
                playerCount += this.generateDeckRow(root.getElementsByTagName("darkDeck"), "[DARK] ", "false", "dark", playerSelect, requiredSide);
                playerCount += this.generateDeckRow(root.getElementsByTagName("lightDeck"), "[LIGHT] ", "false", "light", playerSelect, requiredSide);
                playerCount += this.generateDeckRow(root.getElementsByTagName("otherDeck"), "[UNKNOWN] ", "false", "other", playerSelect, requiredSide);
            }
        }
        if (showLibrary && libraryXml != null) {
            var libRoot = libraryXml.documentElement;
            if (libRoot != null && libRoot.tagName == "decks") {
                libCount += this.generateDeckRow(libRoot.getElementsByTagName("darkDeck"), "[DARK] ", "true", "dark", libSelect, requiredSide);
                libCount += this.generateDeckRow(libRoot.getElementsByTagName("lightDeck"), "[LIGHT] ", "true", "light", libSelect, requiredSide);
                libCount += this.generateDeckRow(libRoot.getElementsByTagName("otherDeck"), "[UNKNOWN] ", "true", "other", libSelect, requiredSide);
            }
        }
        if (playerCount === 0) {
            var emptyLabel = (requiredSide != null && requiredSide !== "") ? "No opposite-side decks found" : "You have no decks yet";
            playerSelect.empty();
            playerSelect.append($("<option></option>").attr("value", "").text(emptyLabel));
        } else {
            playerSelect.prepend($("<option></option>").attr("value", "").text("Choose one of your decks"));
            playerSelect.val(playerSelect.find("option").eq(1).attr("value"));
            libSelect.val("");
        }
        if (playerCount === 0 && showLibrary && libCount > 0)
            libSelect.val(libSelect.find("option").eq(1).attr("value"));

        this.joinResultDiv.hide().empty();
        if (this.joinPending != null && (this.joinPending.kind === "playerTournament" || this.joinPending.kind === "lockDecks")) {
            this.fillJoinDualDeckSelects(playerXml, libraryXml, this.joinPending.kind !== "lockDecks" && showLibrary);
            return;
        }
        var total = playerCount + (showLibrary ? libCount : 0);
        if (total === 0) {
            if (requiredSide != null && requiredSide !== "") {
                this.showJoinError("No opposite-side decks found. Build or import a " +
                    requiredSide.toUpperCase() +
                    " deck legal for this event, then try again.");
            } else {
                this.showJoinError("No decks found for this format. Build or import a deck, then try again.");
            }
            this.setJoinSubmitEnabled(false);
        } else {
            this.setJoinSubmitEnabled(true);
        }
        this.refreshExclusiveDeckPairHighlight(playerSelect, libSelect);
    },

    closeJoinOverlay:function() {
        if (this.joinOverlay == null) {
            return;
        }
        this.joinOverlay.hide();
        this.joinPending = null;
        this.joinDeckLoadGen = (this.joinDeckLoadGen || 0) + 1;
        this.joinResultDiv.hide().empty();
        if (this.playOverlay == null || !this.playOverlay.is(":visible")) {
            $("body").removeClass("play-flow-open");
        }
    },

    showJoinError:function(message) {
        if (this.joinResultDiv == null) {
            this.chat.appendMessage(message, "warningMessage");
            return;
        }
        this.joinResultDiv.text(message).show();
    },

    submitJoin:function() {
        var that = this;
        if (this.joinPending == null) {
            return;
        }
        var pending = this.joinPending;
        if (pending.kind === "playerTournament" || pending.kind === "lockDecks") {
            this.submitPlayerMadeJoin(pending);
            return;
        }
        var picked = this.selectedDeckFromPair(this.joinPlayerDeckSelect, this.joinLibraryDeckSelect);
        if (picked == null || picked.name == null || picked.name === "") {
            this.showJoinError("You must select a deck.");
            return;
        }
        if (this.joinPending.requiredSide != null && picked.side != null && picked.side !== this.joinPending.requiredSide) {
            this.showJoinError("That deck is the wrong Force side. You must play " +
                this.joinPending.requiredSide.toUpperCase() + ".");
            return;
        }
        var deck = picked.name;
        var sampleDeck = picked.sample;
        var button = $(this.joinSubmitButton);
        if (button.hasClass("ui-button")) {
            button.button("disable");
        }

        var onDone = function() {
            setTimeout(function() {
                if (button.hasClass("ui-button")) {
                    button.button("enable");
                }
            }, 1500);
        };

        var errorMap = {
            "0": function() { that.showJoinError("Server unavailable or connection problem."); onDone(); },
            "400": function(xhr) {
                var message = xhr.getResponseHeader("message");
                that.showJoinError(message != null ? message : "Bad request. Check your deck and try again.");
                onDone();
            },
            "401": function() { that.showJoinError("You are not logged in."); onDone(); },
            "403": function() { that.showJoinError("You do not have permission to join."); onDone(); },
            "404": function() { that.showJoinError("Table or queue not found."); onDone(); },
            "410": function() { that.showJoinError("Session expired. Refresh the page."); onDone(); },
            "500": function() { that.showJoinError("Server error. Try again."); onDone(); }
        };

        var handleXml = function(xml) {
            onDone();
            if (xml != null) {
                var root = xml.documentElement;
                if (root != null && root.tagName == "error") {
                    var message = root.getAttribute("message");
                    that.showJoinError(message != null ? message : "Unable to join.");
                    that.chat.appendMessage(message, "warningMessage");
                    return;
                }
                if (root != null && root.tagName == "response") {
                    var msg = root.getAttribute("message");
                    if (msg)
                        that.chat.appendMessage(msg, "warningMessage");
                }
            }
            that.closeJoinOverlay();
        };

        if (pending.kind === "queue") {
            that.comm.joinQueue(pending.id, deck, sampleDeck, handleXml, errorMap);
        } else {
            that.comm.joinTable(pending.id, deck, sampleDeck, handleXml, errorMap);
        }
    },


    openPlayOverlay:function() {
        if (this.playOverlay == null) {
            return;
        }
        var flow = $.cookie("play-default-flow");
        if (flow === "ai") {
            this.showPlayForm("ai");
        } else if (flow === "casual") {
            this.showPlayForm("casual");
        } else {
            this.showPlaySelection();
        }
        this.playOverlay.css("display", "flex");
        $("body").addClass("play-flow-open");
        this.playBackButton.focus();
    },

    closePlayOverlay:function() {
        if (this.playOverlay == null) {
            return;
        }
        this.playOverlay.hide();
        $("body").removeClass("play-flow-open");
        this.playMode = null;
        if (this.playBotPanel != null) {
            this.playBotPanel.hide();
        }
        if (this.playTournamentPanel != null) {
            this.playTournamentPanel.hide();
        }
        if (this.playLeaguePanel != null) {
            this.playLeaguePanel.hide();
        }
        this.playOverlay.removeClass("play-flow-league");
    },

    setPlayFlowTitle:function(suffix) {
        if (this.playFlowTitle == null) {
            return;
        }
        if (suffix == null || suffix === "") {
            this.playFlowTitle.text("Create Table");
        } else {
            this.playFlowTitle.text("Create Table ▸ " + suffix);
        }
    },

    bindLeagueInfoToggles:function(root) {
        // jQuery 1.6.2: .click / .bind only (no .on)
        $(root).find(".info-toggle").each(function () {
            var toggle = $(this);
            if (!toggle.attr("role")) {
                toggle.attr("role", "button");
            }
            if (!toggle.attr("tabindex")) {
                toggle.attr("tabindex", "0");
            }
            toggle.unbind("click.leagueInfo").bind("click.leagueInfo", function (event) {
                if (event && event.preventDefault) {
                    event.preventDefault();
                }
                var id = toggle.attr("data-for");
                if (!id) {
                    return;
                }
                var target = $("#" + id);
                if (target.is(":visible")) {
                    target.hide();
                    toggle.attr("aria-expanded", "false");
                } else {
                    target.show();
                    toggle.attr("aria-expanded", "true");
                }
            });
        });
    },

    showPlaySelection:function() {
        this.playMode = null;
        this.playFormPanel.hide();
        if (this.playBotPanel != null) {
            this.playBotPanel.hide();
        }
        if (this.playTournamentPanel != null) {
            this.playTournamentPanel.hide();
        }
        if (this.playLeaguePanel != null) {
            this.playLeaguePanel.hide();
        }
        this.playOverlay.removeClass("play-flow-league");
        this.parkPlayFormFields();
        this.playSelectionPanel.show();
        this.setPlayFlowTitle(null);
        this.playBackButton.html("&lt; Back");
        // LOTR always offers Play Against Bots as the first Create Table option.
        if (this.playAiChoice != null) {
            this.playAiChoice.show();
        }
    },

    parkPlayFormFields:function() {
        if (this.playFormFields != null && this.playFormPanel != null)
            this.playFormPanel.append(this.playFormFields);
    },

    showTournamentInfo:function() {
        this.playMode = "tournament";
        this.playSelectionPanel.hide();
        this.playFormPanel.hide();
        if (this.playBotPanel != null) {
            this.playBotPanel.hide();
        }
        if (this.playLeaguePanel != null) {
            this.playLeaguePanel.hide();
        }
        this.playOverlay.removeClass("play-flow-league");
        this.parkPlayFormFields();
        this.setPlayFlowTitle("Tournament");
        this.populateTournamentFormats();
        this.syncTournamentTypeUi();
        this.syncTournamentPairingUi();
        var fmt = this.tournamentFormatSelect != null ? this.tournamentFormatSelect.val() : null;
        if (fmt)
            this.updateDecks(fmt, "default");
        this.fillTournamentDeckSelects();
        this.playTournamentPanel.show();
    },

    loadTournamentProducts:function() {
        var that = this;
        if (this.comm == null)
            return;
        this.comm.getTournamentProducts(function (xml) {
            that.tournamentProducts = [];
            if (xml == null || xml.documentElement == null)
                return;
            var nodes = xml.documentElement.getElementsByTagName("product");
            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                that.tournamentProducts.push({
                    code: node.getAttribute("code"),
                    kind: node.getAttribute("kind"),
                    name: node.getAttribute("name"),
                    formatCode: node.getAttribute("formatCode"),
                    liveMax: parseInt(node.getAttribute("liveMax"), 10) || 128,
                    defaultPacks: parseInt(node.getAttribute("defaultPacks"), 10) || 1,
                    jsonCube: node.getAttribute("jsonCube") == "true",
                    wattoCube: node.getAttribute("wattoCube") == "true",
                    packChoices: node.getAttribute("packChoices") || ""
                });
            }
            that.syncTournamentTypeUi();
        }, {
            "0": function() {}
        });
    },

    tournamentTypeValue:function() {
        return this.tournamentTypeSelect != null ? (this.tournamentTypeSelect.val() || "constructed") : "constructed";
    },

    isLimitedTournamentType:function() {
        var type = this.tournamentTypeValue();
        return type === "sealed" || type === "draft" || type === "cube";
    },

    selectedTournamentProduct:function() {
        if (this.tournamentProducts == null || this.tournamentProductSelect == null)
            return null;
        var code = this.tournamentProductSelect.val();
        for (var i = 0; i < this.tournamentProducts.length; i++) {
            if (this.tournamentProducts[i].code === code)
                return this.tournamentProducts[i];
        }
        return null;
    },

    populateTournamentFormats:function() {
        if (this.tournamentFormatSelect == null || this.supportedFormatsSelect == null)
            return;
        if (this.isLimitedTournamentType())
            return;
        var prev = this.tournamentFormatSelect.val();
        this.tournamentFormatSelect.empty();
        var that = this;
        this.supportedFormatsSelect.find("option").each(function () {
            var opt = $(this);
            var value = opt.attr("value");
            var label = opt.text();
            if (value == null || value === "")
                return;
            if (/sealed|draft|cube/i.test(label) || /sealed|draft|cube/i.test(value))
                return;
            that.tournamentFormatSelect.append($("<option></option>").attr("value", value).text(label));
        });
        if (that.selectHasValue(that.tournamentFormatSelect, "anything_goes"))
            that.tournamentFormatSelect.val("anything_goes");
        else if (prev && that.selectHasValue(that.tournamentFormatSelect, prev))
            that.tournamentFormatSelect.val(prev);
        else
            that.tournamentFormatSelect.val(that.tournamentFormatSelect.find("option").eq(0).attr("value"));
    },

    populateTournamentProducts:function() {
        if (this.tournamentProductSelect == null)
            return;
        var type = this.tournamentTypeValue();
        var prev = this.tournamentProductSelect.val();
        this.tournamentProductSelect.empty();
        var products = this.tournamentProducts || [];
        for (var i = 0; i < products.length; i++) {
            var product = products[i];
            if (product.kind !== type)
                continue;
            this.tournamentProductSelect.append($("<option></option>").attr("value", product.code).text(product.name));
        }
        if (prev && this.selectHasValue(this.tournamentProductSelect, prev))
            this.tournamentProductSelect.val(prev);
        else
            this.tournamentProductSelect.val(this.tournamentProductSelect.find("option").eq(0).attr("value"));
    },

    syncTournamentTypeUi:function() {
        var limited = this.isLimitedTournamentType();
        var type = this.tournamentTypeValue();
        if (this.tournamentProductRow != null)
            limited ? this.tournamentProductRow.show() : this.tournamentProductRow.hide();
        if (this.tournamentModeRow != null)
            (type === "draft" || type === "cube") ? this.tournamentModeRow.show() : this.tournamentModeRow.hide();
        if (this.tournamentPacksRow != null)
            (type === "draft" || type === "cube") ? this.tournamentPacksRow.show() : this.tournamentPacksRow.hide();
        var deckDisplay = limited ? "none" : "";
        if (this.tournamentLightPlayerRow != null) this.tournamentLightPlayerRow.css("display", deckDisplay);
        if (this.tournamentLightLibraryRow != null) this.tournamentLightLibraryRow.css("display", deckDisplay);
        if (this.tournamentDarkPlayerRow != null) this.tournamentDarkPlayerRow.css("display", deckDisplay);
        if (this.tournamentDarkLibraryRow != null) this.tournamentDarkLibraryRow.css("display", deckDisplay);
        if (limited)
            this.populateTournamentProducts();
        else
            this.populateTournamentFormats();
        this.syncTournamentProductUi();
    },

    syncTournamentProductUi:function() {
        var product = this.selectedTournamentProduct();
        var type = this.tournamentTypeValue();
        if (this.tournamentFormatSelect != null && product != null && product.formatCode)
            this.tournamentFormatSelect.val(product.formatCode);
        if (this.tournamentPacksSelect != null && (type === "draft" || type === "cube")) {
            var prev = this.tournamentPacksSelect.val();
            this.tournamentPacksSelect.empty();
            var choices = product && product.packChoices ? product.packChoices.split(",") : ["4", "6", "8"];
            var def = product ? String(product.defaultPacks) : "6";
            for (var i = 0; i < choices.length; i++) {
                var value = $.trim(choices[i]);
                if (!value)
                    continue;
                this.tournamentPacksSelect.append($("<option></option>").attr("value", value).text(value));
            }
            if (this.selectHasValue(this.tournamentPacksSelect, def))
                this.tournamentPacksSelect.val(def);
            else if (prev && this.selectHasValue(this.tournamentPacksSelect, prev))
                this.tournamentPacksSelect.val(prev);
        }
        this.clampTournamentMaxForLiveCube();
        if (this.tournamentFormatSelect != null && this.tournamentFormatSelect.val())
            this.updateDecks(this.tournamentFormatSelect.val(), "default");
    },

    clampTournamentMaxForLiveCube:function() {
        if (this.tournamentMaxSelect == null)
            return;
        var product = this.selectedTournamentProduct();
        var live = this.tournamentModeSelect != null && this.tournamentModeSelect.val() === "live";
        var cap = 128;
        if (live && product != null && this.tournamentTypeValue() === "cube")
            cap = product.liveMax || cap;
        var current = parseInt(this.tournamentMaxSelect.val(), 10) || 4;
        this.tournamentMaxSelect.find("option").each(function () {
            var value = parseInt($(this).attr("value"), 10);
            if (value > cap)
                $(this).attr("disabled", "disabled");
            else
                $(this).removeAttr("disabled");
        });
        if (current > cap) {
            this.tournamentMaxSelect.val(String(cap));
            this.prefillTournamentGamesFromMax();
        }
    },

    recommendedTournamentGames:function(playerCount) {
        var n = parseInt(playerCount, 10);
        if (isNaN(n) || n <= 2)
            return 2;
        if (n <= 4)
            return 4;
        if (n <= 8)
            return 6;
        if (n <= 16)
            return 8;
        if (n <= 32)
            return 10;
        if (n <= 64)
            return 12;
        return 14;
    },

    prefillTournamentGamesFromMax:function() {
        if (this.tournamentGamesSelect == null || this.tournamentMaxSelect == null)
            return;
        if (this.tournamentPairingSelect != null && this.tournamentPairingSelect.val() === "matchPlay")
            return;
        var rec = this.recommendedTournamentGames(this.tournamentMaxSelect.val());
        this.tournamentGamesSelect.val(String(rec));
    },

    syncTournamentPairingUi:function() {
        var matchPlay = this.tournamentPairingSelect != null && this.tournamentPairingSelect.val() === "matchPlay";
        if (this.tournamentGamesRow != null) {
            if (matchPlay)
                this.tournamentGamesRow.hide();
            else
                this.tournamentGamesRow.show();
        }
        if (!matchPlay)
            this.prefillTournamentGamesFromMax();
    },

    fillTournamentDeckSelects:function() {
        if (this.tournamentLightPlayerSelect == null)
            return;
        this.fillDeckSelect(this.tournamentLightPlayerSelect, false, "light", "Choose one of your Light decks", true);
        this.fillDeckSelect(this.tournamentLightLibrarySelect, true, "light", "Select a Library Light Deck", false);
        this.fillDeckSelect(this.tournamentDarkPlayerSelect, false, "dark", "Choose one of your Dark decks", true);
        this.fillDeckSelect(this.tournamentDarkLibrarySelect, true, "dark", "Select a Library Dark Deck", false);
        this.refreshExclusiveDeckPairHighlight(this.tournamentLightPlayerSelect, this.tournamentLightLibrarySelect);
        this.refreshExclusiveDeckPairHighlight(this.tournamentDarkPlayerSelect, this.tournamentDarkLibrarySelect);
    },

    fillJoinDualDeckSelects:function(playerXml, libraryXml, showLibrary) {
        var fillSide = function(playerSelect, libSelect, side, playerXml, libraryXml, showLibrary, generate) {
            playerSelect.empty();
            libSelect.empty();
            libSelect.append($("<option></option>").attr("value", "").text("Select a Library Deck"));
            var tag = side === "light" ? "lightDeck" : "darkDeck";
            var prefix = side === "light" ? "[LIGHT] " : "[DARK] ";
            var playerCount = 0;
            var libCount = 0;
            if (playerXml != null && playerXml.documentElement != null)
                playerCount = generate(playerXml.documentElement.getElementsByTagName(tag), prefix, "false", side, playerSelect, side);
            if (showLibrary && libraryXml != null && libraryXml.documentElement != null)
                libCount = generate(libraryXml.documentElement.getElementsByTagName(tag), prefix, "true", side, libSelect, side);
            if (playerCount === 0) {
                playerSelect.append($("<option></option>").attr("value", "").text("No " + side + " decks found"));
            } else {
                playerSelect.prepend($("<option></option>").attr("value", "").text("Choose one of your decks"));
                playerSelect.val(playerSelect.find("option").eq(1).attr("value"));
                libSelect.val("");
            }
            if (playerCount === 0 && showLibrary && libCount > 0)
                libSelect.val(libSelect.find("option").eq(1).attr("value"));
            return playerCount + (showLibrary ? libCount : 0);
        };
        var generate = $.proxy(this.generateDeckRow, this);
        var lightTotal = fillSide(this.joinLightPlayerSelect, this.joinLightLibrarySelect, "light", playerXml, libraryXml, showLibrary, generate);
        var darkTotal = fillSide(this.joinDarkPlayerSelect, this.joinDarkLibrarySelect, "dark", playerXml, libraryXml, showLibrary, generate);
        this.refreshExclusiveDeckPairHighlight(this.joinLightPlayerSelect, this.joinLightLibrarySelect);
        this.refreshExclusiveDeckPairHighlight(this.joinDarkPlayerSelect, this.joinDarkLibrarySelect);
        if (lightTotal === 0 || darkTotal === 0) {
            this.showJoinError("You need both a Light Side deck and a Dark Side deck legal for this format.");
            this.setJoinSubmitEnabled(false);
        } else {
            this.setJoinSubmitEnabled(true);
        }
    },

    submitPlayerMadeJoin:function(pending) {
        var that = this;
        var light = this.selectedDeckFromPair(this.joinLightPlayerSelect, this.joinLightLibrarySelect);
        var dark = this.selectedDeckFromPair(this.joinDarkPlayerSelect, this.joinDarkLibrarySelect);
        if (light == null || dark == null) {
            this.showJoinError("Select both a Light Side deck and a Dark Side deck.");
            return;
        }
        if (light.side != null && light.side !== "light") {
            this.showJoinError("The Light Side selection must be a Light Side deck.");
            return;
        }
        if (dark.side != null && dark.side !== "dark") {
            this.showJoinError("The Dark Side selection must be a Dark Side deck.");
            return;
        }
        var button = $(this.joinSubmitButton);
        if (button.hasClass("ui-button"))
            button.button("disable");
        var onDone = function() {
            setTimeout(function() {
                if (button.hasClass("ui-button"))
                    button.button("enable");
            }, 1500);
        };
        var errorMap = {
            "0": function() { that.showJoinError("Server unavailable or connection problem."); onDone(); },
            "400": function(xhr) {
                var message = xhr.getResponseHeader("message");
                that.showJoinError(message != null ? message : "Bad request. Check your decks and try again.");
                onDone();
            },
            "401": function() { that.showJoinError("You are not logged in."); onDone(); },
            "404": function() { that.showJoinError("Tournament not found."); onDone(); },
            "500": function() { that.showJoinError("Server error. Try again."); onDone(); }
        };
        var submitFn = pending.kind === "lockDecks"
            ? function(cb, errors) { that.comm.lockTournamentDecks(pending.id, light.name, light.sample, dark.name, dark.sample, cb, errors); }
            : function(cb, errors) { that.comm.joinPlayerMadeQueue(pending.id, light.name, light.sample, dark.name, dark.sample, cb, errors); };
        submitFn(function (xml) {
            onDone();
            if (xml != null && xml.documentElement != null && xml.documentElement.tagName == "error") {
                that.showJoinError(xml.documentElement.getAttribute("message") || "Unable to join.");
                return;
            }
            that.closeJoinOverlay();
        }, errorMap);
    },

    openLimitedTournamentTab:function(kind, tournamentId, collectionCode, cubeSoloType) {
        var url;
        if (kind === "draft") {
            if (cubeSoloType)
                url = "/gemp-swccg/soloDraft.html?leagueType=" + encodeURIComponent(collectionCode || cubeSoloType);
            else
                url = "/gemp-swccg/draft.html?tournamentId=" + encodeURIComponent(tournamentId)
                    + (collectionCode ? "&collection=" + encodeURIComponent(collectionCode) : "");
        } else {
            url = "/gemp-swccg/deckBuild.html?collection=" + encodeURIComponent(collectionCode || "")
                + "&product=" + encodeURIComponent("all");
        }
        window.open(url, "_blank");
    },

    maybePromptLimitedStart:function(tournamentId, stage, collectionCode, cubeSoloType, name) {
        if (this.promptedLimited == null)
            this.promptedLimited = {};
        if (stage !== "Drafting" && stage !== "Deck building")
            return;
        var key = tournamentId + ":" + stage;
        if (this.promptedLimited[key])
            return;
        this.promptedLimited[key] = true;
        var that = this;
        var kind = stage === "Drafting" ? "draft" : "deck";
        var label = stage === "Drafting" ? "Open draft" : "Open deck builder";
        var content = $("<div></div>");
        content.append($("<p></p>").text((name || "Your tournament") + " is in " + stage.toLowerCase() + "."));
        content.dialog({
            title: stage,
            modal: true,
            resizable: false,
            width: 360,
            closeText: "",
            buttons: [
                {
                    text: label,
                    click: function () {
                        that.openLimitedTournamentTab(kind, tournamentId, collectionCode, cubeSoloType);
                        $(this).dialog("close");
                    }
                },
                {
                    text: "Later",
                    click: function () {
                        $(this).dialog("close");
                    }
                }
            ],
            close: function () {
                content.dialog("destroy").remove();
            }
        });
    },

    submitCreateTournament:function() {
        var that = this;
        var type = this.tournamentTypeValue();
        var limited = this.isLimitedTournamentType();
        var light = limited ? {name: "", sample: "false"} : this.selectedDeckFromPair(this.tournamentLightPlayerSelect, this.tournamentLightLibrarySelect);
        var dark = limited ? {name: "", sample: "false"} : this.selectedDeckFromPair(this.tournamentDarkPlayerSelect, this.tournamentDarkLibrarySelect);
        if (!limited && (light == null || dark == null)) {
            this.showTournamentError("Select both a Light Side deck and a Dark Side deck.");
            return;
        }
        var product = this.selectedTournamentProduct();
        if (limited && product == null) {
            this.showTournamentError("Choose a " + type + " product.");
            return;
        }
        var pairing = this.tournamentPairingSelect.val() || "swiss";
        var params = {
            type: type,
            formatCode: product != null ? product.formatCode : this.tournamentFormatSelect.val(),
            productCode: product != null ? product.code : "",
            draftMode: (type === "draft" || type === "cube") ? (this.tournamentModeSelect.val() || "solo") : "",
            packCount: (type === "draft" || type === "cube") ? (this.tournamentPacksSelect.val() || "0") : "0",
            pairing: pairing,
            totalGames: this.tournamentGamesSelect.val() || "4",
            maxPlayers: this.tournamentMaxSelect.val() || "4",
            readyCheckSeconds: this.tournamentReadySelect.val() || "0",
            privateEvent: this.tournamentPrivateCheckbox != null && this.tournamentPrivateCheckbox.is(":checked") ? "true" : "false",
            titlePrefix: this.tournamentTitleInput != null ? this.tournamentTitleInput.val() : "",
            lightDeckName: light.name,
            lightSampleDeck: light.sample,
            darkDeckName: dark.name,
            darkSampleDeck: dark.sample
        };
        var button = $(this.tournamentCreateButton);
        if (button.hasClass("ui-button"))
            button.button("disable");
        var onDone = function() {
            setTimeout(function() {
                if (button.hasClass("ui-button"))
                    button.button("enable");
            }, 1500);
        };
        this.comm.createTournament(params, function (xml) {
            onDone();
            if (xml != null && xml.documentElement != null && xml.documentElement.tagName == "error") {
                that.showTournamentError(xml.documentElement.getAttribute("message") || "Unable to create tournament.");
                return;
            }
            that.closePlayOverlay();
        }, {
            "0": function() { that.showTournamentError("Server unavailable or connection problem."); onDone(); },
            "400": function() { that.showTournamentError("Bad request. Check your decks and options."); onDone(); },
            "401": function() { that.showTournamentError("You are not logged in."); onDone(); },
            "500": function() { that.showTournamentError("Server error. Try again."); onDone(); }
        });
    },

    showTournamentError:function(message) {
        if (this.tournamentResultDiv == null) {
            this.chat.appendMessage(message, "warningMessage");
            return;
        }
        this.tournamentResultDiv.text(message);
        this.chat.appendMessage(message, "warningMessage");
    },

    showPlayForm:function(mode) {
        // Slice 1.5: league uses its own full panel
        if (mode === "league") {
            this.showLeaguePanel();
            return;
        }
        this.playMode = mode;
        this.playSelectionPanel.hide();
        if (this.playTournamentPanel != null) {
            this.playTournamentPanel.hide();
        }
        if (this.playLeaguePanel != null) {
            this.playLeaguePanel.hide();
        }
        this.playOverlay.removeClass("play-flow-league");

        if (this.playLeagueEmpty != null) {
            this.playLeagueEmpty.hide();
        }
        this.playFormFields.show();

        if (mode === "ai") {
            this.setPlayFlowTitle("Bot");
            this.opponentSelect.val("ai");
            if (this.libraryDeckLabel)
                this.libraryDeckLabel.text("Your Deck from Library");
            this.playFormPanel.hide();
            if (this.playBotFieldsHost != null)
                this.playBotFieldsHost.append(this.playFormFields);
            this.playBotPanel.show();
        } else {
            this.setPlayFlowTitle("Casual");
            this.playFormTitle.text("Open Casual Table");
            this.opponentSelect.val("human");
            if (this.libraryDeckLabel)
                this.libraryDeckLabel.text("Library Deck");
            if (this.playBotPanel != null)
                this.playBotPanel.hide();
            this.playFormPanel.append(this.playFormFields);
            this.playFormPanel.show();
        }

        this.applyCasualBotFieldVisibility();
        this.syncDefaultFlowCheckbox();
        this.clearPlayFormResult();

        this.filterFormatsForPlayMode(mode);
        var fmt = this.supportedFormatsSelect.val();
        this.updateDecks(fmt, "default");
        this.updateAiDecksForSelection();
        this.updateCreateTableLabel();

        if (this.supportedFormatsInitialized) {
            this.supportedFormatsSelect.css("display", "");
            this.createTableButton.css("display", "");
        }
    },

    applyCasualBotFieldVisibility:function() {
        var ai = this.playMode === "ai";
        if (this.aiControlsDiv) {
            if (ai)
                this.aiControlsDiv.show();
            else
                this.aiControlsDiv.hide();
        }
        if (this.botPlayerDeckRow)
            ai ? this.botPlayerDeckRow.show() : this.botPlayerDeckRow.hide();
        if (this.botLibraryDeckRow)
            ai ? this.botLibraryDeckRow.show() : this.botLibraryDeckRow.hide();
        if (this.descRow)
            ai ? this.descRow.hide() : this.descRow.show();
        if (this.timerRow)
            ai ? this.timerRow.hide() : this.timerRow.show();
        if (this.inviteRow)
            ai ? this.inviteRow.hide() : this.inviteRow.show();
        if (this.privateRow) {
            if (ai)
                this.privateRow.hide();
            else
                this.privateRow.show();
        }
        if (this.privateHelp) {
            if (ai)
                this.privateHelp.hide();
        }
        this.syncInviteeRowVisibility();
    },

    syncInviteeRowVisibility:function() {
        if (this.inviteeRow == null)
            return;
        var inviteOn = this.playMode === "casual"
            && this.isInviteOnlyCheckbox != null
            && this.isInviteOnlyCheckbox.find("input").is(":checked");
        if (inviteOn) {
            this.inviteeRow.show();
            if (this.descRow)
                this.descRow.hide();
        } else {
            this.inviteeRow.hide();
            if (this.descRow && this.playMode === "casual")
                this.descRow.show();
        }
    },

    syncDefaultFlowCheckbox:function() {
        if (this.defaultFlowCheckbox == null)
            return;
        var flow = $.cookie("play-default-flow");
        this.defaultFlowCheckbox.find("input").prop("checked", flow === this.playMode);
    },

    clearPlayFormResult:function() {
        if (this.playFormResult == null)
            return;
        this.playFormResult.removeClass("result-error result-success").text("");
    },

    showPlayFormResult:function(message, isError) {
        if (this.playFormResult == null)
            return;
        this.playFormResult.removeClass("result-error result-success")
            .addClass(isError ? "result-error" : "result-success")
            .text(message || "");
    },

    bareHallPlayerName:function(raw) {
        if (raw == null)
            return "";
        var s = String(raw).replace(/&[a-zA-Z0-9#]+;/g, "");
        s = s.replace(/^[*+]+/, "");
        s = $.trim(s);
        if (s === "")
            return "";
        var parts = s.split(/\s+/);
        return parts[parts.length - 1];
    },

    bindExclusiveDeckPair:function(playerSelect, librarySelect, onChange) {
        var that = this;
        playerSelect.change(function () {
            if (playerSelect.val())
                librarySelect.val("");
            that.refreshExclusiveDeckPairHighlight(playerSelect, librarySelect);
            if (typeof onChange == "function")
                onChange();
        });
        librarySelect.change(function () {
            if (librarySelect.val())
                playerSelect.val("");
            that.refreshExclusiveDeckPairHighlight(playerSelect, librarySelect);
            if (typeof onChange == "function")
                onChange();
        });
    },

    refreshExclusiveDeckPairHighlight:function(playerSelect, librarySelect) {
        if (playerSelect == null || librarySelect == null)
            return;
        var playerRow = playerSelect.closest(".play-form-row");
        var libraryRow = librarySelect.closest(".play-form-row");
        playerRow.removeClass("play-form-row--selected play-form-row--dim");
        libraryRow.removeClass("play-form-row--selected play-form-row--dim");
        var playerVal = playerSelect.val();
        var libraryVal = librarySelect.val();
        var libraryVisible = libraryRow.length > 0 && libraryRow.is(":visible");
        if (playerVal) {
            playerRow.addClass("play-form-row--selected");
            if (libraryVisible)
                libraryRow.addClass("play-form-row--dim");
        } else if (libraryVal && libraryVisible) {
            libraryRow.addClass("play-form-row--selected");
            playerRow.addClass("play-form-row--dim");
        }
    },

    selectedDeckFromPair:function(playerSelect, librarySelect) {
        var libVal = librarySelect != null ? librarySelect.val() : null;
        if (libVal != null && libVal !== "") {
            var libOpt = librarySelect.find(":selected");
            return {
                name:libVal,
                sample:"true",
                side:libOpt.attr("data-side")
            };
        }
        var playerVal = playerSelect != null ? playerSelect.val() : null;
        if (playerVal != null && playerVal !== "") {
            var playerOpt = playerSelect.find(":selected");
            return {
                name:playerVal,
                sample:"false",
                side:playerOpt.attr("data-side")
            };
        }
        return null;
    },

    selectHasValue:function(select, value) {
        var found = false;
        if (select == null)
            return false;
        select.find("option").each(function () {
            if ($(this).attr("value") === value)
                found = true;
        });
        return found;
    },

    fillDeckSelect:function(select, sampleOnly, requiredSide, placeholder, selectDefault) {
        if (select == null)
            return 0;
        var prev = select.val();
        select.empty();
        select.append($("<option></option>").attr("value", "").text(placeholder));
        var count = 0;
        var i;
        for (i = 0; i < this.deckOptions.length; i++) {
            var opt = this.deckOptions[i];
            if (sampleOnly && !opt.sample)
                continue;
            if (!sampleOnly && opt.sample)
                continue;
            if (requiredSide != null && requiredSide !== "" && opt.side !== requiredSide && opt.side !== "other")
                continue;
            var option = $("<option></option>");
            option.attr("value", opt.name);
            option.attr("data-sample-deck", opt.sample ? "true" : "false");
            option.attr("data-side", opt.side);
            var label = opt.label;
            if (sampleOnly && label.indexOf("Sample: ") === 0)
                label = label.substring(8);
            option.text(label);
            select.append(option);
            count++;
        }
        if (count == 0 && !sampleOnly) {
            select.empty();
            var emptyLabel = requiredSide != null ? "No opposite-side decks found" : "You have no decks yet";
            select.append($("<option></option>").attr("value", "").text(emptyLabel));
            return 0;
        }
        if (prev != null && prev !== "") {
            var found = false;
            select.find("option").each(function () {
                if ($(this).attr("value") === prev)
                    found = true;
            });
            if (found) {
                select.val(prev);
                return count;
            }
        }
        if (selectDefault && count > 0)
            select.val(select.find("option").eq(1).attr("value"));
        return count;
    },

    fillSplitDeckSelects:function() {
        this.fillDeckSelect(this.playerDeckSelect, false, null, "Choose one of your decks", true);
        this.fillDeckSelect(this.libraryDeckSelect, true, null, "Select a Library Deck", false);
        this.updateBotDeckSelects();
        this.refreshExclusiveDeckPairHighlight(this.playerDeckSelect, this.libraryDeckSelect);
        this.fillTournamentDeckSelects();
    },

    oppositeSide:function(side) {
        if (side === "dark")
            return "light";
        if (side === "light")
            return "dark";
        return null;
    },

    updateBotDeckSelects:function() {
        var required = this.oppositeSide(this.getSelectedDeckSide());
        var prevBotPlayer = this.botPlayerDeckSelect != null ? this.botPlayerDeckSelect.val() : null;
        var prevBotLib = this.botLibraryDeckSelect != null ? this.botLibraryDeckSelect.val() : null;
        var playerCount = this.fillDeckSelect(this.botPlayerDeckSelect, false, required, "Choose one of your decks", false);
        var libCount = this.fillDeckSelect(this.botLibraryDeckSelect, true, required, "Select a Library Deck", false);
        if (prevBotLib != null && prevBotLib !== "" && this.selectHasValue(this.botLibraryDeckSelect, prevBotLib)) {
            this.botLibraryDeckSelect.val(prevBotLib);
            this.botPlayerDeckSelect.val("");
        } else if (prevBotPlayer != null && prevBotPlayer !== "" && this.selectHasValue(this.botPlayerDeckSelect, prevBotPlayer)) {
            this.botPlayerDeckSelect.val(prevBotPlayer);
            this.botLibraryDeckSelect.val("");
        } else if (playerCount > 0) {
            this.botPlayerDeckSelect.val(this.botPlayerDeckSelect.find("option").eq(1).attr("value"));
            this.botLibraryDeckSelect.val("");
        } else if (libCount > 0) {
            this.botLibraryDeckSelect.val(this.botLibraryDeckSelect.find("option").eq(1).attr("value"));
            this.botPlayerDeckSelect.val("");
        }
        this.refreshExclusiveDeckPairHighlight(this.botPlayerDeckSelect, this.botLibraryDeckSelect);
    },

    // Slice 1.5b — LOTR CreateLeagueTable parity: Open League Table + Join Leagues
    showLeaguePanel:function() {
        this.playMode = "league";
        this.playSelectionPanel.hide();
        this.playFormPanel.hide();
        if (this.playBotPanel != null) {
            this.playBotPanel.hide();
        }
        if (this.playTournamentPanel != null) {
            this.playTournamentPanel.hide();
        }
        this.parkPlayFormFields();
        this.playOverlay.addClass("play-flow-league");
        this.setPlayFlowTitle("League");
        if (this.playLeagueNextSteps != null) {
            this.playLeagueNextSteps.hide().empty();
        }
        if (this.leagueResultDiv != null) {
            this.leagueResultDiv.removeClass("result-error result-success").text("Ready.");
        }
        this.refreshLeagueDropdownAndList();
        this.playLeaguePanel.show();
    },

    syncLeagueDecksFromCreateSelect:function() {
        if (this.leagueDecksSelect == null || this.decksSelect == null) {
            return;
        }
        var prevPlayer = this.leagueDecksSelect.val();
        var showLibrary = this._leagueLibraryDeckVisible;
        var prevLib = (showLibrary && this.leagueLibraryDecksSelect != null) ? this.leagueLibraryDecksSelect.val() : null;
        var playerSelect = this.leagueDecksSelect;
        var libSelect = this.leagueLibraryDecksSelect;
        playerSelect.empty();
        if (libSelect != null) {
            libSelect.empty();
            libSelect.append($("<option></option>").attr("value", "").text("Select a Library Deck"));
        }
        var playerCount = 0;
        this.decksSelect.find("option").each(function () {
            var src = $(this);
            var sample = src.attr("data-sample-deck");
            var isSample = (sample === "true" || sample === true);
            var opt = $("<option></option>");
            opt.attr("value", src.attr("value"));
            if (sample != null) {
                opt.attr("data-sample-deck", sample);
            }
            var side = src.attr("data-side");
            if (side != null) {
                opt.attr("data-side", side);
            }
            var label = src.text();
            if (isSample && libSelect != null && showLibrary) {
                // strip "Sample: " prefix for library dropdown (LOTR uses Deck.formatDeck)
                if (label.indexOf("Sample: ") === 0) {
                    label = label.substring(8);
                }
                opt.text(label);
                libSelect.append(opt);
            } else if (!isSample) {
                opt.text(label);
                playerSelect.append(opt);
                playerCount++;
            }
        });
        if (playerCount == 0) {
            playerSelect.append($("<option></option>").attr("value", "").text("You have no decks yet"));
        } else {
            // placeholder like LOTR SelectDeck
            playerSelect.prepend($("<option></option>").attr("value", "").text("Choose one of your decks"));
        }
        if (prevPlayer != null && prevPlayer !== "") {
            var foundP = false;
            playerSelect.find("option").each(function () {
                if ($(this).attr("value") === prevPlayer) {
                    foundP = true;
                }
            });
            if (foundP) {
                playerSelect.val(prevPlayer);
            } else if (playerCount > 0) {
                playerSelect.val(playerSelect.find("option").eq(1).attr("value"));
            }
        } else if (playerCount > 0) {
            playerSelect.val(playerSelect.find("option").eq(1).attr("value"));
        }
        if (showLibrary && libSelect != null && prevLib != null && prevLib !== "") {
            var foundL = false;
            libSelect.find("option").each(function () {
                if ($(this).attr("value") === prevLib) {
                    foundL = true;
                }
            });
            if (foundL) {
                libSelect.val(prevLib);
                playerSelect.val("");
            }
        }
        this.refreshExclusiveDeckPairHighlight(playerSelect, libSelect);
    },

    leagueUsesIssuedCards:function (xml) {
        var root = xml && xml.documentElement;
        if (root == null || root.tagName != "league") {
            return true;
        }
        if (root.getAttribute("isSoloDraft") == "true") {
            return true;
        }
        var series = root.getElementsByTagName("serie");
        for (var si = 0; si < series.length; si++) {
            if (series[si].getAttribute("limited") == "true") {
                return true;
            }
        }
        return false;
    },

    setLeagueLibraryDeckVisible:function (visible) {
        this._leagueLibraryDeckVisible = !!visible;
        if (this.leagueLibraryRow != null) {
            if (visible) {
                this.leagueLibraryRow.show();
            } else {
                this.leagueLibraryRow.hide();
                if (this.leagueLibraryDecksSelect != null) {
                    this.leagueLibraryDecksSelect.val("");
                }
                if (this.leagueLibraryHelp != null) {
                    this.leagueLibraryHelp.hide();
                }
            }
        }
        this.refreshExclusiveDeckPairHighlight(this.leagueDecksSelect, this.leagueLibraryDecksSelect);
    },

    refreshLeagueDropdownAndList:function() {
        var that = this;
        if (this.comm == null) {
            return;
        }
        this.comm.getLeagues(function (xml) {
            that.applyLeagueCache(xml);
            that.populateLeagueDropdown(null);
            that.ensurePlayLeagueListUI();
            // Re-entry: restore next-steps for selected enrolled league (cleared on panel open)
            that.restoreLeagueNextSteps();
            that.refreshLeagueDecksForSelectedLeague();
        }, {
            "0": function () {
                that.populateLeagueDropdown(null);
                that.ensurePlayLeagueListUI();
                that.restoreLeagueNextSteps();
                that.refreshLeagueDecksForSelectedLeague();
            }
        });
    },

    applyLeagueCache:function(xml) {
        this.leagueCache = [];
        if (xml == null) {
            return;
        }
        var root = xml.documentElement;
        if (root == null || root.tagName != "leagues") {
            return;
        }
        var leagues = root.getElementsByTagName("league");
        var types = {};
        for (var i = 0; i < leagues.length; i++) {
            var type = leagues[i].getAttribute("type");
            var name = leagues[i].getAttribute("name");
            var member = leagues[i].getAttribute("member") == "true";
            var start = leagues[i].getAttribute("start");
            var end = leagues[i].getAttribute("end");
            if (type != null && type.length > 0) {
                this.leagueCache.push({type: type, name: name, member: member, start: start, end: end});
                types[type] = true;
            }
        }
        // Keep casual/ai format tags in sync
        if (this.supportedFormatsSelect != null) {
            this.supportedFormatsSelect.find("option").each(function () {
                var val = $(this).attr("value");
                if (types[val]) {
                    $(this).attr("data-league", "1");
                }
            });
            this.leagueTypesLoaded = true;
        }
    },

    // Mirror LOTR FormatManager.registerLeagueDropdownUpdate: only enrolled leagues
    populateLeagueDropdown:function(preferType) {
        if (this.leagueFormatSelect == null) {
            return;
        }
        var current = preferType != null ? preferType : this.leagueFormatSelect.val();
        this.leagueFormatSelect.empty();
        var count = 0;
        var cache = this.leagueCache || [];
        for (var i = 0; i < cache.length; i++) {
            var league = cache[i];
            if (!league.member) {
                continue;
            }
            var opt = $("<option></option>");
            opt.attr("value", league.type);
            opt.text(league.name != null ? league.name : league.type);
            this.leagueFormatSelect.append(opt);
            count++;
        }
        if (count == 0) {
            var empty = $("<option></option>");
            empty.attr("disabled", "disabled");
            empty.attr("value", "");
            empty.text("You have not joined a league yet; join one below");
            this.leagueFormatSelect.append(empty);
            this.leagueFormatSelect.val("");
        } else if (current != null && current !== "") {
            var found = false;
            this.leagueFormatSelect.find("option").each(function () {
                if ($(this).attr("value") === current) {
                    found = true;
                }
            });
            if (found) {
                this.leagueFormatSelect.val(current);
            }
        }
    },

    ensurePlayLeagueListUI:function() {
        var that = this;
        if (this.playLeagueList == null) {
            return;
        }
        if (this.playLeagueUI == null) {
            this.playLeagueUI = new LeagueResultsUI("/gemp-swccg-server",
                function (leagueCode) { that.leagueJoined(leagueCode); },
                {
                    list: this.playLeagueList,
                    autoLoad: true,
                    idPrefix: "playLeague",
                    onJoinError: function (leagueCode, message) {
                        that.showLeagueNextStepsMessage("Could not join the league: " + message, true);
                    }
                });
        } else {
            this.playLeagueUI.loadResults();
        }
    },

    leagueJoined:function(leagueCode) {
        var that = this;
        // Refresh membership + select the new league; rebuild ALL enrolled next-steps
        this.comm.getLeagues(function (xml) {
            that.applyLeagueCache(xml);
            that.populateLeagueDropdown(leagueCode);
            that.renderAllLeagueNextSteps(leagueCode);
            that.refreshLeagueDecksForSelectedLeague();
        }, {
            "0": function () {
                that.populateLeagueDropdown(leagueCode);
                that.renderAllLeagueNextSteps(leagueCode);
                that.refreshLeagueDecksForSelectedLeague();
            }
        });
    },

    // Rebuild next-steps for EVERY enrolled league (panel re-entry / dropdown change)
    restoreLeagueNextSteps:function() {
        this.renderAllLeagueNextSteps(null);
    },

    showLeagueNextStepsMessage:function(text, isError) {
        if (this.playLeagueNextSteps == null) {
            return;
        }
        this._leagueNextStepsGen = (this._leagueNextStepsGen || 0) + 1;
        this.playLeagueNextSteps.empty();
        if (isError) {
            this.playLeagueNextSteps.addClass("play-error");
        } else {
            this.playLeagueNextSteps.removeClass("play-error");
        }
        this.playLeagueNextSteps.append($("<div></div>").text(text));
        this.playLeagueNextSteps.show();
    },

    // Multi-league: one stacked box per enrolled (member:true) league at top of Join Leagues.
    // freshJoinCode: that league gets "You joined NAME. Next:"; others "Next for NAME:".
    renderAllLeagueNextSteps:function(freshJoinCode) {
        if (this.playLeagueNextSteps == null) {
            return;
        }
        this._leagueNextStepsGen = (this._leagueNextStepsGen || 0) + 1;
        var gen = this._leagueNextStepsGen;
        this.playLeagueNextSteps.removeClass("play-error");
        this.playLeagueNextSteps.empty();
        var cache = this.leagueCache || [];
        var members = [];
        for (var i = 0; i < cache.length; i++) {
            if (cache[i].member && cache[i].type) {
                members.push(cache[i].type);
            }
        }
        if (members.length === 0) {
            this.playLeagueNextSteps.hide();
            return;
        }
        for (var j = 0; j < members.length; j++) {
            var code = members[j];
            var fresh = (freshJoinCode != null && freshJoinCode !== "" && code === freshJoinCode);
            this.appendLeagueNextStepsBlock(this.playLeagueNextSteps, code, fresh, gen);
        }
        this.playLeagueNextSteps.show();
    },

    // One per-league next-steps block (Create table always; draft / deck builder when applicable)
    appendLeagueNextStepsBlock:function(container, leagueCode, freshJoin, gen) {
        var that = this;
        if (leagueCode == null || leagueCode === "") {
            return;
        }
        var name = leagueCode;
        var cache = this.leagueCache || [];
        for (var i = 0; i < cache.length; i++) {
            if (cache[i].type === leagueCode && cache[i].name) {
                name = cache[i].name;
                break;
            }
        }
        var header = freshJoin ? ("You joined " + name + ". Next:") : ("Next for " + name + ":");
        var block = $("<div class='play-next-steps-league'></div>");
        if (freshJoin) {
            block.addClass("play-next-steps-fresh");
        }
        block.append($("<div class='play-next-steps-header'></div>").text(header));
        var buttons = $("<div class='play-next-buttons'></div>");
        // Capture code for this block (Create table targets THIS league)
        (function (codeForBlock) {
            buttons.append($("<button type='button'></button>").text("Create a table in this league").button().click(function () {
                that.populateLeagueDropdown(codeForBlock);
                that.leagueFormatSelect.focus();
                var form = $("#league-table-options")[0];
                if (form && form.scrollIntoView) {
                    form.scrollIntoView(true);
                }
            }));
        })(leagueCode);
        block.append(buttons);
        container.append(block);

        // Draft / limited next-steps (SWCCG soloDraft.html + deckBuild.html); gen guards stale async
        this.comm.getLeague(leagueCode, function (xml) {
            if (gen != null && that._leagueNextStepsGen !== gen) {
                return;
            }
            var root = xml && xml.documentElement;
            if (root == null || root.tagName != "league") {
                return;
            }
            if (root.getAttribute("draftable") == "true") {
                (function (codeForDraft) {
                    buttons.append($("<button type='button'></button>").text("Go to draft").button().click(function () {
                        var win = window.open("soloDraft.html?leagueType=" + encodeURIComponent(codeForDraft), "_blank");
                        if (win) {
                            win.focus();
                        }
                    }));
                })(leagueCode);
            }
            var limited = false;
            var collectionCode = null;
            var series = root.getElementsByTagName("serie");
            for (var si = 0; si < series.length; si++) {
                if (series[si].getAttribute("limited") == "true") {
                    limited = true;
                    if (collectionCode == null) {
                        var ct = series[si].getAttribute("collectionType");
                        if (ct) {
                            collectionCode = ct;
                        }
                    }
                }
            }
            if (limited) {
                var deckHref = "deckBuild.html?product=" + encodeURIComponent("all");
                if (collectionCode) {
                    deckHref = "deckBuild.html?collection=" + encodeURIComponent(collectionCode)
                        + "&product=" + encodeURIComponent("all");
                }
                buttons.append($("<a href='" + deckHref + "' target='_blank' rel='noopener'></a>")
                    .text("Open the deck builder").button());
                block.append($("<div class='page-hint play-subtitle'></div>").text(
                    "This league issues its own cards: in the deck builder, choose the league's collection to open your packs and build a deck from them. League tables only accept such decks."));
            }
        }, {});
    },

    // Compat: single-league callers → multi render (freshJoin highlights that code)
    showLeagueNextSteps:function(leagueCode, freshJoin) {
        if (freshJoin !== false && leagueCode != null && leagueCode !== "") {
            this.renderAllLeagueNextSteps(leagueCode);
        } else {
            this.renderAllLeagueNextSteps(null);
        }
    },

    submitLeagueTable:function() {
        var that = this;
        var format = this.leagueFormatSelect != null ? this.leagueFormatSelect.val() : null;
        if (format == null || format === "") {
            this.showLeagueCreateResult("You must select a league. If you are not enrolled in one, join one below.", true);
            return;
        }
        var deck = null;
        var sampleDeck = "false";
        var libVal = (this._leagueLibraryDeckVisible && this.leagueLibraryDecksSelect != null)
            ? this.leagueLibraryDecksSelect.val() : null;
        if (libVal != null && libVal !== "") {
            deck = libVal;
            sampleDeck = "true";
        } else if (this.leagueDecksSelect != null) {
            deck = this.leagueDecksSelect.val();
            if (deck != null && deck !== "" && this.leagueDecksSelect[0].selectedIndex >= 0) {
                var attr = this.leagueDecksSelect[0][this.leagueDecksSelect[0].selectedIndex].getAttribute("data-sample-deck");
                sampleDeck = attr != null ? attr : "false";
            }
        }
        if (deck == null || deck === "") {
            this.showLeagueCreateResult("You must select a deck. Remember that if this is a sealed or draft league, you may only use cards issued by this league.", true);
            return;
        }
        $(this.leagueCreateButton).button("disable");
        // League tables: human, not private (server rejects otherwise)
        this.comm.createTable(format, deck, sampleDeck, "", false, false, null, null, null, null, false,
            function (xml) {
                $(that.leagueCreateButton).button("enable");
                if (xml != null) {
                    var root = xml.documentElement;
                    if (root.tagName == "error") {
                        that.showLeagueCreateResult(root.getAttribute("message") || "Could not create league table.", true);
                        return;
                    }
                    if (root.tagName == "response") {
                        that.showLeagueCreateResult(root.getAttribute("message") || "Table created.", false);
                        that.closePlayOverlay();
                        return;
                    }
                }
                that.closePlayOverlay();
            }, {
                "409": function (xhr, status, error) {
                    // Collection / HallException usually arrives as XML <error> on success; bare 409 has empty body.
                    var msg = that.extractCreateTableErrorMessage(xhr,
                        "Could not create league table (conflict). If this is a sealed/draft league, use only cards from that league collection.");
                    that.showLeagueCreateResult(msg, true);
                    $(that.leagueCreateButton).button("enable");
                },
                "400": function (xhr) {
                    var msg = that.extractCreateTableErrorMessage(xhr,
                        "Could not create league table (bad request). Check your deck and league selection.");
                    that.showLeagueCreateResult(msg, true);
                    $(that.leagueCreateButton).button("enable");
                },
                "0": function () {
                    that.showLeagueCreateResult("Network error creating table.", true);
                    $(that.leagueCreateButton).button("enable");
                }
            });
    },

    // Prefer server body / XML error message; fall back to a sealed-aware default when body is empty.
    extractCreateTableErrorMessage:function(xhr, fallback) {
        if (xhr == null) {
            return fallback;
        }
        var text = xhr.responseText;
        if (text != null && text !== "") {
            // Try XML <error message="..."> (dataType xml may still leave responseText)
            try {
                var xml = xhr.responseXML;
                if (xml != null && xml.documentElement != null && xml.documentElement.tagName == "error") {
                    var m = xml.documentElement.getAttribute("message");
                    if (m != null && m !== "") {
                        return m;
                    }
                }
            } catch (e) { /* ignore parse */ }
            // Strip trivial HTML/XML wrappers if present
            var stripped = String(text).replace(/^\s+|\s+$/g, "");
            if (stripped.indexOf("<") === 0) {
                var match = /message\s*=\s*["']([^"']+)["']/.exec(stripped);
                if (match && match[1]) {
                    return match[1];
                }
            }
            return stripped;
        }
        return fallback;
    },

    showLeagueCreateResult:function(text, isError) {
        if (this.leagueResultDiv == null) {
            return;
        }
        this.leagueResultDiv.text(text != null ? text : "Ready.");
        this.leagueResultDiv.removeClass("warningMessage result-error result-success");
        if (isError) {
            this.leagueResultDiv.addClass("result-error warningMessage");
        } else if (text != null && text !== "Ready.") {
            this.leagueResultDiv.addClass("result-success");
        }
        this.leagueResultDiv.show();
    },


    countLeagueFormatOptions:function() {
        var n = 0;
        this.supportedFormatsSelect.find("option").each(function () {
            if ($(this).attr("data-league") === "1" && !$(this).prop("disabled")) {
                n++;
            }
        });
        return n;
    },

    filterFormatsForPlayMode:function(mode) {
        var that = this;
        var firstEnabled = null;
        this.supportedFormatsSelect.find("option").each(function () {
            var opt = $(this);
            var isLeague = opt.attr("data-league") === "1";
            var enable;
            if (mode === "league") {
                enable = isLeague;
            } else {
                // Casual + AI: hall formats only (hide league types)
                enable = !isLeague;
            }
            // jQuery 1.6.2: .prop exists (1.6+); disable non-matching options
            opt.prop("disabled", !enable);
            if (enable && firstEnabled == null) {
                firstEnabled = opt.attr("value");
            }
        });
        var preferred = "anything_goes";
        var preferredEnabled = false;
        this.supportedFormatsSelect.find("option").each(function () {
            var opt = $(this);
            if (!opt.prop("disabled") && opt.attr("value") === preferred)
                preferredEnabled = true;
        });
        if (mode !== "league" && preferredEnabled) {
            this.supportedFormatsSelect.val(preferred);
        } else if (firstEnabled != null) {
            this.supportedFormatsSelect.val(firstEnabled);
        }
    },

    tagLeagueFormats:function() {
        var that = this;
        if (this.leagueTypesLoaded || this.comm == null) {
            return;
        }
        this.comm.getLeagues(function (xml) {
            if (xml == null) {
                return;
            }
            var root = xml.documentElement;
            if (root == null || root.tagName != "leagues") {
                return;
            }
            var leagues = root.getElementsByTagName("league");
            var types = {};
            for (var i = 0; i < leagues.length; i++) {
                var type = leagues[i].getAttribute("type");
                if (type != null && type.length > 0) {
                    types[type] = true;
                }
            }
            that.supportedFormatsSelect.find("option").each(function () {
                var val = $(this).attr("value");
                if (types[val]) {
                    $(this).attr("data-league", "1");
                }
            });
            that.leagueTypesLoaded = true;
            // If league panel is open, refresh enrolled dropdown
            if (that.playMode === "league") {
                that.refreshLeagueDropdownAndList();
            }
        }, {});
    },

    submitCreateTable:function() {
        var that = this;
        var format = that.supportedFormatsSelect.val();
        var playerDeck = that.selectedDeckFromPair(that.playerDeckSelect, that.libraryDeckSelect);
        if (playerDeck == null || playerDeck.name == null || playerDeck.name === "") {
            that.showPlayFormResult("You must select a deck.", true);
            return;
        }
        var deck = playerDeck.name;
        var sampleDeck = playerDeck.sample;
        var tableDesc = that.tableDescInput.val();
        var isPrivate = false;
        if (document.getElementById('isPrivateCheckbox1') != null)
            isPrivate = document.getElementById('isPrivateCheckbox1').checked;
        var isInviteOnly = false;
        if (that.playMode === "casual" && that.isInviteOnlyCheckbox != null)
            isInviteOnly = that.isInviteOnlyCheckbox.find("input").is(":checked");
        if (isInviteOnly) {
            tableDesc = that.invitePicker != null ? that.invitePicker.val() : "";
            if (tableDesc == null || $.trim(tableDesc) === "") {
                that.showPlayFormResult("If set to invite-only, you must name a player to invite.", true);
                return;
            }
        }
        var playVsAi = that.playMode === "ai";
        if (that.playMode === "league") {
            isPrivate = false;
            playVsAi = false;
            isInviteOnly = false;
        }
        if (playVsAi) {
            isPrivate = false;
            isInviteOnly = false;
            that.opponentSelect.val("ai");
        }
        var aiSkill = that.aiSkillSelect.val();
        var aiDeckName = null;
        var aiDeckSample = null;
        if (playVsAi) {
            var botDeck = that.selectedDeckFromPair(that.botPlayerDeckSelect, that.botLibraryDeckSelect);
            if (botDeck == null || botDeck.name == null || botDeck.name === "") {
                that.showPlayFormResult("You must select a bot deck.", true);
                return;
            }
            aiDeckName = botDeck.name;
            aiDeckSample = botDeck.sample;
        }
        var timer = (that.playMode === "casual" && that.timerSelect != null) ? that.timerSelect.val() : null;
        var keepOpen = that.keepOpenCheckbox != null && that.keepOpenCheckbox.find("input").is(":checked");
        that.clearPlayFormResult();
        $(that.createTableButton).button("disable");
        var gameWin = null;
        if (playVsAi)
            gameWin = window.open("about:blank", "_blank");
        that.pendingBotWin = gameWin;

        var enableCreate = function() {
            setTimeout(function() {
                $(that.createTableButton).button("enable");
            }, 2000);
        };
        var finishOk = function(started) {
            if (keepOpen)
                that.showPlayFormResult(started || playVsAi ? "Bot game started." : "Table created.", false);
            else
                that.closePlayOverlay();
            enableCreate();
        };
        var finishErr = function(message) {
            that.closePendingBotWin();
            if (message) {
                that.chat.appendMessage(message, "warningMessage");
                that.showPlayFormResult(message, true);
            }
            enableCreate();
        };
        var onCreateError = function(xhr) {
            finishErr(that.extractCreateTableErrorMessage(xhr, "Could not create table."));
        };

        that.comm.createTable(format, deck, sampleDeck, tableDesc, isPrivate, playVsAi, aiSkill, aiDeckName, aiDeckSample, timer, isInviteOnly, function (xml) {
            var started = false;
            if (xml != null && xml.documentElement != null) {
                var root = xml.documentElement;
                if (root.tagName == "error") {
                    finishErr(root.getAttribute("message"));
                    return;
                }
                if (root.tagName == "response") {
                    var gameId = root.getAttribute("gameId");
                    var message = root.getAttribute("message");
                    if (gameId)
                        started = that.openStartedGame(gameId, gameWin);
                    if (message) {
                        that.chat.appendMessage(message, "warningMessage");
                        that.showDialog("Info", message, 320);
                    }
                    finishOk(started);
                    return;
                }
            }
            finishOk(false);
        }, {
            "0": onCreateError,
            "400": onCreateError,
            "401": onCreateError,
            "403": onCreateError,
            "409": onCreateError,
            "500": onCreateError,
            "502": onCreateError,
            "503": onCreateError
        });
    },

    closePendingBotWin:function() {
        var win = this.pendingBotWin;
        this.pendingBotWin = null;
        if (win && !win.closed)
            win.close();
    },

    openStartedGame:function(gameId, existingWin) {
        if (gameId == null || gameId === "") {
            if (existingWin && !existingWin.closed)
                existingWin.close();
            return false;
        }
        if (this.openedGameIds == null)
            this.openedGameIds = {};
        if (this.openedGameIds[gameId]) {
            if (existingWin && !existingWin.closed && existingWin !== this.pendingBotWin)
                existingWin.close();
            return true;
        }
        this.openedGameIds[gameId] = true;
        var win = existingWin;
        if ((!win || win.closed) && this.pendingBotWin && !this.pendingBotWin.closed)
            win = this.pendingBotWin;
        this.pendingBotWin = null;
        var participantId = getUrlParam("participantId");
        var url = "/gemp-swccg/game.html?gameId=" + encodeURIComponent(gameId);
        if (participantId != null)
            url += "&participantId=" + encodeURIComponent(participantId);
        if (win && !win.closed) {
            win.location.href = url;
            win.focus();
            return true;
        }
        return this.openOrOffer("game:" + gameId, url, "Your game is ready",
            "Your game has started, but your browser blocked the new window.", "Open game");
    },

    openOrOffer:function(key, url, title, text, label) {
        var win = null;
        try {
            win = window.open(url, "_blank");
        } catch (e) {
            win = null;
        }
        if (win != null && !win.closed) {
            try {
                win.focus();
            } catch (e) {
            }
            return true;
        }
        this.showReadyModal(key, url, title, text, label);
        return false;
    },

    showReadyModal:function(key, url, title, text, label) {
        if (this.readyModals[key] != null)
            return;
        var that = this;
        var content = $("<div class='hall-ready-modal'></div>");
        content.append($("<p></p>").text(text));
        var link = $("<a class='hall-ready-open' target='_blank'></a>").attr("href", url).text(label);
        content.append($("<p class='hall-ready-actions'></p>").append(link));
        this.readyModals[key] = content;
        content.dialog({
            title: title,
            modal: true,
            resizable: false,
            closeOnEscape: false,
            width: 360,
            closeText: "",
            dialogClass: "hall-ready-dialog",
            open: function () {
                $(this).closest(".ui-dialog").find(".ui-dialog-titlebar-close").hide();
            },
            close: function () {
                if (that.readyModals[key] === content)
                    delete that.readyModals[key];
                content.dialog("destroy").remove();
            }
        });
        link.button().click(function () {
            content.dialog("close");
        });
    },

    closeReadyModal:function(key) {
        var content = this.readyModals[key];
        if (content != null)
            content.dialog("close");
    },

    refreshLayout:function() {
        if (this.div == null) {
            return;
        }
        var width = $(this.div).width();
        var height = $(this.div).height();
        this.hallResized(width, height);
    },

    hallResized:function (width, height) {
        this.buttonsDiv.css({left:"0px", width:width + "px", backgroundColor:"#000000", "border-top-width":"1px", "border-top-color":"#ffffff", "border-top-style":"solid", display:"flex", "flex-direction":"row", "align-items":"center", "justify-content":"center"});
        var buttonsHeight = this.buttonsDiv.outerHeight();
        if (buttonsHeight == null || buttonsHeight <= 0) {
            buttonsHeight = 56;
        }
        this.tablesDiv.css({overflow:"auto", left:"0px", top:"0px", width:width + "px", height:(height - buttonsHeight) + "px"});
        this.buttonsDiv.css({top:(height - buttonsHeight) + "px"});
    },

    getHall: function() {
        var that = this;

        this.comm.getHall(
            function(xml) {
                that.processHall(xml);
            }, this.hallErrorMap());
    },

    updateHall:function () {
        var that = this;

        this.comm.updateHall(
            function (xml) {
                that.processHall(xml);
            }, this.hallChannelId, this.hallErrorMap());
    },

    hallErrorMap:function() {
        var that = this;
        return {
            "0": function() {
                if (that.connection)
                    that.connection.set("disconnected", {message: "Unable to connect to server.", action: "reload"});
                that.showErrorDialog("Server connection error", "Unable to connect to server. Either server is down or there is a problem with your internet connection.", true, false);
            },
            "401":function() {
                if (that.connection)
                    that.connection.set("disconnected", {message: "You are not logged in.", action: "login"});
                that.showErrorDialog("Authentication error", "You are not logged in", false, true);
            },
            "409":function() {
                if (that.connection)
                    that.connection.set("disconnected", {message: "Game Hall opened in another window.", action: "reload"});
                that.showErrorDialog("Concurrent access error", "You are accessing Game Hall from another browser or window. Close this window or if you wish to access Game Hall from here, click \"Refresh page\".", true, false);
            },
            "410":function() {
                if (that.connection)
                    that.connection.set("disconnected", {message: "Removed from Game Hall due to inactivity.", action: "reload"});
                that.showErrorDialog("Inactivity error", "You were inactive for too long and have been removed from the Game Hall. If you wish to re-enter, click \"Refresh page\".", true, false);
            }
        };
    },

    showErrorDialog:function(title, text, reloadButton, mainPageButton) {
        var buttons = {};
        if (reloadButton) {
            buttons["Refresh page"] =
                function () {
                    location.reload(true);
                };
        }
        if (mainPageButton) {
            buttons["Go to main page"] =
                function() {
                    location.href = "/gemp-swccg/";
                };
        }

        var dialog = $("<div></div>").dialog({
            title: title,
            resizable: false,
            height: 160,
            modal: true,
            buttons: buttons
        }).text(text);
    },
    
    showDialog:function(title, text, height) {
        if(height == null)
            height = 200
        var dialog = $("<div></div>").dialog({
            title: title,
            resizable: true,
            height: height,
            modal: true,
            closeOnEscape: true,
            buttons: [
                {
                    text: "OK",
                    click: function() {
                        $( this ).dialog( "close" );
                    }
                }
            ],
            closeText: ''
        }).html(text);  
    },

    // Fetches player + library decks into decksSelect (GET /deck/list + library).
    // Optional format/collection query params filter on the server (cached valid_formats).
    // skipLibrary: sealed / draft / cube league tables do not offer sample decks.
    updateDecks:function (format, collection, skipLibrary) {
        var that = this;
        var gen = (this.deckLoadGen || 0) + 1;
        this.deckLoadGen = gen;
        this.deckOptions = [];
        this.decksSelect.html("");
        this.aiDeckSelect.html("");
        this.comm.getDecks(function (xml) {
            if (gen !== that.deckLoadGen)
                return;
            that.processDecks(xml);
            if (skipLibrary) {
                if (that.playLeaguePanel != null && that.playLeaguePanel.is(":visible")) {
                    that.syncLeagueDecksFromCreateSelect();
                }
                that.fillSplitDeckSelects();
                that.updateAiDecksForSelection();
                return;
            }
            that.comm.getLibraryDecks(function (xml2) {
                if (gen !== that.deckLoadGen)
                    return;
                that.processLibraryDecks(xml2);
                that.updateAiDecksForSelection();
            }, null, format, collection);
        }, null, format, collection);
    },

    currentDateYyyymmdd:function() {
        var s = this.lastServerTime;
        if (s != null && s.length >= 10) {
            var n = parseInt(s.substring(0, 10).replace(/-/g, ""), 10);
            if (!isNaN(n))
                return n;
        }
        var d = new Date();
        return d.getFullYear() * 10000 + (d.getMonth() + 1) * 100 + d.getDate();
    },

    currentSerieFromLeagueXml:function(xml) {
        if (xml == null || xml.documentElement == null)
            return null;
        var series = xml.documentElement.getElementsByTagName("serie");
        if (series.length === 0)
            return null;
        var today = this.currentDateYyyymmdd();
        var last = series[series.length - 1];
        for (var i = 0; i < series.length; i++) {
            var start = parseInt(series[i].getAttribute("start"), 10);
            var end = parseInt(series[i].getAttribute("end"), 10);
            if (!isNaN(start) && !isNaN(end) && start <= today && today <= end)
                return series[i];
        }
        return last;
    },

    refreshLeagueDecksForSelectedLeague:function() {
        var that = this;
        if (this.leagueFormatSelect == null) {
            return;
        }
        var type = this.leagueFormatSelect.val();
        if (type == null || type === "") {
            this.setLeagueLibraryDeckVisible(false);
            this.syncLeagueDecksFromCreateSelect();
            return;
        }
        this.comm.getLeague(type, function (xml) {
            var issued = that.leagueUsesIssuedCards(xml);
            that.setLeagueLibraryDeckVisible(!issued);
            var serie = that.currentSerieFromLeagueXml(xml);
            var format = serie != null ? serie.getAttribute("formatType") : null;
            var collection = serie != null ? serie.getAttribute("collectionType") : null;
            that.updateDecks(format, collection, issued);
        }, {
            "0": function () {
                that.setLeagueLibraryDeckVisible(false);
                that.updateDecks(null, null, true);
            },
            "404": function () {
                that.setLeagueLibraryDeckVisible(false);
                that.updateDecks(null, null, true);
            }
        });
    },

    processResponse:function (xml) {
        if (xml != null) {
            var root = xml.documentElement;
            if (root.tagName == "error") {
                var message = root.getAttribute("message");
                this.chat.appendMessage(message, "warningMessage");
            }
        }
    },

    processDecks:function (xml) {
        this.decksSelect.html("");
        var root = xml.documentElement;
        if (root.tagName == "decks") {
            var darkDecks = root.getElementsByTagName("darkDeck");
            this.generateDeckRow(darkDecks, "[DARK] ", "false", "dark");
            var lightDecks = root.getElementsByTagName("lightDeck");
            this.generateDeckRow(lightDecks, "[LIGHT] ", "false", "light");
            var otherDecks = root.getElementsByTagName("otherDeck");
            this.generateDeckRow(otherDecks, "[UNKNOWN] ", "false", "other");
        }
        this.fillSplitDeckSelects();
    },

    processLibraryDecks:function (xml) {
        var root = xml.documentElement;
        if (root.tagName == "decks") {
            var darkDecks = root.getElementsByTagName("darkDeck");
            this.generateDeckRow(darkDecks, "Sample: [DARK] ", "true", "dark");
            var lightDecks = root.getElementsByTagName("lightDeck");
            this.generateDeckRow(lightDecks, "Sample: [LIGHT] ", "true", "light");
            var otherDecks = root.getElementsByTagName("otherDeck");
            this.generateDeckRow(otherDecks, "Sample: [UNKNOWN] ", "true", "other");
        }
        if (this.playLeaguePanel != null && this.playLeaguePanel.is(":visible")) {
            this.syncLeagueDecksFromCreateSelect();
        }
        this.fillSplitDeckSelects();
    },

    generateDeckRow:function (decks, prefix, sampleDeck, side, targetSelect, requiredSide) {
        if (requiredSide != null && requiredSide !== "" && side !== requiredSide)
            return 0;
        var select = targetSelect != null ? targetSelect : this.decksSelect;
        var added = 0;
        for (var i = 0; i < decks.length; i++) {
            // Sanity-check the deck
            if (!decks[i].childNodes || (decks[i].childNodes.length == 0)) {
                // This deck is messed up.  Just skip it
                continue;
            }

            var deckName = decks[i].childNodes[0].nodeValue;
            var deckElem = $("<option></option>");
            deckElem.attr("value", deckName);
            deckElem.attr("data-sample-deck", sampleDeck);
            deckElem.attr("data-side", side);
            deckElem.text(prefix + deckName);
            select.append(deckElem);
            added++;

            // Track for AI selection only when filling the create-table dropdown
            if (targetSelect == null) {
                this.deckOptions.push({name: deckName, sample: sampleDeck === "true", side: side, label: prefix + deckName});
            }
        }
        return added;
    },

    getSelectedDeckSide:function() {
        var picked = this.selectedDeckFromPair(this.playerDeckSelect, this.libraryDeckSelect);
        if (picked == null)
            return null;
        return picked.side;
    },

    updateCreateTableLabel:function() {
        if (this.createTableButton == null || this.opponentSelect == null) {
            return false;
        }
        var label = "Create Table";
        var button = $(this.createTableButton);
        var currentLabel = button.hasClass("ui-button") ? button.button("option", "label") : button.text();
        if (currentLabel === label) {
            return false;
        }
        if (button.hasClass("ui-button")) {
            button.button("option", "label", label);
        } else {
            button.text(label);
        }
        return true;
    },

    updateAiDecksForSelection:function() {
        this.updateCreateTableLabel();
        if (this.playMode === "ai")
            this.updateBotDeckSelects();
        this.applyCasualBotFieldVisibility();
    },

    setAiTablesEnabled:function(enabled) {
        if (this.aiTablesEnabled === enabled) {
            return;
        }
        this.aiTablesEnabled = enabled;

        var aiOption = this.opponentSelect.find("option[value='ai']");
        if (this.playAiChoice != null) {
            this.playAiChoice.show();
        }
        if (aiOption.length === 0) {
            this.opponentSelect.append("<option value='ai'>vs Bot</option>");
        }
        this.updateAiDecksForSelection();
    },

    animateRowUpdate: function(rowSelector) {
        $(rowSelector, this.tablesDiv)
            .css({borderTopColor:"#000000", borderLeftColor:"#000000", borderBottomColor:"#000000", borderRightColor:"#000000"})
            .animate({borderTopColor:"#ffffff", borderLeftColor:"#ffffff", borderBottomColor:"#ffffff", borderRightColor:"#ffffff"}, "fast");
    },
    
    playSound: function(soundObj) {
        try {
            var myAudio = document.getElementById(soundObj);
            if (myAudio)
                myAudio.play();
        } catch (e) {
        }
    },

    formatAge:function(ageMs) {
        var total = Math.floor(Math.max(0, ageMs) / 1000);
        var two = function (n) {
            return (n < 10 ? "0" : "") + n;
        };
        var hours = Math.floor(total / 3600);
        var minutes = Math.floor(total / 60) % 60;
        var seconds = total % 60;
        return (hours > 0 ? hours + ":" + two(minutes) : two(minutes)) + ":" + two(seconds);
    },

    formatServerEpoch:function(ms) {
        var d = new Date(ms);
        var two = function (n) {
            return (n < 10 ? "0" : "") + n;
        };
        return d.getUTCFullYear() + "-" + two(d.getUTCMonth() + 1) + "-" + two(d.getUTCDate())
            + " " + two(d.getUTCHours()) + ":" + two(d.getUTCMinutes()) + ":" + two(d.getUTCSeconds());
    },

    renderTableAge:function(span) {
        var createdAt = +span.attr("data-created-at");
        if (isNaN(createdAt))
            return;
        var shown = parseInt(span.attr("data-shown-seconds"), 10);
        if (!isNaN(shown)) {
            shown += 1;
        } else {
            var offset = this.serverClockOffset == null ? 0 : this.serverClockOffset;
            shown = Math.floor(Math.max(0, Date.now() + offset - createdAt) / 1000);
        }
        span.attr("data-shown-seconds", shown);
        span.text(this.formatAge(shown * 1000));
        if (span.attr("data-title-for") !== String(createdAt)) {
            span.attr("data-title-for", createdAt);
            var prefix = span.attr("data-age-kind") == "playing" ? "Started " : "Open since ";
            span.attr("title", prefix + this.formatServerEpoch(createdAt) + " (server time)");
        }
    },

    refreshServerTime:function() {
        if (this.serverClockOffset == null)
            return;
        var text = this.formatServerEpoch(Date.now() + this.serverClockOffset);
        this.lastServerTime = text;
        $(".serverTime").text("Server time: " + text);
        if (this.serverTimeValue)
            this.serverTimeValue.html(text.replace(" ", "<br>"));
    },

    adoptClockSpan:function(oldRow, newRow) {
        var oldSpan = $(".table-age", oldRow);
        var newSpan = $(".table-age", newRow);
        if (!oldSpan.length || !newSpan.length)
            return;
        if (oldSpan.attr("data-created-at") !== newSpan.attr("data-created-at"))
            return;
        var shown = oldSpan.attr("data-shown-seconds");
        if (shown != null) {
            newSpan.attr("data-shown-seconds", shown);
            newSpan.text(oldSpan.text());
        }
        var title = oldSpan.attr("title");
        if (title) {
            newSpan.attr("title", title);
            newSpan.attr("data-title-for", oldSpan.attr("data-title-for"));
        }
    },

    refreshTableAges:function() {
        var that = this;
        this.refreshServerTime();
        $(".table-age", this.tablesDiv).each(function () {
            that.renderTableAge($(this));
        });
        this.refreshReadyCheckLabels();
    },

    refreshReadyCheckLabels:function() {
        if (this.readyChecks == null)
            return;
        for (var id in this.readyChecks) {
            if (!this.readyChecks.hasOwnProperty(id))
                continue;
            var check = this.readyChecks[id];
            this.tickReadyCheck(check);
            var secs = Math.max(0, Math.round((check.deadline - Date.now()) / 1000));
            var row = $(".queue" + id, this.tablesDiv);
            $("button", row).each(function () {
                var button = $(this);
                if (!button.hasClass("ui-button"))
                    return;
                var label = button.button("option", "label") || "";
                if (label.indexOf("READY CHECK") == 0)
                    button.button("option", "label", "READY CHECK - " + secs + " s");
                else if (label.indexOf("Waiting for others") == 0)
                    button.button("option", "label", "Waiting for others - " + secs + " s");
            });
        }
    },

    renderPlayerMadeQueueRow:function(queue, id, action) {
        var that = this;
        var formatName = queue.getAttribute("format") || "";
        var queueName = queue.getAttribute("queue") || "";
        var statusText = queue.getAttribute("start") || "Waiting for players";
        var playersStr = queue.getAttribute("players") || "";
        var joined = queue.getAttribute("signedUp") == "true";
        var joinable = queue.getAttribute("joinable") == "true";
        var isHost = queue.getAttribute("isHost") == "true";
        var startable = queue.getAttribute("startable") == "true";
        var canCancel = queue.getAttribute("canCancel") == "true";
        var readyCheck = queue.getAttribute("readyCheck") == "true";
        var formatCode = queue.getAttribute("formatCode");
        var playerCount = queue.getAttribute("playerCount") || "0";
        var maxPlayers = queue.getAttribute("maxPlayers") || "";
        if (maxPlayers)
            statusText = statusText + " (" + playerCount + "/" + maxPlayers + ")";

        var row = $("<tr class='queue" + id + "'></tr>");
        row.append("<td>" + formatName + "</td>");
        row.append("<td>" + queueName + "</td>");
        var statusCell = $("<td></td>");
        statusCell.text(statusText);
        var ageAt = parseInt(queue.getAttribute("ageAt"), 10);
        if (!isNaN(ageAt) && ageAt > 0) {
            statusCell.append(" ");
            var ageSpan = $("<span class='table-age'></span>").attr("data-created-at", ageAt);
            statusCell.append(ageSpan);
            this.renderTableAge(ageSpan);
        }
        row.append(statusCell);
        row.append("<td>" + playersStr + "</td>");

        var lastField = $("<td></td>");
        if (joinable && !joined) {
            var joinBut = $("<button>Join</button>");
            var requiresDeck = queue.getAttribute("requiresDeck") != "false";
            $(joinBut).button().click((function(queueId, fmt, qname, fmtCode, needsDeck) {
                return function () {
                    if (!needsDeck) {
                        that.comm.joinPlayerMadeQueue(queueId, "", false, "", false, function (xml) {
                            that.processResponse(xml);
                        }, {
                            "0": function() { that.chat.appendMessage("Could not join tournament.", "warningMessage"); }
                        });
                        return;
                    }
                    that.openJoinPopup({
                        kind: "playerTournament",
                        id: queueId,
                        formatName: fmt,
                        formatCode: fmtCode,
                        collectionCode: null,
                        hostSide: null,
                        requiredSide: null,
                        contextLabel: that.buildJoinContextLabel(fmt, null, qname, null, null)
                    });
                };
            })(id, formatName, queueName, formatCode, requiresDeck));
            lastField.append(joinBut);
        }
        if (joined) {
            var leaveBut = $("<button>Leave</button>");
            $(leaveBut).button().click((function(queueId) {
                return function() {
                    that.comm.leaveQueue(queueId, function (xml) {
                        that.processResponse(xml);
                    });
                };
            })(id));
            lastField.append(leaveBut);
        }
        if (startable) {
            var startBut = $("<button>Start</button>");
            $(startBut).button().click((function(queueId) {
                return function() {
                    that.comm.startQueue(queueId, function (xml) {
                        that.processResponse(xml);
                    }, {
                        "0": function() { that.chat.appendMessage("Could not start tournament.", "warningMessage"); }
                    });
                };
            })(id));
            lastField.append(startBut);
        }
        if (canCancel) {
            var cancelBut = $("<button>Cancel</button>");
            $(cancelBut).button().click((function(queueId) {
                return function() {
                    that.comm.cancelQueue(queueId, function (xml) {
                        that.processResponse(xml);
                    }, {
                        "0": function() { that.chat.appendMessage("Could not cancel tournament.", "warningMessage"); }
                    });
                };
            })(id));
            lastField.append(cancelBut);
        }
        var secs = parseInt(queue.getAttribute("readyCheckSecsRemaining"), 10);
        if (isNaN(secs))
            secs = readyCheck ? 0 : -1;
        if (joined && secs > -1) {
            var checkBut = $("<button type='button'></button>").text("READY CHECK - " + secs + " s");
            checkBut.button().click((function(queueId) {
                return function() {
                    that.confirmReadyCheck(queueId);
                };
            })(id));
            if (queue.getAttribute("confirmedReadyCheck") == "true")
                checkBut.button("option", "label", "Waiting for others - " + secs + " s").button("disable");
            lastField.append(checkBut);
        }
        row.append(lastField);
        this.updateReadyCheck(queue);

        if (action == "add") {
            $("table.waitingTables", this.tablesDiv).append(row);
        } else if (action == "update") {
            var existingQueue = $(".queue" + id, this.tablesDiv);
            if ($(".queue" + id, $("table.waitingTables")).length > 0) {
                this.adoptClockSpan(existingQueue, row);
                existingQueue.replaceWith(row);
            } else {
                existingQueue.remove();
                $("table.waitingTables", this.tablesDiv).append(row);
            }
        }
    },

    markSignedUp:function(kind, id, signed) {
        if (this.signedUpEvents == null)
            this.signedUpEvents = {};
        var key = kind + ":" + id;
        if (signed)
            this.signedUpEvents[key] = true;
        else
            delete this.signedUpEvents[key];
    },

    refreshInTournament:function() {
        this.inTournament = false;
        if (this.signedUpEvents == null)
            return;
        for (var key in this.signedUpEvents) {
            if (this.signedUpEvents.hasOwnProperty(key)) {
                this.inTournament = true;
                return;
            }
        }
    },

    updateReadyCheck:function(queue) {
        var id = queue.getAttribute("id");
        var secs = parseInt(queue.getAttribute("readyCheckSecsRemaining"), 10);
        if (isNaN(secs))
            secs = -1;
        if (queue.getAttribute("signedUp") != "true" || !(secs > -1)) {
            this.endReadyCheck(id);
            return;
        }
        if (this.readyChecks == null)
            this.readyChecks = {};
        var check = this.readyChecks[id];
        if (check == null)
            check = this.readyChecks[id] = {shown: false, dialog: null, deadline: 0, timer: null};
        var fromServer = Date.now() + secs * 1000;
        if (check.deadline <= 0 || fromServer < check.deadline)
            check.deadline = fromServer;
        if (queue.getAttribute("confirmedReadyCheck") == "true") {
            check.shown = true;
            this.closeReadyCheckDialog(check);
            return;
        }
        if (!check.shown) {
            check.shown = true;
            this.showReadyCheckDialog(id, queue.getAttribute("queue"), check);
            this.playSound("gamestart");
        }
        this.tickReadyCheck(check);
    },

    showReadyCheckDialog:function(queueId, queueName, check) {
        var that = this;
        var content = $("<div class='hall-ready-check'></div>");
        content.append($("<p></p>").text("Ready Check started for the ").append($("<b></b>").text(queueName)).append(" tournament."));
        content.append($("<p></p>").text("Confirm you are present within ")
            .append($("<span class='hall-ready-check-secs'></span>"))
            .append(" seconds."));
        content.append($("<p></p>").text("Players who do not click Ready are dropped when the timer ends. If fewer than two remain, the tournament is cancelled."));
        check.dialog = content;
        content.dialog({
            title: "Ready Check",
            modal: true,
            resizable: false,
            closeOnEscape: true,
            width: 360,
            closeText: "",
            buttons: [
                {
                    text: "Ready",
                    "class": "hall-ready-check-confirm",
                    click: function () {
                        that.confirmReadyCheck(queueId);
                    }
                },
                {
                    text: "OK",
                    click: function () {
                        $(this).dialog("close");
                    }
                }
            ],
            close: function () {
                if (check.timer != null)
                    clearInterval(check.timer);
                check.timer = null;
                check.dialog = null;
                content.dialog("destroy").remove();
            }
        });
        check.timer = setInterval(function () {
            that.tickReadyCheck(check);
        }, 1000);
        this.tickReadyCheck(check);
    },

    tickReadyCheck:function(check) {
        if (check.dialog != null)
            check.dialog.find(".hall-ready-check-secs").text(Math.max(0, Math.round((check.deadline - Date.now()) / 1000)));
    },

    confirmReadyCheck:function(queueId) {
        var that = this;
        var check = this.readyChecks ? this.readyChecks[queueId] : null;
        if (check != null)
            this.closeReadyCheckDialog(check);
        this.comm.readyQueue(queueId, function (xml) {
            that.processResponse(xml);
        }, {
            "0": function() { that.chat.appendMessage("Could not confirm ready.", "warningMessage"); }
        });
    },

    closeReadyCheckDialog:function(check) {
        if (check.dialog != null)
            check.dialog.dialog("close");
    },

    endReadyCheck:function(queueId) {
        if (this.readyChecks == null)
            return;
        var check = this.readyChecks[queueId];
        if (check != null) {
            this.closeReadyCheckDialog(check);
            delete this.readyChecks[queueId];
        }
    },

    processHall:function (xml) {
        var that = this;

        var root = xml.documentElement;
        if (root.tagName == "hall") {
            this.hallChannelId = root.getAttribute("channelNumber");

            var currency = parseInt(root.getAttribute("currency"));
            if (!isNaN(currency))
                this.pocketValue = currency; // kept for merchant; no longer shown on primary bar

            var privateGamesEnabled = root.getAttribute("privateGamesEnabledBoolean");
            this.privateGamesAllowed = (privateGamesEnabled == "true");
            this.applyCasualBotFieldVisibility();

            var aiTablesEnabled = root.getAttribute("aiTablesEnabledBoolean");
            if (aiTablesEnabled != null && aiTablesEnabled.length > 0) {
                this.setAiTablesEnabled(aiTablesEnabled == "true");
            }


            var motd = root.getAttribute("motd");
            if (motd != null)
                $("#motd").html("<b>MOTD:</b> " + motd);

            var serverTimeMs = parseInt(root.getAttribute("serverTimeMs"), 10);
            if (!isNaN(serverTimeMs)) {
                var newOffset = serverTimeMs - Date.now();
                if (this.serverClockOffset == null || Math.abs(newOffset - this.serverClockOffset) > 2000) {
                    this.serverClockOffset = newOffset;
                    $(".table-age", this.tablesDiv).removeAttr("data-shown-seconds");
                }
            }
            this.refreshServerTime();
            if (this.connection)
                this.connection.updated(null, this.lastServerTime || this.connection.lastUpdate);

            var queues = root.getElementsByTagName("queue");
            for (var i = 0; i < queues.length; i++) {
                var queue = queues[i];
                var id = queue.getAttribute("id");
                var action = queue.getAttribute("action");
                if (action == "add" || action == "update") {
                    if (queue.getAttribute("playerMade") == "true") {
                        this.markSignedUp("queue", id, queue.getAttribute("signedUp") == "true");
                        this.renderPlayerMadeQueueRow(queue, id, action);
                        this.animateRowUpdate(".queue" + id);
                    } else {
                    var actionsField = $("<td></td>");

                    var joined = queue.getAttribute("signedUp");
                    this.markSignedUp("queue", id, joined == "true");
                    if (joined != "true" && queue.getAttribute("joinable") == "true") {
                        var but = $("<button>Join queue</button>");
                        $(but).button().click((
                            function(queueId, fmt, qname, fmtCode) {
                                return function () {
                                    that.openJoinQueuePopup(queueId, fmt, qname, fmtCode);
                                };
                            }
                            )(id, queue.getAttribute("format"), queue.getAttribute("queue"), queue.getAttribute("formatCode")));
                        actionsField.append(but);
                    } else if (joined == "true") {
                        var but = $("<button>Leave queue</button>");
                        $(but).button().click((
                            function(queueId) {
                                return function() {
                                    that.comm.leaveQueue(queueId, function (xml) {
                                        that.processResponse(xml);
                                    });
                                }
                            })(id));
                        actionsField.append(but);
                    }

                    var row = $("<tr class='queue" + id + "'><td>" + queue.getAttribute("format") + "</td>" +
                        "<td>" + queue.getAttribute("collection") + "</td>" +
                        "<td>" + queue.getAttribute("queue") + "</td>" +
                        "<td>" + queue.getAttribute("start") + "</td>" +
                        "<td>" + queue.getAttribute("system") + "</td>" +
                        "<td>" + queue.getAttribute("playerCount") + "</td>" +
                        "<td align='right'>" + formatPrice(queue.getAttribute("cost")) + "</td>" +
                        "<td>" + queue.getAttribute("prizes") + "</td>" +
                        "</tr>");

                    row.append(actionsField);

                    if (action == "add") {
                        $("table.queues", this.tablesDiv)
                            .append(row);
                    } else if (action == "update") {
                        $(".queue" + id, this.tablesDiv).replaceWith(row);
                    }

                    this.animateRowUpdate(".queue" + id);
                    }
                } else if (action == "remove") {
                    $(".queue" + id, this.tablesDiv).remove();
                    this.markSignedUp("queue", id, false);
                    this.endReadyCheck(id);
                }
            }

            var tournaments = root.getElementsByTagName("tournament");
            for (var i = 0; i < tournaments.length; i++) {
                var tournament = tournaments[i];
                var id = tournament.getAttribute("id");
                var action = tournament.getAttribute("action");
                if (action == "add" || action == "update") {
                    var actionsField = $("<td></td>");

                    var joined = tournament.getAttribute("signedUp");
                    this.markSignedUp("tournament", id, joined == "true");
                    var stage = tournament.getAttribute("stage") || "";
                    var collectionCode = tournament.getAttribute("collectionCode");
                    var cubeSoloType = tournament.getAttribute("cubeSoloType");
                    var decksLocked = tournament.getAttribute("decksLocked") == "true";
                    if (joined == "true") {
                        if (stage === "Drafting") {
                            var draftBut = $("<button>Open draft</button>");
                            $(draftBut).button().click((function(tournamentId, collection, cubeType) {
                                return function () {
                                    that.openLimitedTournamentTab("draft", tournamentId, collection, cubeType);
                                };
                            })(id, collectionCode, cubeSoloType));
                            actionsField.append(draftBut);
                        }
                        if (stage === "Deck building") {
                            var unpackBut = $("<button>Open deck builder</button>");
                            $(unpackBut).button().click((function(collection) {
                                return function () {
                                    that.openLimitedTournamentTab("deck", null, collection, null);
                                };
                            })(collectionCode));
                            actionsField.append(unpackBut);
                            if (!decksLocked) {
                                var lockBut = $("<button>Lock decks</button>");
                                $(lockBut).button().click((function(tournamentId, fmt, fmtCode, collection) {
                                    return function () {
                                        that.openJoinPopup({
                                            kind: "lockDecks",
                                            id: tournamentId,
                                            formatName: fmt,
                                            formatCode: fmtCode,
                                            collectionCode: collection,
                                            hostSide: null,
                                            requiredSide: null,
                                            contextLabel: "Lock Light and Dark decks for this tournament."
                                        });
                                    };
                                })(id, tournament.getAttribute("format"), tournament.getAttribute("formatCode"), collectionCode));
                                actionsField.append(lockBut);
                            }
                        }
                        var but = $("<button>Drop from tournament</button>");
                        $(but).button().click((
                            function(tournamentId) {
                                return function () {
                                    that.comm.dropFromTournament(tournamentId, function (xml) {
                                        that.processResponse(xml);
                                    });
                                };
                            }
                            )(id));
                        actionsField.append(but);
                        that.maybePromptLimitedStart(id, stage, collectionCode, cubeSoloType, tournament.getAttribute("name"));
                    }

                    var row = $("<tr class='tournament" + id + "'><td>" + tournament.getAttribute("format") + "</td>" +
                        "<td>" + tournament.getAttribute("collection") + "</td>" +
                        "<td>" + tournament.getAttribute("name") + "</td>" +
                        "<td>" + tournament.getAttribute("system") + "</td>" +
                        "<td>" + tournament.getAttribute("stage") + "</td>" +
                        "<td>" + tournament.getAttribute("round") + "</td>" +
                        "<td>" + tournament.getAttribute("playerCount") + "</td>" +
                        "</tr>");

                    row.append(actionsField);

                    if (action == "add") {
                        $("table.tournaments", this.tablesDiv)
                            .append(row);
                    } else if (action == "update") {
                        $(".tournament" + id, this.tablesDiv).replaceWith(row);
                    }

                    this.animateRowUpdate(".tournament" + id);
                } else if (action == "remove") {
                    $(".tournament" + id, this.tablesDiv).remove();
                    this.markSignedUp("tournament", id, false);
                }
            }

            var tables = root.getElementsByTagName("table");
            for (var i = 0; i < tables.length; i++) {
                var table = tables[i];
                var id = table.getAttribute("id");
                var action = table.getAttribute("action");
                if (action == "add" || action == "update") {
                    var status = table.getAttribute("status");

                    var gameId = table.getAttribute("gameId");
                    var statusDescription = table.getAttribute("statusDescription");
                    var watchable = table.getAttribute("watchable");
                    var playersAttr = table.getAttribute("players");
                    var formatName = table.getAttribute("format");
                    var tournamentName = table.getAttribute("tournament");
                    var players = new Array();
                    if (playersAttr.length > 0)
                        players = playersAttr.split(",");
                    var playing = table.getAttribute("playing");
                    var winner = table.getAttribute("winner");

                    var row = $("<tr class='table" + id + "'></tr>");

                    row.append("<td>" + formatName + "</td>");
                    row.append("<td>" + tournamentName + "</td>");
                    var statusCell = $("<td></td>");
                    if (statusDescription)
                        statusCell.text(statusDescription);
                    var ageAt = parseInt(table.getAttribute("ageAt"), 10);
                    if ((status == "WAITING" || status == "PLAYING") && !isNaN(ageAt) && ageAt > 0) {
                        if (statusDescription)
                            statusCell.append(" ");
                        var ageSpan = $("<span class='table-age'></span>").attr("data-created-at", ageAt);
                        if (status == "PLAYING")
                            ageSpan.attr("data-age-kind", "playing");
                        statusCell.append(ageSpan);
                        this.renderTableAge(ageSpan);
                    }
                    row.append(statusCell);

                    var playersStr = "";
                    for (var playerI = 0; playerI < players.length; playerI++) {
                        if (playerI > 0)
                            playersStr += ", ";
                        playersStr += players[playerI];
                    }
                    row.append("<td>" + playersStr + "</td>");

                    var lastField = $("<td></td>");
                    if (status == "WAITING") {
                        if (playing == "true") {
                            var that = this;

                            var but = $("<button>Leave table</button>");
                            $(but).button().click((
                                function(tableId) {
                                    return function() {
                                        that.comm.leaveTable(tableId);
                                    };
                                })(id));
                            lastField.append(but);
                        } else {
                            var that = this;

                            var formatCode = table.getAttribute("formatCode");
                            var collectionCode = table.getAttribute("collectionCode");
                            var but = $("<button>Join table</button>");
                            $(but).button().click((
                                function(tableId, fmt, owners, fmtCode, collCode) {
                                    return function() {
                                        that.openJoinTablePopup(tableId, fmt, owners, fmtCode, collCode);
                                    };
                                })(id, formatName, playersStr, formatCode, collectionCode));
                            lastField.append(but);
                        }
                    } else if (status == "PLAYING") {
                        if (playing == "true") {
                            var participantId = getUrlParam("participantId");
                            var participantIdAppend = "";
                            if (participantId != null)
                                participantIdAppend = "&participantId=" + participantId;

                            lastField.append($("<a class='hall-row-link' target='_blank' rel='noopener noreferrer'></a>")
                                .attr("href", "game.html?gameId=" + gameId + participantIdAppend)
                                .text("Play the game")
                                .button());
                        } else if (watchable == "true") {
                            var participantId = getUrlParam("participantId");
                            var participantIdAppend = "";
                            if (participantId != null)
                                participantIdAppend = "&participantId=" + participantId;

                            lastField.append($("<a class='hall-row-link' target='_blank' rel='noopener noreferrer'></a>")
                                .attr("href", "game.html?gameId=" + gameId + participantIdAppend)
                                .text("Watch game")
                                .button());
                        }
                    } else if (status == "FINISHED") {
                        if (winner != null) {
                            lastField.append(winner);
                        }
                        if (gameId)
                            this.closeReadyModal("game:" + gameId);
                    }

                    row.append(lastField);

                    if (action == "add") {
                        if (status == "WAITING") {
                            $("table.waitingTables", this.tablesDiv)
                                .append(row);
                        } else if (status == "PLAYING") {
                            $("table.playingTables", this.tablesDiv)
                                .append(row);
                        } else if (status == "FINISHED") {
                            $("table.finishedTables", this.tablesDiv)
                                .append(row);
                        }
                    } else if (action == "update") {
                        var existingTable = $(".table" + id, this.tablesDiv);
                        this.adoptClockSpan(existingTable, row);
                        if (status == "WAITING") {
                            if ($(".table" + id, $("table.waitingTables")).length > 0) {
                                existingTable.replaceWith(row);
                            } else {
                                existingTable.remove();
                                $("table.waitingTables", this.tablesDiv)
                                    .append(row);
                            }
                        } else if (status == "PLAYING") {
                            if ($(".table" + id, $("table.playingTables")).length > 0) {
                                existingTable.replaceWith(row);
                            } else {
                                existingTable.remove();
                                $("table.playingTables", this.tablesDiv)
                                    .append(row);
                            }
                        } else if (status == "FINISHED") {
                            if ($(".table" + id, $("table.finishedTables")).length > 0) {
                                existingTable.replaceWith(row);
                            } else {
                                existingTable.remove();
                                $("table.finishedTables", this.tablesDiv)
                                    .append(row);
                            }
                        }

                        this.animateRowUpdate(".table" + id);
                    }

                    if (playing == "true")
                        row.addClass("played");
                } else if (action == "remove") {
                    $(".table" + id, this.tablesDiv).remove();
                }
            }

            $(".count", $(".eventHeader.queues")).html("(" + ($("tr", $("table.queues")).length - 1) + ")");
            $(".count", $(".eventHeader.tournaments")).html("(" + ($("tr", $("table.tournaments")).length - 1) + ")");
            $(".count", $(".eventHeader.waitingTables")).html("(" + ($("tr", $("table.waitingTables")).length - 1) + ")");
            $(".count", $(".eventHeader.playingTables")).html("(" + ($("tr", $("table.playingTables")).length - 1) + ")");
            $(".count", $(".eventHeader.finishedTables")).html("(" + ($("tr", $("table.finishedTables")).length - 1) + ")");
            this.refreshTableAges();

            var games = root.getElementsByTagName("newGame");
            var started = false;
            if (this.offeredGames == null)
                this.offeredGames = {};
            for (var i=0; i<games.length; i++) {
                var gameId = games[i].getAttribute("id");
                if (gameId == null || this.offeredGames[gameId])
                    continue;
                this.offeredGames[gameId] = true;
                started = true;
                that.openStartedGame(gameId, null);
            }
            if (started) {
                this.playSound("gamestart");
            }
            this.refreshInTournament();

            if (!this.supportedFormatsInitialized) {
                var formats = root.getElementsByTagName("format");
                for (var i = 0; i < formats.length; i++) {
                    var format = formats[i].childNodes[0].nodeValue;
                    var type = formats[i].getAttribute("type");
                    this.supportedFormatsSelect.append("<option value='" + type + "'>" + format + "</option>");
                }
                this.supportedFormatsInitialized = true;
                // Mark league-type options via /league list (hall emits both as plain <format>)
                this.tagLeagueFormats();
                this.populateTournamentFormats();
            }

            var layoutChanged = false;
            if (this.supportedFormatsSelect.css("display") == "none") {
                this.supportedFormatsSelect.css("display", "");
                layoutChanged = true;
            }
            if (this.createTableButton.css("display") == "none") {
                this.createTableButton.css("display", "");
                layoutChanged = true;
            }
            if (layoutChanged) {
                this.refreshLayout();
            }

            setTimeout(function () {
                that.updateHall();
            }, 100);
        }
    }
});
