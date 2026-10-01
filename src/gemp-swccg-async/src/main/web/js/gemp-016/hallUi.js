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
    leagueCreateButton:null,
    leagueResultDiv:null,
    playLeagueNextSteps:null,
    playLeagueList:null,
    playLeagueUI:null,
    leagueCache:null, // [{type,name,member,start,end}, ...]
    playBackButton:null,
    playMode:null, // "casual" | "ai" | "league" | "tournament"
    leagueTypesLoaded:false,
    privateGamesAllowed:false,

    // Slice 1.1: Join table / queue deck picker overlay
    joinOverlay:null,
    joinDecksSelect:null,
    joinSubmitButton:null,
    joinResultDiv:null,
    joinContextDiv:null,
    joinTitleEl:null,
    joinPending:null, // {kind:"table"|"queue", id, formatName, contextLabel}

    init:function (div, url, chat) {
        this.div = div;
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
        // Currency / pocket removed from primary bar (still tracked for merchant elsewhere via pocketValue)

        // Create-table form controls live in the Play overlay (not the always-visible strip)
        this.supportedFormatsSelect = $("<select class='play-form-select' style='width: 175px'></select>");
        this.supportedFormatsSelect.hide();

        this.createTableButton = $("<button type='button' class='play-submit-button'>Create table</button>");
        $(this.createTableButton).button().click(function () {
            that.submitCreateTable();
        });
        this.createTableButton.hide();

        this.isPrivateCheckbox = $("<label class='play-private-label'><input type='checkbox' id='isPrivateCheckbox1'> Private game</input></label>");

        this.decksSelect = $("<select class='play-form-select' style='width: 300px'></select>");
        this.decksSelect.hide();
        this.decksSelect.change(function () { that.updateAiDecksForSelection(); });

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

        this.aiDeckSelect = $("<select class='play-form-select' style='width: 280px'></select>");

        this.aiControlsDiv = $("<div class='play-ai-controls'></div>");
        var aiSkillRow = $("<div class='play-form-row'></div>");
        aiSkillRow.append("<span class='play-form-label'>Bot skill</span>");
        aiSkillRow.append(this.aiSkillSelect);
        var aiDeckRow = $("<div class='play-form-row'></div>");
        aiDeckRow.append("<span class='play-form-label'>Bot deck</span>");
        aiDeckRow.append(this.aiDeckSelect);
        this.aiControlsDiv.append(aiSkillRow);
        this.aiControlsDiv.append(aiDeckRow);
        this.aiControlsDiv.hide();

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
        this.updateDecks();
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
        this.playAiChoice.click(function () { that.showPlayForm("ai"); });
        this.playCasualChoice.click(function () { that.showPlayForm("casual"); });
        this.playLeagueChoice.click(function () { that.showLeaguePanel(); });
        this.playTournamentChoice.click(function () { that.showTournamentInfo(); });
        // LOTR order: Bot, Casual, League, Tournament
        this.playSelectionPanel.append(this.playAiChoice);
        this.playSelectionPanel.append(this.playCasualChoice);
        this.playSelectionPanel.append(this.playLeagueChoice);
        this.playSelectionPanel.append(this.playTournamentChoice);
        panel.append(this.playSelectionPanel);

        this.playFormPanel = $("<div id='create-table-form' class='table-form' style='display:none'></div>");
        this.playFormTitle = $("<h2 class='play-form-heading'>Open Casual Table</h2>");
        this.playFormPanel.append(this.playFormTitle);

        this.playFormFields = $("<div class='inner-table-form'></div>");

        var formatRow = $("<div class='play-form-row'></div>");
        formatRow.append("<span class='play-form-label'>Format</span>");
        formatRow.append(this.supportedFormatsSelect);

        var deckRow = $("<div class='play-form-row'></div>");
        deckRow.append("<span class='play-form-label'>Your deck</span>");
        deckRow.append(this.decksSelect);

        var descRow = $("<div class='play-form-row'></div>");
        descRow.append("<span class='play-form-label'>Description</span>");
        descRow.append(this.tableDescInput);

        var privateRow = $("<div class='play-form-row play-form-row-check'></div>");
        privateRow.append(this.isPrivateCheckbox);

        var submitRow = $("<div class='play-form-row play-form-actions'></div>");
        submitRow.append(this.createTableButton);

        this.playFormFields.append(formatRow);
        this.playFormFields.append(deckRow);
        this.playFormFields.append(this.aiControlsDiv);
        this.playFormFields.append(descRow);
        this.playFormFields.append(privateRow);
        this.playFormFields.append(submitRow);
        this.playFormPanel.append(this.playFormFields);

        this.playLeagueEmpty = $("<div class='play-league-empty' style='display:none'></div>");
        this.playLeagueEmpty.append("<p class='play-subtitle'>You are not in any active league that offers a table format right now.</p>");
        this.playLeagueEmpty.append("<p class='play-subtitle'>Join a league from the <b>Events</b> tab (league results / join), then return here to open a league table.</p>");
        this.playFormPanel.append(this.playLeagueEmpty);

        panel.append(this.playFormPanel);

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
        var leagueFmtRow = $("<div class='flex-horiz play-form-row'></div>");
        leagueFmtRow.append("<div class='label-column'>League: </div>");
        leagueFmtRow.append(this.leagueFormatSelect);
        leagueOptions.append(leagueFmtRow);

        var playerDeckBlock = $("<div class='player-deck flex-vert'></div>");
        this.leagueDecksSelect = $("<select id='league-deck' class='player-deck-dropdown flex-fill play-form-select'></select>");
        var leagueDeckRow = $("<div class='flex-horiz play-form-row'></div>");
        leagueDeckRow.append("<div class='label-column'>Deck: </div>");
        leagueDeckRow.append(this.leagueDecksSelect);
        playerDeckBlock.append(leagueDeckRow);

        // SWCCG has getLibraryDecks / sample decks — wire Select Library Deck like LOTR SelectDeck
        this.leagueLibraryDecksSelect = $("<select id='league-library-deck' class='library-deck-dropdown flex-fill play-form-select'></select>");
        var leagueLibRow = $("<div class='flex-horiz play-form-row'></div>");
        leagueLibRow.append("<div class='label-column'>Select Library Deck: <span class='info-toggle' data-for='help-library-league' role='button' tabindex='0' title='What is this?' aria-expanded='false'>i</span></div>");
        leagueLibRow.append(this.leagueLibraryDecksSelect);
        playerDeckBlock.append(leagueLibRow);
        playerDeckBlock.append("<div id='help-library-league' class='info-text' style='display:none'>The Deck Library contains sample decks you can use, including starter decks, past championship decks, and more.</div>");
        leagueOptions.append(playerDeckBlock);

        this.leagueDecksSelect.change(function () {
            if (that.leagueDecksSelect.val()) {
                that.leagueLibraryDecksSelect.val("");
            }
        });
        this.leagueLibraryDecksSelect.change(function () {
            if (that.leagueLibraryDecksSelect.val()) {
                that.leagueDecksSelect.val("");
            }
        });

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

        panel.append(this.playLeaguePanel);

        this.playTournamentPanel = $("<div id='create-tournament-info' class='table-form play-tournament-info' style='display:none'></div>");
        this.playTournamentPanel.append("<h2 class='play-form-heading'>Create Tournament</h2>");
        this.playTournamentPanel.append("<p class='play-subtitle'>SWCCG GEMP has no player-facing host-tournament API (unlike LOTR). Tournaments are server-scheduled queues — join them from the Game Hall when a queue is listed above.</p>");
        this.playTournamentPanel.append("<p class='play-subtitle'>This button stays visible to mirror the LOTR Play menu; hosting is not available from the client.</p>");
        panel.append(this.playTournamentPanel);

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

        this.joinDecksSelect = $("<select class='play-form-select' style='width: 300px'></select>");

        var deckRow = $("<div class='play-form-row'></div>");
        deckRow.append("<span class='play-form-label'>Your deck</span>");
        deckRow.append(this.joinDecksSelect);
        form.append(deckRow);

        this.joinResultDiv = $("<div class='join-result warningMessage' style='display:none'></div>");
        form.append(this.joinResultDiv);

        var submitRow = $("<div class='play-form-row play-form-actions'></div>");
        this.joinSubmitButton = $("<button type='button' class='play-submit-button'>Join table</button>");
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

    // requiredSide: "light"|"dark" to filter join decks; null/undefined = show all (queues).
    syncJoinDecksFromCreateSelect:function(requiredSide) {
        if (this.joinDecksSelect == null || this.decksSelect == null) {
            return { count: 0, hasPlaceholder: false };
        }
        var prev = this.joinDecksSelect.val();
        var select = this.joinDecksSelect;
        select.empty();
        var matchCount = 0;
        this.decksSelect.find("option").each(function () {
            var src = $(this);
            var side = src.attr("data-side");
            if (requiredSide != null && requiredSide !== "" && side !== requiredSide)
                return;
            var opt = $("<option></option>");
            opt.attr("value", src.attr("value"));
            var sample = src.attr("data-sample-deck");
            if (sample != null)
                opt.attr("data-sample-deck", sample);
            if (side != null)
                opt.attr("data-side", side);
            opt.text(src.text());
            select.append(opt);
            matchCount++;
        });
        var hasPlaceholder = false;
        if (requiredSide != null && requiredSide !== "" && matchCount === 0) {
            var ph = $("<option></option>");
            ph.attr("value", "");
            ph.attr("disabled", "disabled");
            ph.attr("selected", "selected");
            ph.text("No opposite-side decks found");
            select.append(ph);
            hasPlaceholder = true;
        } else {
            var prevOk = false;
            if (prev != null && prev !== "") {
                select.find("option").each(function () {
                    if ($(this).attr("value") === prev) {
                        prevOk = true;
                        return false;
                    }
                });
            }
            if (prevOk) {
                select.val(prev);
            } else if (matchCount > 0) {
                // Prefer first opposite-side (or any) deck when opening / refreshing
                select.prop("selectedIndex", 0);
            }
        }
        return { count: matchCount, hasPlaceholder: hasPlaceholder };
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

    openJoinTablePopup:function(tableId, formatName, playersStr) {
        var hostSide = this.parseHostSideFromPlayersStr(playersStr);
        var requiredSide = this.oppositeForceSide(hostSide);
        this.openJoinPopup({
            kind: "table",
            id: tableId,
            formatName: formatName || "",
            hostSide: hostSide,
            requiredSide: requiredSide,
            contextLabel: this.buildJoinContextLabel(formatName, playersStr, null, hostSide, requiredSide)
        });
    },

    openJoinQueuePopup:function(queueId, formatName, queueName) {
        // Queues have no host side — show all decks (unfiltered).
        this.openJoinPopup({
            kind: "queue",
            id: queueId,
            formatName: formatName || "",
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
        // Slice 1.5e / LOTR JoinTable.showPopup: refresh decks on every Join open
        // (processLibraryDecks re-syncs join dropdown when overlay is visible).
        this.updateDecks();
        var syncResult = this.syncJoinDecksFromCreateSelect(pending.requiredSide);
        this.joinResultDiv.hide().empty();

        var isQueue = pending.kind === "queue";
        this.joinTitleEl.text(isQueue ? "Join Queue" : "Join Table");
        var button = $(this.joinSubmitButton);
        var label = isQueue ? "Join queue" : "Join table";
        if (button.hasClass("ui-button")) {
            button.button("option", "label", label);
        } else {
            button.text(label);
        }

        var canJoin = !(syncResult && syncResult.hasPlaceholder) && syncResult && syncResult.count > 0;
        this.setJoinSubmitEnabled(canJoin);
        if (syncResult && syncResult.hasPlaceholder) {
            this.showJoinError("No opposite-side decks found. Build or import a " +
                (pending.requiredSide ? pending.requiredSide.toUpperCase() : "matching") +
                " deck, then try again.");
        }

        if (pending.contextLabel) {
            this.joinContextDiv.html(pending.contextLabel).show();
        } else {
            this.joinContextDiv.hide().empty();
        }

        this.joinOverlay.css("display", "flex");
        $("body").addClass("play-flow-open");
        this.joinDecksSelect.focus();
    },

    closeJoinOverlay:function() {
        if (this.joinOverlay == null) {
            return;
        }
        this.joinOverlay.hide();
        this.joinPending = null;
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
        var selectEl = this.joinDecksSelect[0];
        if (selectEl == null || selectEl.selectedIndex < 0 || selectEl.options.length === 0) {
            this.showJoinError("You must select a deck.");
            return;
        }
        var deck = this.joinDecksSelect.val();
        if (deck == null || deck === "") {
            if (this.joinPending != null && this.joinPending.requiredSide != null) {
                this.showJoinError("No opposite-side decks found. You must play " +
                    this.joinPending.requiredSide.toUpperCase() + ".");
            } else {
                this.showJoinError("You must select a deck.");
            }
            return;
        }
        var selectedOpt = selectEl.options[selectEl.selectedIndex];
        if (selectedOpt != null && selectedOpt.disabled) {
            this.showJoinError("No opposite-side decks found.");
            return;
        }
        if (this.joinPending != null && this.joinPending.requiredSide != null) {
            var optSide = selectedOpt != null ? selectedOpt.getAttribute("data-side") : null;
            if (optSide != null && optSide !== this.joinPending.requiredSide) {
                this.showJoinError("That deck is the wrong Force side. You must play " +
                    this.joinPending.requiredSide.toUpperCase() + ".");
                return;
            }
        }
        var sampleDeck = selectEl[selectEl.selectedIndex].getAttribute("data-sample-deck");
        var pending = this.joinPending;
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
        // Slice 1.5e / LOTR CreateTable.showPopup: every Play open re-fetches /deck/list
        // so decks built in deckBuild.html (new tab) after hall init appear without hard refresh.
        this.updateDecks();
        this.showPlaySelection();
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
        if (this.playTournamentPanel != null) {
            this.playTournamentPanel.hide();
        }
        if (this.playLeaguePanel != null) {
            this.playLeaguePanel.hide();
        }
        this.playOverlay.removeClass("play-flow-league");
        this.playSelectionPanel.show();
        this.setPlayFlowTitle(null);
        this.playBackButton.html("&lt; Back");
        // AI choice visibility follows server flag
        if (this.aiTablesEnabled) {
            this.playAiChoice.show();
        } else {
            this.playAiChoice.hide();
        }
        // Restore private checkbox visibility for next form open
        if (this.privateGamesAllowed) {
            this.isPrivateCheckbox.show();
        } else {
            this.isPrivateCheckbox.hide();
        }
    },

    showTournamentInfo:function() {
        this.playMode = "tournament";
        this.playSelectionPanel.hide();
        this.playFormPanel.hide();
        if (this.playLeaguePanel != null) {
            this.playLeaguePanel.hide();
        }
        this.playOverlay.removeClass("play-flow-league");
        this.setPlayFlowTitle("Tournament");
        this.playTournamentPanel.show();
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
        this.playFormPanel.show();

        if (mode === "ai") {
            this.setPlayFlowTitle("Bots");
            this.playFormTitle.text("Play Against Bots (Beta)");
            this.opponentSelect.val("ai");
            if (this.privateGamesAllowed) {
                this.isPrivateCheckbox.show();
            }
            if (this.playLeagueEmpty != null) {
                this.playLeagueEmpty.hide();
            }
            this.playFormFields.show();
        } else {
            this.setPlayFlowTitle("Casual");
            this.playFormTitle.text("Open Casual Table");
            this.opponentSelect.val("human");
            if (this.privateGamesAllowed) {
                this.isPrivateCheckbox.show();
            }
            if (this.playLeagueEmpty != null) {
                this.playLeagueEmpty.hide();
            }
            this.playFormFields.show();
        }

        this.filterFormatsForPlayMode(mode);
        this.updateAiDecksForSelection();
        this.updateCreateTableLabel();

        // Ensure selects are visible inside the form once hall has loaded formats/decks
        if (this.supportedFormatsInitialized) {
            this.supportedFormatsSelect.css("display", "");
            this.decksSelect.css("display", "");
            this.createTableButton.css("display", "");
        }
    },

    // Slice 1.5b — LOTR CreateLeagueTable parity: Open League Table + Join Leagues
    showLeaguePanel:function() {
        this.playMode = "league";
        this.playSelectionPanel.hide();
        this.playFormPanel.hide();
        if (this.playTournamentPanel != null) {
            this.playTournamentPanel.hide();
        }
        this.playOverlay.addClass("play-flow-league");
        this.setPlayFlowTitle("League");
        if (this.playLeagueNextSteps != null) {
            this.playLeagueNextSteps.hide().empty();
        }
        if (this.leagueResultDiv != null) {
            this.leagueResultDiv.removeClass("result-error result-success").text("Ready.");
        }
        // Slice 1.5e belt-and-suspenders: refresh even if Play overlay already refreshed
        // (processLibraryDecks re-syncs league dropdowns when this panel is visible).
        this.updateDecks();
        this.syncLeagueDecksFromCreateSelect();
        this.refreshLeagueDropdownAndList();
        this.playLeaguePanel.show();
    },

    syncLeagueDecksFromCreateSelect:function() {
        if (this.leagueDecksSelect == null || this.decksSelect == null) {
            return;
        }
        var prevPlayer = this.leagueDecksSelect.val();
        var prevLib = this.leagueLibraryDecksSelect != null ? this.leagueLibraryDecksSelect.val() : null;
        var playerSelect = this.leagueDecksSelect;
        var libSelect = this.leagueLibraryDecksSelect;
        playerSelect.empty();
        if (libSelect != null) {
            libSelect.empty();
            libSelect.append($("<option></option>").attr("value", "").text("Choose a Deck Library deck"));
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
            if (isSample && libSelect != null) {
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
        if (libSelect != null && prevLib != null && prevLib !== "") {
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
        }, {
            "0": function () {
                that.populateLeagueDropdown(null);
                that.ensurePlayLeagueListUI();
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
        // Refresh membership + select the new league; offer next steps
        this.comm.getLeagues(function (xml) {
            that.applyLeagueCache(xml);
            that.populateLeagueDropdown(leagueCode);
            that.showLeagueNextSteps(leagueCode);
        }, {
            "0": function () {
                that.populateLeagueDropdown(leagueCode);
                that.showLeagueNextSteps(leagueCode);
            }
        });
    },

    showLeagueNextStepsMessage:function(text, isError) {
        if (this.playLeagueNextSteps == null) {
            return;
        }
        this.playLeagueNextSteps.empty();
        if (isError) {
            this.playLeagueNextSteps.addClass("play-error");
        } else {
            this.playLeagueNextSteps.removeClass("play-error");
        }
        this.playLeagueNextSteps.append($("<div></div>").text(text));
        this.playLeagueNextSteps.show();
    },

    showLeagueNextSteps:function(leagueCode) {
        var that = this;
        var name = leagueCode;
        var cache = this.leagueCache || [];
        for (var i = 0; i < cache.length; i++) {
            if (cache[i].type === leagueCode && cache[i].name) {
                name = cache[i].name;
                break;
            }
        }
        this.showLeagueNextStepsMessage("You joined " + name + ". Next:", false);
        var buttons = $("<div class='play-next-buttons'></div>");
        buttons.append($("<button type='button'></button>").text("Create a table in this league").button().click(function () {
            that.populateLeagueDropdown(leagueCode);
            that.leagueFormatSelect.focus();
            var form = $("#league-table-options")[0];
            if (form && form.scrollIntoView) {
                form.scrollIntoView(true);
            }
        }));
        this.playLeagueNextSteps.append(buttons);

        // Draft / limited next-steps (SWCCG soloDraft.html + deckBuild.html)
        this.comm.getLeague(leagueCode, function (xml) {
            var root = xml && xml.documentElement;
            if (root == null || root.tagName != "league") {
                return;
            }
            if (root.getAttribute("draftable") == "true") {
                buttons.append($("<button type='button'></button>").text("Go to draft").button().click(function () {
                    var win = window.open("soloDraft.html?leagueType=" + encodeURIComponent(leagueCode), "_blank");
                    if (win) {
                        win.focus();
                    }
                }));
            }
            var limited = false;
            var series = root.getElementsByTagName("serie");
            for (var i = 0; i < series.length; i++) {
                if (series[i].getAttribute("limited") == "true") {
                    limited = true;
                }
            }
            if (limited) {
                buttons.append($("<a href='deckBuild.html' target='_blank' rel='noopener'></a>")
                    .text("Open the deck builder").button());
                that.playLeagueNextSteps.append($("<div class='page-hint play-subtitle'></div>").text(
                    "This league issues its own cards: in the deck builder, choose the league's collection to open your packs and build a deck from them. League tables only accept such decks."));
            }
        }, {});
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
        var libVal = (this.leagueLibraryDecksSelect != null) ? this.leagueLibraryDecksSelect.val() : null;
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
        this.comm.createTable(format, deck, sampleDeck, "", false, false, null, null, null,
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
        if (firstEnabled != null) {
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
        var deck = that.decksSelect.val();
        var sampleDeck = that.decksSelect[0][that.decksSelect[0].selectedIndex].getAttribute("data-sample-deck");
        var tableDesc = that.tableDescInput.val();
        var isPrivate = false;
        if (document.getElementById('isPrivateCheckbox1') != null)
            isPrivate = document.getElementById('isPrivateCheckbox1').checked;
        var playVsAi = that.opponentSelect.val() === "ai";
        // League tables: server rejects private + bot; force safe values client-side too
        if (that.playMode === "league") {
            isPrivate = false;
            playVsAi = false;
        }
        var aiSkill = that.aiSkillSelect.val();
        var aiDeckName = that.aiDeckSelect.val();
        var aiDeckSample = that.aiDeckSelect.find(":selected").attr("data-sample-deck");
        if (deck != null) {
            $(that.createTableButton).button("disable");
            that.comm.createTable(format, deck, sampleDeck, tableDesc, isPrivate, playVsAi, aiSkill, aiDeckName, aiDeckSample, function (xml) {
                if (xml != null) {
                    var root = xml.documentElement;
                    if (root.tagName == "error") {
                        var message = root.getAttribute("message");
                        that.chat.appendMessage(message, "warningMessage");
                    }
                    else if (root.tagName == "response") {
                        var message = root.getAttribute("message");
                        that.chat.appendMessage(message, "warningMessage");
                        that.showDialog("Info", message, 320);
                        that.closePlayOverlay();
                    }
                    else {
                        that.closePlayOverlay();
                    }
                } else {
                    // Null/empty response = create accepted; return to hall tables
                    that.closePlayOverlay();
                }

                // Re-enable the button after a short delay to prevent accidental double-clicks
                setTimeout(function() {
                    $(that.createTableButton).button("enable");
                }, 2000);
            });
        }
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
    // Called at hall init and on every Play / Join / League-panel open (LOTR DeckManager parity)
    // so decks saved in deckBuild.html after hall load appear without a hard refresh.
    // No client-side format/sealed filter — SelectDeck-style: show all; server validates on createTable.
    updateDecks:function () {
        var that = this;
        this.deckOptions = [];
        this.decksSelect.html("");
        this.aiDeckSelect.html("");
        this.comm.getDecks(function (xml) {
            that.processDecks(xml);
            that.comm.getLibraryDecks(function (xml2) {
                that.processLibraryDecks(xml2);
                that.updateAiDecksForSelection();
            });
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
        this.decksSelect.css("display", "");
        if (this.joinOverlay != null && this.joinOverlay.is(":visible")) {
            var req = (this.joinPending != null) ? this.joinPending.requiredSide : null;
            var syncResult = this.syncJoinDecksFromCreateSelect(req);
            var canJoin = !(syncResult && syncResult.hasPlaceholder) && syncResult && syncResult.count > 0;
            this.setJoinSubmitEnabled(canJoin);
        }
        if (this.playLeaguePanel != null && this.playLeaguePanel.is(":visible")) {
            this.syncLeagueDecksFromCreateSelect();
        }
    },

    generateDeckRow:function (decks, prefix, sampleDeck, side) {
        var that = this;
        for (var i = 0; i < decks.length; i++) {
            var deck = decks[i];

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
            this.decksSelect.append(deckElem);

            // Track for AI selection (player and sample decks)
            this.deckOptions.push({name: deckName, sample: sampleDeck === "true", side: side, label: prefix + deckName});
        }
    },

    getSelectedDeckSide:function() {

        var opt = this.decksSelect.find(":selected");
        if (opt == null || opt.length == 0)
            return null;
        var side = opt.attr("data-side");
        return side;
    },

    updateCreateTableLabel:function() {
        if (this.createTableButton == null || this.opponentSelect == null) {
            return false;
        }
        var label = this.opponentSelect.val() === "ai" ? "Start Bot Game" : "Create table";
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
        var layoutChanged = this.updateCreateTableLabel();
        if (!this.aiTablesEnabled) {
            if (this.aiControlsDiv.css("display") != "none") {
                this.aiControlsDiv.hide();
                layoutChanged = true;
            }
            if (layoutChanged) {
                this.refreshLayout();
            }
            return;
        }
        var playingVsAi = this.opponentSelect.val() === "ai";
        if (!playingVsAi) {
            if (this.aiControlsDiv.css("display") != "none") {
                this.aiControlsDiv.hide();
                layoutChanged = true;
            }
            if (layoutChanged) {
                this.refreshLayout();
            }
            return;
        }
        if (this.aiControlsDiv.css("display") == "none") {
            this.aiControlsDiv.show();
            layoutChanged = true;
        }
        this.aiControlsDiv.show();

        var playerSide = this.getSelectedDeckSide();
        var shouldRebuild = false;

        if (this.aiDeckSelect.children().length === 0) {
            shouldRebuild = true;
        }
        if (this.lastAiDeckPlayerSide !== playerSide) {
            shouldRebuild = true;
        }
        if (!shouldRebuild) {
            if (layoutChanged) {
                this.refreshLayout();
            }
            return;
        }

        var previousSelection = this.aiDeckSelect.val();
        var previousSample = this.aiDeckSelect.find(":selected").attr("data-sample-deck");
        this.aiDeckSelect.html("");
        this.lastAiDeckPlayerSide = playerSide;

        var added = false;
        for (var i = 0; i < this.deckOptions.length; i++) {
            var opt = this.deckOptions[i];
            if (playerSide != null) {
                if (opt.side === "dark" && playerSide === "dark")
                    continue;
                if (opt.side === "light" && playerSide === "light")
                    continue;
            }
            var option = $("<option></option>");
            option.attr("value", opt.name);
            option.attr("data-side", opt.side);
            option.attr("data-sample-deck", opt.sample ? "true" : "false");
            option.text(opt.label);
            this.aiDeckSelect.append(option);
            added = true;
        }

        if (!added) {
            var placeholder = $("<option disabled selected>No opposite-side decks found</option>");
            this.aiDeckSelect.append(placeholder);
        } else if (previousSelection != null) {
            var restored = false;
            this.aiDeckSelect.find("option").each(function() {
                var option = $(this);
                if (option.attr("value") === previousSelection &&
                        option.attr("data-sample-deck") === previousSample) {
                    option.prop("selected", true);
                    restored = true;
                    return false;
                }
            });
            if (!restored) {
                this.aiDeckSelect.val(previousSelection);
            }
        }
        this.refreshLayout();
    },

    setAiTablesEnabled:function(enabled) {
        if (this.aiTablesEnabled === enabled) {
            return;
        }
        this.aiTablesEnabled = enabled;

        var aiOption = this.opponentSelect.find("option[value='ai']");
        if (enabled) {
            // opponentSelect stays hidden; Play submenu chooses AI vs Casual
            if (aiOption.length === 0) {
                this.opponentSelect.append("<option value='ai'>vs Bot</option>");
            }
            if (this.playAiChoice != null) {
                this.playAiChoice.show();
            }
        } else {
            if (aiOption.length > 0) {
                aiOption.remove();
            }
            if (this.opponentSelect.val() === "ai") {
                this.opponentSelect.val("human");
            }
            this.aiControlsDiv.hide();
            if (this.playAiChoice != null) {
                this.playAiChoice.hide();
            }
            if (this.playMode === "ai") {
                this.showPlaySelection();
            }
        }
        this.updateAiDecksForSelection();
    },

    animateRowUpdate: function(rowSelector) {
        $(rowSelector, this.tablesDiv)
            .css({borderTopColor:"#000000", borderLeftColor:"#000000", borderBottomColor:"#000000", borderRightColor:"#000000"})
            .animate({borderTopColor:"#ffffff", borderLeftColor:"#ffffff", borderBottomColor:"#ffffff", borderRightColor:"#ffffff"}, "fast");
    },
    
    playSound: function(soundObj) {
        var myAudio = document.getElementById(soundObj);
        myAudio.play();
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
            if (this.privateGamesAllowed) {
               // Do not force-show while league form is open (private rejected server-side)
               if (this.playMode !== "league") {
                   this.isPrivateCheckbox.show();
               }
            }
            else {
               if(document.getElementById('isPrivateCheckbox1')!=null)
                   document.getElementById('isPrivateCheckbox1').checked = false;
               this.isPrivateCheckbox.hide();
            }

            var aiTablesEnabled = root.getAttribute("aiTablesEnabledBoolean");
            if (aiTablesEnabled != null && aiTablesEnabled.length > 0) {
                this.setAiTablesEnabled(aiTablesEnabled == "true");
            }


            var motd = root.getAttribute("motd");
            if (motd != null)
                $("#motd").html("<b>MOTD:</b> " + motd);

            var serverTime = root.getAttribute("serverTime");
            if (serverTime != null) {
                // top info strip (legacy)
                $(".serverTime").text("Server time: " + serverTime);
                // primary bar right (LOTR .server-time: date<br>time) under explicit "Server Time" label
                if (this.serverTimeValue)
                    this.serverTimeValue.html(serverTime.replace(" ", "<br>"));
                if (this.connection)
                    this.connection.updated(null, serverTime);
            } else if (this.connection) {
                this.connection.updated(null, this.connection.lastUpdate);
            }

            var queues = root.getElementsByTagName("queue");
            for (var i = 0; i < queues.length; i++) {
                var queue = queues[i];
                var id = queue.getAttribute("id");
                var action = queue.getAttribute("action");
                if (action == "add" || action == "update") {
                    var actionsField = $("<td></td>");

                    var joined = queue.getAttribute("signedUp");
                    if (joined != "true" && queue.getAttribute("joinable") == "true") {
                        var but = $("<button>Join queue</button>");
                        $(but).button().click((
                            function(queueId, fmt, qname) {
                                return function () {
                                    that.openJoinQueuePopup(queueId, fmt, qname);
                                };
                            }
                            )(id, queue.getAttribute("format"), queue.getAttribute("queue")));
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
                } else if (action == "remove") {
                    $(".queue" + id, this.tablesDiv).remove();
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
                    if (joined == "true") {
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
                    row.append("<td>" + statusDescription + "</td>");

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

                            var but = $("<button>Join table</button>");
                            $(but).button().click((
                                function(tableId, fmt, owners) {
                                    return function() {
                                        that.openJoinTablePopup(tableId, fmt, owners);
                                    };
                                })(id, formatName, playersStr));
                            lastField.append(but);
                        }
                    } else if (status == "PLAYING") {
                        if (playing == "true") {
                            var participantId = getUrlParam("participantId");
                            var participantIdAppend = "";
                            if (participantId != null)
                                participantIdAppend = "&participantId=" + participantId;

                            lastField.append("<a href='game.html?gameId=" + gameId + participantIdAppend + "' target='_blank' rel='noopener noreferrer'>Play the game</a>");
                        } else if (watchable == "true") {
                            var participantId = getUrlParam("participantId");
                            var participantIdAppend = "";
                            if (participantId != null)
                                participantIdAppend = "&participantId=" + participantId;

                            lastField.append("<a href='game.html?gameId=" + gameId + participantIdAppend + "' target='_blank' rel='noopener noreferrer'>Watch game</a>");
                        }
                    } else if (status == "FINISHED") {
                        if (winner != null) {
                            lastField.append(winner);
                        }
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
                        if (status == "WAITING") {
                            if ($(".table" + id, $("table.waitingTables")).length > 0) {
                                $(".table" + id, this.tablesDiv).replaceWith(row);
                            } else {
                                $(".table" + id, this.tablesDiv).remove();
                                $("table.waitingTables", this.tablesDiv)
                                    .append(row);
                            }
                        } else if (status == "PLAYING") {
                            if ($(".table" + id, $("table.playingTables")).length > 0) {
                                $(".table" + id, this.tablesDiv).replaceWith(row);
                            } else {
                                $(".table" + id, this.tablesDiv).remove();
                                $("table.playingTables", this.tablesDiv)
                                    .append(row);
                            }
                        } else if (status == "FINISHED") {
                            if ($(".table" + id, $("table.finishedTables")).length > 0) {
                                $(".table" + id, this.tablesDiv).replaceWith(row);
                            } else {
                                $(".table" + id, this.tablesDiv).remove();
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

            var games = root.getElementsByTagName("newGame");
            for (var i=0; i<games.length; i++) {
                var waitingGameId = games[i].getAttribute("id");
                var participantId = getUrlParam("participantId");
                var participantIdAppend = "";
                if (participantId != null)
                    participantIdAppend = "&participantId=" + participantId;
                window.open("/gemp-swccg/game.html?gameId=" + waitingGameId + participantIdAppend, "_blank");
            }
            
            if (games.length > 0) {
                this.playSound("gamestart");
            }

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
            }

            var layoutChanged = false;
            if (this.supportedFormatsSelect.css("display") == "none") {
                this.supportedFormatsSelect.css("display", "");
                layoutChanged = true;
            }
            if (this.decksSelect.css("display") == "none") {
                this.decksSelect.css("display", "");
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
