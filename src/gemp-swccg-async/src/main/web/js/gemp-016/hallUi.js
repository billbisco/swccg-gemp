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
    playBackButton:null,
    playMode:null, // "casual" | "ai" 

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

        this.buttonsDiv = $("<div class='hall-primary-bar'></div>");
        this.buttonsDiv.css({left:"0px", top:(height - 56) + "px", width:width + "px", backgroundColor:"#000000", "border-top-width":"1px", "border-top-color":"#ffffff", "border-top-style":"solid", "box-sizing":"border-box", padding:"6px 8px", display:"grid", "grid-template-columns":"1fr auto", "column-gap":"10px", "align-items":"center"});

        var that = this;

        this.controlsLeft = $("<div class='hall-primary-actions'></div>");
        this.controlsLeft.css({display:"flex", "flex-wrap":"wrap", "align-items":"center", gap:"10px", flex:"1 1 auto", "min-width":"0"});

        this.controlsRight = $("<div class='hall-primary-meta'></div>");
        this.controlsRight.css({display:"flex", "align-items":"center", gap:"8px", "justify-self":"end", "white-space":"nowrap"});

        this.buttonsDiv.append(this.controlsLeft);
        this.buttonsDiv.append(this.controlsRight);

        // Large primary actions (LOTR-style): Deck-builder + Play
        this.deckBuilderButton = $("<a id='deckbuilder-button' class='hall-play-button hall-play-button-deckbuilder' href='deckBuild.html' target='_blank'><span class='hall-play-button-label'><b>Deck-builder</b></span></a>");
        this.playButton = $("<button type='button' id='open-table-button' class='hall-play-button hall-play-button-play'><span class='hall-play-button-label'><b>Play</b></span></button>");
        this.controlsLeft.append(this.deckBuilderButton);
        this.controlsLeft.append(this.playButton);
        $(this.playButton).button().click(function () {
            that.openPlayOverlay();
        });
        // jQuery UI button() on an <a> keeps the link behavior
        $(this.deckBuilderButton).button();

        this.pocketDiv = $("<div class='pocket'></div>");
        this.pocketDiv.css({width:95, height:18});
        this.controlsRight.append(this.pocketDiv);

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

        var panel = $("<div class='play-flow-panel' role='dialog' aria-modal='true' aria-label='Play'></div>");

        var header = $("<div class='play-flow-header'></div>");
        this.playBackButton = $("<button type='button' id='create-table-back-button' class='play-back-button'>&lt; Back</button>");
        this.playBackButton.click(function () {
            if (that.playFormPanel.is(":visible")) {
                that.showPlaySelection();
            } else {
                that.closePlayOverlay();
            }
        });
        header.append(this.playBackButton);
        header.append($("<button type='button' class='play-close-button' title='Close'>×</button>").click(function () {
            that.closePlayOverlay();
        }));
        panel.append(header);

        this.playSelectionPanel = $("<div id='create-table-selection' class='play-selection'></div>");
        this.playCasualChoice = $("<button type='button' id='create-unranked-table-button' class='play-choice-button'><span class='play-choice-title'>Open Casual Table</span><span class='play-subtitle'>A 1-on-1 game against another player.</span></button>");
        this.playAiChoice = $("<button type='button' id='create-bot-table-button' class='play-choice-button'><span class='play-choice-title'>Play Against AI</span><span class='play-subtitle'>A practice game against a computer opponent.</span></button>");
        this.playCasualChoice.click(function () { that.showPlayForm("casual"); });
        this.playAiChoice.click(function () { that.showPlayForm("ai"); });
        this.playSelectionPanel.append(this.playCasualChoice);
        this.playSelectionPanel.append(this.playAiChoice);
        // League / Tournament deferred (Slice later)
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
        panel.append(this.playFormPanel);

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

    syncJoinDecksFromCreateSelect:function() {
        if (this.joinDecksSelect == null || this.decksSelect == null) {
            return;
        }
        var prev = this.joinDecksSelect.val();
        var select = this.joinDecksSelect;
        select.empty();
        this.decksSelect.find("option").each(function () {
            var src = $(this);
            var opt = $("<option></option>");
            opt.attr("value", src.attr("value"));
            var sample = src.attr("data-sample-deck");
            if (sample != null)
                opt.attr("data-sample-deck", sample);
            var side = src.attr("data-side");
            if (side != null)
                opt.attr("data-side", side);
            opt.text(src.text());
            select.append(opt);
        });
        if (prev != null) {
            select.val(prev);
        }
    },

    openJoinTablePopup:function(tableId, formatName, playersStr) {
        this.openJoinPopup({
            kind: "table",
            id: tableId,
            formatName: formatName || "",
            contextLabel: this.buildJoinContextLabel(formatName, playersStr, null)
        });
    },

    openJoinQueuePopup:function(queueId, formatName, queueName) {
        this.openJoinPopup({
            kind: "queue",
            id: queueId,
            formatName: formatName || "",
            contextLabel: this.buildJoinContextLabel(formatName, null, queueName)
        });
    },

    buildJoinContextLabel:function(formatName, playersStr, queueName) {
        var parts = [];
        if (formatName)
            parts.push("Format: <b>" + $("<div>").text(formatName).html() + "</b>");
        if (playersStr)
            parts.push("Waiting: <b>" + $("<div>").text(playersStr).html() + "</b>");
        if (queueName)
            parts.push("Queue: <b>" + $("<div>").text(queueName).html() + "</b>");
        return parts.join(" · ");
    },

    openJoinPopup:function(pending) {
        if (this.joinOverlay == null) {
            return;
        }
        this.joinPending = pending;
        this.syncJoinDecksFromCreateSelect();
        this.joinResultDiv.hide().empty();

        var isQueue = pending.kind === "queue";
        this.joinTitleEl.text(isQueue ? "Join Queue" : "Join Table");
        var button = $(this.joinSubmitButton);
        var label = isQueue ? "Join queue" : "Join table";
        if (button.hasClass("ui-button")) {
            button.button("option", "label", label);
            button.button("enable");
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
            this.showJoinError("You must select a deck.");
            return;
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
    },

    showPlaySelection:function() {
        this.playMode = null;
        this.playFormPanel.hide();
        this.playSelectionPanel.show();
        this.playBackButton.html("&lt; Back");
        // AI choice visibility follows server flag
        if (this.aiTablesEnabled) {
            this.playAiChoice.show();
        } else {
            this.playAiChoice.hide();
        }
    },

    showPlayForm:function(mode) {
        this.playMode = mode;
        this.playSelectionPanel.hide();
        this.playFormPanel.show();

        if (mode === "ai") {
            this.playFormTitle.text("Play Against AI");
            this.opponentSelect.val("ai");
            // Private does not apply to bot games meaningfully; keep control but typically unused
        } else {
            this.playFormTitle.text("Open Casual Table");
            this.opponentSelect.val("human");
        }
        this.updateAiDecksForSelection();
        this.updateCreateTableLabel();
        // Ensure selects are visible inside the form once hall has loaded formats/decks
        if (this.supportedFormatsInitialized) {
            this.supportedFormatsSelect.css("display", "");
            this.decksSelect.css("display", "");
            this.createTableButton.css("display", "");
        }
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
        this.buttonsDiv.css({left:"0px", width:width + "px", align:"right", backgroundColor:"#000000", "border-top-width":"1px", "border-top-color":"#ffffff", "border-top-style":"solid"});
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
                that.showErrorDialog("Server connection error", "Unable to connect to server. Either server is down or there is a problem with your internet connection.", true, false);
            },
            "401":function() {
                that.showErrorDialog("Authentication error", "You are not logged in", false, true);
            },
            "409":function() {
                that.showErrorDialog("Concurrent access error", "You are accessing Game Hall from another browser or window. Close this window or if you wish to access Game Hall from here, click \"Refresh page\".", true, false);
            },
            "410":function() {
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
            this.syncJoinDecksFromCreateSelect();
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
            if (currency != this.pocketValue) {
                this.pocketValue = currency;
                this.pocketDiv.html(formatPrice(currency));
            }

            var privateGamesEnabled = root.getAttribute("privateGamesEnabledBoolean");
            if (privateGamesEnabled=="true") {
               this.isPrivateCheckbox.show();
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
            if (serverTime != null)
                $(".serverTime").text("Server time: " + serverTime);

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
