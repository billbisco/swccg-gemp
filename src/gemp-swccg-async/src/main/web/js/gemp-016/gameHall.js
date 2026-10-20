var chat;
var hall;

// The hall's top-level tabs (and every nested tab set loaded into them).
//  - A remote tab (Help, Events, Server Info, My Account, Admin) is fetched on its first visit only.  jQuery UI would
//    otherwise re-fetch it on every visit, re-running its script: new UI objects, lost state, leaked dialogs.
//  - Which tab opens is decided by GempLinks (js/gemp-016/hallLinks.js): a link's #fragment first, else the tab the
//    player last had open, else the Game Hall.
// Ported from PlayersCouncil/gemp-lotr src/Hall/GameHall.js.
var GempHallTabs = {
    // jQuery UI tabs beforeLoad handler: cancel the request when the tab's content is already there.
    loadOnce: function (event, ui) {
        if (ui.tab.data("loaded")) {
            event.preventDefault();
            return;
        }
        ui.jqXHR.done(function () {
            ui.tab.data("loaded", true);
        });
    },

    // ---- refresh on return ----
    // A tab's page is loaded once, so the data it shows would never change.  Each page instead registers the parts
    // that show live data:
    //
    //   GempHallTabs.onReturn($element, function () { ...re-fetch and re-render in place... });
    //
    // refresh() runs when the viewer comes back to $element (it becomes visible again because they switch to its tab
    // or sub-tab, or return to the browser tab / window while it is showing) after having been away from it for at
    // least REFRESH_AFTER_MS.  An element that is hidden when registered counts as left at that moment (its data was
    // just fetched), so its first showing refreshes only if that was long enough ago.  refresh() re-fetches DATA and
    // updates the page in place; it must keep what the viewer has open, typed or scrolled to.
    REFRESH_AFTER_MS: 10000,
    returnHandlers: [],
    pageHidden: false,     // the browser tab is hidden
    pageBlurred: false,    // the window lost focus (e.g. to a game window)
    watching: false,

    onReturn: function (element, refresh) {
        var node = element && element.jquery ? element[0] : element;
        if (node == null || typeof refresh != "function")
            return null;
        this.watch();
        var shown = this.isShown(node);
        var handler = {node: node, refresh: refresh, shown: shown, leftAt: shown ? null : this.now()};
        this.returnHandlers.push(handler);
        return handler;
    },

    now: function () {
        return Date.now();
    },

    // Visible as far as the tabs and the page are concerned: attached, no ancestor hidden by a tabs widget (or by
    // anything else with an inline display: none / the hidden attribute), and the page itself in front of the viewer.
    isShown: function (node) {
        if (this.pageHidden || this.pageBlurred)
            return false;
        if (!document.documentElement.contains(node))
            return false;
        for (var n = node; n != null && n.nodeType === 1; n = n.parentElement) {
            if ((n.style && n.style.display === "none") || n.hasAttribute("hidden")
                    || ($(n).hasClass("ui-tabs-panel") && n.getAttribute("aria-hidden") === "true"))
                return false;
        }
        return true;
    },

    // Re-evaluates every registered element; those that have just come back after long enough are refreshed.
    checkReturns: function () {
        var now = this.now();
        for (var i = 0; i < this.returnHandlers.length; i++) {
            var handler = this.returnHandlers[i];
            var shown = this.isShown(handler.node);
            if (handler.shown && !shown) {
                handler.leftAt = now;
            } else if (!handler.shown && shown && handler.leftAt != null && now - handler.leftAt >= this.REFRESH_AFTER_MS) {
                try {
                    handler.refresh();
                } catch (e) {
                    if (window.console)
                        console.error("Refreshing a hall tab failed", e);
                }
            }
            handler.shown = shown;
        }
    },

    // Tab switches of any tabs widget (nested ones too) bubble up as "tabsactivate"; the check runs once the widget
    // has finished updating its panels' state.  Hiding / showing the browser tab and the window losing / regaining
    // focus count as leaving / returning as well.
    watch: function () {
        if (this.watching)
            return;
        this.watching = true;
        var that = this;
        this.pageHidden = document.visibilityState === "hidden";
        var later = function () {
            setTimeout(function () { that.checkReturns(); }, 0);
        };
        $(document).on("tabsactivate.gempReturn", later);
        $(document).on("visibilitychange.gempReturn", function () {
            that.pageHidden = document.visibilityState === "hidden";
            that.checkReturns();
        });
        $(window).on("blur.gempReturn", function () {
            that.pageBlurred = true;
            that.checkReturns();
        });
        $(window).on("focus.gempReturn", function () {
            that.pageBlurred = false;
            that.checkReturns();
        });
    }
};

