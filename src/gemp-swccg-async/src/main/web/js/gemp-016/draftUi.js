var GempSwccgDraftUI = Class.extend({
    DIALOG_HORIZONTAL_SPACE: 30,
    DIALOG_VERTICAL_SPACE: 45,

    comm:null,
    tournamentId:null,
    collectionCode:null,
    channelNumber:null,
    updating:false,

    topDiv:null,
    bottomDiv:null,
    messageDiv:null,
    picksDiv:null,
    draftedDiv:null,
    completionScreen:null,
    picksCardGroup:null,
    draftedCardGroup:null,
    autoZoom:null,
    infoDialog:null,
    rightClickListenerAdded:false,

    init:function (url) {
        var that = this;
        if (typeof Card !== "undefined" && Card.applyFoilPresentation)
            Card.applyFoilPresentation();

        this.comm = new GempSwccgCommunication(url, function () {});
        this.tournamentId = getUrlParam("tournamentId");
        this.collectionCode = getUrlParam("collection");

        this.topDiv = $("#topDiv");
        this.bottomDiv = $("#bottomDiv");
        this.messageDiv = $("#messageDiv");
        this.picksDiv = $("#picksDiv");
        this.draftedDiv = $("#draftedDiv");
        this.completionScreen = $("#completionScreen");

        this.picksCardGroup = new NormalCardGroup(this.picksDiv, function () { return true; });
        this.picksCardGroup.maxCardHeight = 200;
        this.draftedCardGroup = new NormalCardGroup(this.draftedDiv, function () { return true; });
        this.draftedCardGroup.maxCardHeight = 200;
        this.autoZoom = new AutoZoom("autoZoomInSoloDraft");

        $("body").click(function (event) { return that.clickCardFunction(event); });
        if (!this.rightClickListenerAdded) {
            $("body")[0].addEventListener("contextmenu", function (event) {
                if (!that.clickCardFunction(event)) {
                    event.preventDefault();
                    return false;
                }
                return true;
            });
            this.rightClickListenerAdded = true;
        }

        this.infoDialog = $("<div></div>").dialog({
            autoOpen:false,
            closeOnEscape:true,
            resizable:false,
            title:"Card information"
        });

        var href = "deckBuild.html";
        if (this.collectionCode)
            href += "?collection=" + encodeURIComponent(this.collectionCode) + "&product=" + encodeURIComponent("all");
        $("#deckbuilderButton").attr("href", href);

        this.signup();
    },

    layoutUI:function (dontUseCachedLayouts) {
        this.picksCardGroup.layoutCards();
        this.draftedCardGroup.layoutCards();
    },

    signup:function () {
        var that = this;
        if (!this.tournamentId) {
            this.messageDiv.text("Missing tournament.");
            return;
        }
        this.comm.getHallDraft(this.tournamentId, function (xml, textStatus, xhr) {
            if (xhr && xhr.status == 204) {
                that.showFinished();
                return;
            }
            that.renderDraft(xml);
            that.poll();
        }, {
            "204": function () { that.showFinished(); },
            "0": function () { that.messageDiv.text("Could not load draft."); }
        });
    },

    poll:function () {
        var that = this;
        if (this.channelNumber == null || this.updating)
            return;
        this.updating = true;
        this.comm.updateHallDraft(this.tournamentId, this.channelNumber, function (xml, textStatus, xhr) {
            that.updating = false;
            if (xhr && xhr.status == 204) {
                that.showFinished();
                return;
            }
            that.renderDraft(xml);
            that.poll();
        }, {
            "204": function () { that.updating = false; that.showFinished(); },
            "409": function () { that.updating = false; that.signup(); },
            "410": function () { that.updating = false; that.signup(); },
            "0": function () {
                that.updating = false;
                setTimeout(function () { that.poll(); }, 2000);
            }
        });
    },

    renderDraft:function (xml) {
        if (xml == null || xml.documentElement == null)
            return;
        var root = xml.documentElement;
        if (root.tagName != "draft")
            return;
        this.channelNumber = root.getAttribute("channelNumber");
        var timeLeft = parseInt(root.getAttribute("timeLeft"), 10);
        this.picksDiv.empty();
        this.draftedDiv.empty();
        var picks = root.getElementsByTagName("pick");
        for (var i = 0; i < picks.length; i++) {
            var blueprintId = picks[i].getAttribute("blueprintId");
            var card = new Card(blueprintId, null, null, false, "picks", "deck", "player");
            var cardDiv = Card.CreateCardDiv(card.imageUrl, null, null, card.isFoil(), false, false, card.incomplete);
            cardDiv.data("card", card);
            cardDiv.data("blueprintId", blueprintId);
            this.picksDiv.append(cardDiv);
        }
        var cards = root.getElementsByTagName("card");
        for (var j = 0; j < cards.length; j++) {
            var draftedId = cards[j].getAttribute("blueprintId");
            var count = parseInt(cards[j].getAttribute("count"), 10) || 1;
            for (var n = 0; n < count; n++) {
                var drafted = new Card(draftedId, null, null, false, "drafted", "deck", "player");
                var draftedDiv = Card.CreateCardDiv(drafted.imageUrl, null, null, drafted.isFoil(), false, false, drafted.incomplete);
                draftedDiv.data("card", drafted);
                this.draftedDiv.append(draftedDiv);
            }
        }
        if (picks.length > 0) {
            var seconds = isNaN(timeLeft) ? 0 : Math.max(0, Math.round(timeLeft / 1000));
            this.messageDiv.text("Pick a card" + (seconds > 0 ? " (" + seconds + "s)" : "") + ".");
        } else {
            this.messageDiv.text("Waiting for the next pack.");
        }
        this.layoutUI(true);
    },

    showFinished:function () {
        this.messageDiv.text("Drafting is finished.");
        this.completionScreen.show();
    },

    clickCardFunction:function (event) {
        var tar = $(event.target);
        if (tar.length == 1 && tar[0].tagName == "A")
            return true;
        if (this.infoDialog != null && this.infoDialog.dialog("isOpen")) {
            this.infoDialog.dialog("close");
            event.stopPropagation();
            return false;
        }
        if (!tar.hasClass("actionArea"))
            return true;
        var selectedCardElem = tar.closest(".card");
        if (event.shiftKey || event.which > 1) {
            var card = selectedCardElem.data("card");
            if (card != null)
                this.infoDialog.html("").append($("<div></div>").append(Card.CreateFullCardDiv(card.imageUrl, null, card.isFoil(), card.horizontal, false))).dialog("open");
            return false;
        }
        var blueprintId = selectedCardElem.data("blueprintId");
        if (blueprintId == null)
            return false;
        var that = this;
        this.comm.pickHallDraft(this.tournamentId, blueprintId, function () {
            that.messageDiv.text("Waiting for other players.");
        }, {
            "204": function () { that.showFinished(); }
        });
        return false;
    }
});