// Every tabs widget on the page (the hall's and those in the pages loaded into it) loads its remote tabs once, unless
// it passes a beforeLoad of its own.
$.ui.tabs.prototype.options.beforeLoad = GempHallTabs.loadOnce;

$(document).keydown(function (e) {
    if (e.which === 8 && !$(e.target).is("input, textarea")) {
        e.preventDefault();
    }
});

$(document).ready(function () {
    // The Admin tab stays hidden until the hall's player info shows an admin (hallUi.js); a link to it, or a
    // remembered Admin tab, is opened by GempLinks once it appears.
    $("#admin-tab").hide();

    $("#main").tabs({
        active: GempLinks.initialTabIndex()
    });

    chat = new ChatBoxUI("Game Hall", $("#chat"), "/gemp-swccg-server", true, null, false, true, [], true);
    chat.showTimestamps = true;
    chat.beginGameChat();

    hall = new GempSwccgHallUI($("#hall"), "/gemp-swccg-server", chat);

    // Hall links (hallLinks.js): #game-hall, #events/calendar, #account/history, #league-<type>, ... open their tab;
    // switching tabs by hand writes that tab's link into the address bar.
    GempLinks.start();

    var infoDialog = $("<div></div>")
        .dialog({
            autoOpen: false,
            closeOnEscape: true,
            resizable: false,
            title: "Card information"
        });

    $("body").click(
        function (event) {
            var tar = $(event.target).closest(".cardHint, .prizeHint");

            if (tar.hasClass("cardHint")) {
                var blueprintId = tar[0].getAttribute("value");
                var testingText = tar.attr("data-testingText");
                var backSideTestingText = tar.attr("data-backSideTestingText");
                var card = new Card(blueprintId, testingText, backSideTestingText, "SPECIAL", "hint", "");

                infoDialog.html("");
                infoDialog.html("<div style='scroll: auto'></div>");
                var floatCardDiv = $("<div style='float: left;'></div>");
                floatCardDiv.append(Card.CreateFullCardDiv(card.imageUrl, card.testingText, card.backSideTestingText, card.foil, card.horizontal, false));
                infoDialog.append(floatCardDiv);

                var windowWidth = $(window).width();
                var windowHeight = $(window).height();

                var horSpace = 30;
                var vertSpace = 45;

                infoDialog.dialog({ title: "Card information" });
                if (card.horizontal) {
                    // 500x360
                    infoDialog.dialog({ width: Math.min(500 + horSpace, windowWidth), height: Math.min(360 + vertSpace, windowHeight) });
                } else {
                    // 360x500
                    infoDialog.dialog({ width: Math.min(360 + horSpace, windowWidth), height: Math.min(500 + vertSpace, windowHeight) });
                }
                infoDialog.dialog("open");

                event.stopPropagation();
                return false;
            } else if (tar.hasClass("prizeHint")) {
                var prizeDescription = tar[0].getAttribute("value");

                infoDialog.text(prizeDescription);

                infoDialog.dialog({ title: "Prizes details", width: 300, height: 150 });
                infoDialog.dialog("open");

                event.stopPropagation();
                return false;
            }

            return true;
        }); /* body click */
}); /* document ready */

/*
 * Function to expand, or contract, formats on the "Format Rules" page.
 */
function showFormat(t) {
    var contentId = $(t).attr('id') + "-content";
    $("#" + contentId).slideToggle();
} /* showFormat */

/*
 * Function to change the arrow before the label: ▼ ▶
 */
function toggleLabelArrow(t) {
    $(t).toggleClass("format-name-label-downarrow");
} /* showFormat */
