/**
 * Hall links: the one place that decides which hall tab (and sub-tab) is showing, so that every screen of the hall
 * has a link of its own and the other pages (deck builder, game pages) can link straight into it.
 * Adapted from the LOTR GEMP hall router for this hall's jQuery UI 1.13 tabs.
 *
 *   #game-hall, #help, #events, #info, #account, #admin                                      a top tab
 *   #help/formats, #events/calendar, #info/stats, #account/history, ...                      a sub-tab
 *   #league-<type>                      Events > Current Leagues, with that league's details open
 *   #tournament-<id>                    Events > Current Tournaments, with that tournament's details open
 *   #account/history?opponent=..        My Game History, filtered (keys: format, opponent, event, side, result,
 *                                       from, to, page)
 *
 * The links the hall had before the shell was rebuilt still work: #history, #your-stats, #collection-stats,
 * #server-stats and #formats are rewritten to their new homes (OLD_LINKS below), so links in the deck builder, in
 * event pages and in players' bookmarks keep opening the right page.
 *
 * On load the link wins; without one the tab the player last had open (localStorage) comes back, else the Game Hall.
 * A link followed later (hashchange) is handled the same way.  Switching tabs by hand writes that tab's link into the
 * address bar with history.replaceState (no reload, no history entry).  A page that shows something more specific (a
 * league, a filtered history) writes its own link with GempLinks.setLink().
 *
 * Tabs are found by the id of their <li> ("tab-" + slug, or the tab's li); a sub-tab by a distinctive piece of its
 * anchor's href (its "match" strings), so a page can move its sub-pages around without the router following along.
 */
var GempLinks = {
    STORAGE_KEY: "gemp.swccg.hall.tab",

    // login is not handled here: every hall tab needs a logged-in player, as before.
    TABS: [
        {slug: "game-hall", aliases: ["hall", "gameHall"]},
        {slug: "help", panel: "#helpMain", subs: [
            {slug: "howtoplay", match: ["#help-howtoplay"]},
            {slug: "manual", match: ["instruction.html"]},
            {slug: "formats", match: ["formats/html"]},
            {slug: "leagues", match: ["leagueRules"]}
        ]},
        {slug: "events", panel: "#eventsMain", subs: [
            {slug: "calendar", match: ["#events-calendar"]},
            {slug: "leagues", match: ["#events-leagues"]},
            {slug: "tournaments", match: ["#events-tournaments"]}
        ]},
        {slug: "info", aliases: ["server-info"], panel: "#infoMain", subs: [
            {slug: "stats", match: ["stats.html"]},
            {slug: "changelog", match: ["changeLog"]},
            {slug: "contribute", match: ["contribute"]},
            {slug: "about", match: ["#info-about"]}
        ]},
        {slug: "account", aliases: ["my-account"], panel: "#accountMain", subs: [
            {slug: "overview", match: ["#account-overview"]},
            {slug: "history", match: ["history.html"]},
            {slug: "stats", match: ["yourStats"]},
            {slug: "collection", match: ["yourCollectionStats"]}
        ]},
        {slug: "admin", li: "#admin-tab", panel: "#adminMain", subs: [
            {slug: "general", match: ["generalAdmin"]},
            {slug: "users", match: ["userAdmin"]},
            {slug: "league", match: ["leagueAdmin"]},
            {slug: "playtesting", match: ["playtestingAdmin"]},
            {slug: "commentator", match: ["commentatorAdmin"]}
        ]}
    ],

    // Links the hall used before the shell was rebuilt, and where they live now.  A query string is kept.
    OLD_LINKS: {
        "history": "#account/history",
        "game-history": "#account/history",
        "my-history": "#account/history",
        "your-stats": "#account/stats",
        "collection-stats": "#account/collection",
        "server-stats": "#info/stats",
        "formats": "#help/formats",
        "format-rules": "#help/formats"
    },

    // Links to something inside a page; run(match, hash) is called once that page (and sub-tab) is showing.
    ROUTES: [
        {pattern: /^#league-([A-Za-z0-9_.-]+)$/, tab: "events", sub: "leagues", run: function (m) {
            if (window.GempEventsPage)
                GempEventsPage.showLeague(m[1]);
        }},
        {pattern: /^#tournament-([A-Za-z0-9_.-]+)$/, tab: "events", sub: "tournaments", run: function (m) {
            if (window.GempEventsPage)
                GempEventsPage.showTournament(m[1]);
        }},
        {pattern: /^#(?:account\/history|history)\?(.*)$/, tab: "account", sub: "history", run: function (m) {
            if (window.GempHistoryPage)
                GempHistoryPage.applyLink(GempLinks.parseQuery(m[1]));
        }}
    ],

    POLL_MS: 50,
    MAX_TRIES: 600,        // 30 s: a tab still hidden (Admin before the player info) or still loading by then is given up on

    started: false,
    pending: null,
    token: 0,
    timer: null,
    switching: false,
    lastLinks: {},

    // ---- the tab table ----

    tab: function (slug) {
        for (var i = 0; i < this.TABS.length; i++) {
            var t = this.TABS[i];
            if (t.slug === slug || (t.aliases && $.inArray(slug, t.aliases) >= 0))
                return t;
        }
        return null;
    },

    sub: function (tab, slug) {
        if (tab == null || !tab.subs || slug == null)
            return null;
        for (var i = 0; i < tab.subs.length; i++) {
            if (tab.subs[i].slug === slug)
                return tab.subs[i];
        }
        return null;
    },

    topItem: function (tab) {
        return $(tab.li || "#tab-" + tab.slug);
    },

    tabByItemId: function (id) {
        for (var i = 0; i < this.TABS.length; i++) {
            if ((this.TABS[i].li || "#tab-" + this.TABS[i].slug) === "#" + id)
                return this.TABS[i];
        }
        return null;
    },

    visible: function (li) {
        return li.length > 0 && li.css("display") !== "none";
    },

    // ---- parsing ----

    parseQuery: function (query) {
        var result = {};
        if (!query)
            return result;
        var parts = String(query).split("&");
        for (var i = 0; i < parts.length; i++) {
            if (parts[i] === "")
                continue;
            var eq = parts[i].indexOf("=");
            var key = eq < 0 ? parts[i] : parts[i].substring(0, eq);
            var value = eq < 0 ? "" : parts[i].substring(eq + 1);
            try {
                result[decodeURIComponent(key)] = decodeURIComponent(value.replace(/\+/g, " "));
            } catch (e) {
                // a malformed escape: the key is skipped
            }
        }
        return result;
    },

    buildQuery: function (params) {
        var parts = [];
        for (var key in params) {
            if (params.hasOwnProperty(key) && params[key] != null && params[key] !== "")
                parts.push(encodeURIComponent(key) + "=" + encodeURIComponent(params[key]));
        }
        return parts.join("&");
    },

    // An old link ("#history", "#your-stats?x=1") rewritten to where that page lives now; the link itself otherwise.
    rewriteOldLink: function (hash) {
        var parts = /^#([A-Za-z-]+)(\?.*)?$/.exec(hash);
        if (parts == null)
            return hash;
        var moved = this.OLD_LINKS[parts[1]];
        return moved == null ? hash : moved + (parts[2] || "");
    },

    // {tab, sub, run, match, hash} for a link this router knows, else null.
    parse: function (hash) {
        hash = String(hash == null ? "" : hash);
        if (hash.charAt(0) !== "#")
            hash = "#" + hash;
        hash = this.rewriteOldLink(hash);
        for (var i = 0; i < this.ROUTES.length; i++) {
            var m = this.ROUTES[i].pattern.exec(hash);
            if (m)
                return {tab: this.ROUTES[i].tab, sub: this.ROUTES[i].sub, run: this.ROUTES[i].run, match: m, hash: hash};
        }
        var parts = /^#([A-Za-z-]+)(?:\/([A-Za-z0-9-]+))?\/?$/.exec(hash);
        if (!parts)
            return null;
        var tab = this.tab(parts[1]);
        if (tab == null)
            return null;
        var sub = this.sub(tab, parts[2]);
        return {tab: tab.slug, sub: sub ? sub.slug : null, run: null, match: null, hash: hash};
    },

    link: function (tab, sub) {
        return "#" + tab + (sub ? "/" + sub : "");
    },

    // A link to the hall from another page (deck builder, game page).
    hallUrl: function (hash) {
        return "/gemp-swccg/hall.html" + (hash || "");
    },

    // ---- start-up ----

    // The tab the hall should open on (its index in the tab bar), for $("#main").tabs({active: ...}): the one the
    // current link names, else the one the player last had open, else the Game Hall.  Calling it before the tabs are
    // built keeps the hall from loading the Game Hall first and then switching away from it.
    initialTabIndex: function () {
        var route = this.parse(window.location.hash);
        if (route == null) {
            var saved = this.readSaved();
            if (saved != null && this.tab(saved) != null)
                route = this.parse(this.link(saved));
        }
        this.initial = route;
        this.initialized = true;
        var tab = route != null ? this.tab(route.tab) : null;
        var li = tab != null ? this.topItem(tab) : $();
        return this.visible(li) ? li.index() : 0;
    },

    // Called once the hall's tab set exists.
    start: function () {
        var that = this;
        if (this.started)
            return;
        this.started = true;
        if (!this.initialized)
            this.initialTabIndex();

        $(window).bind("hashchange.gempLinks", function () {
            that.go(window.location.hash, true);
        });
        // A tab picked by hand while a link is still opening wins over the link.  (tabsbeforeactivate fires during the
        // click; a remote tab's page loads after that, long after the router's own switch.)
        $(document).bind("tabsbeforeactivate.gempLinks", function (event) {
            if (!that.switching && that.pending != null && that.isTabSet(event.target))
                that.cancel();
        });
        // A tab shown (top level or an Events sub-tab), by a click or by the router, updates the address bar; so does a
        // remote tab's page finishing loading or setting up its sub-tabs, since the link then names the sub-tab.
        $(document).bind("tabsactivate.gempLinks tabsload.gempLinks tabscreate.gempLinks", function (event) {
            if (that.isTabSet(event.target))
                that.scheduleSync();
        });

        if (this.initial != null)
            this.go(this.initial.hash, true);
        else
            this.scheduleSync();
    },

    isTabSet: function (element) {
        if (element == null)
            return false;
        if (element.id === "main")
            return true;
        for (var i = 0; i < this.TABS.length; i++) {
            if (this.TABS[i].panel && element.id === this.TABS[i].panel.substring(1))
                return true;
        }
        return false;
    },

    readSaved: function () {
        try {
            return window.localStorage ? window.localStorage.getItem(this.STORAGE_KEY) : null;
        } catch (e) {
            return null;
        }
    },

    save: function (slug) {
        try {
            if (window.localStorage)
                window.localStorage.setItem(this.STORAGE_KEY, slug);
        } catch (e) {
            // not remembered; nothing depends on it
        }
    },

    // ---- navigating ----

    // Opens what the link names.  False for a link this router does not know (nothing happens).
    go: function (hash, keepHash) {
        var route = this.parse(hash);
        if (route == null)
            return false;
        var that = this;
        var token = ++this.token;
        clearTimeout(this.timer);
        this.pending = route;
        if (!keepHash)
            this.replaceHash(route.hash);
        var tries = 0;
        var step = function () {
            clearTimeout(that.timer);
            if (token !== that.token)
                return;
            if (that.advance(route, token)) {
                that.pending = null;
                that.scheduleSync();
                return;
            }
            if (++tries < that.MAX_TRIES) {
                that.timer = setTimeout(step, that.POLL_MS);
            } else {
                that.pending = null;
                that.scheduleSync();
            }
        };
        step();
        return true;
    },

    // Opens a link from script, e.g. a link in the History table or an event's details.
    open: function (hash) {
        if (window.location.hash === hash)
            return this.go(hash, true);
        return this.go(hash, false);
    },

    cancel: function () {
        this.token++;
        clearTimeout(this.timer);
        this.pending = null;
    },

    // One step of opening a route: true once it is done (its handler scheduled), false to be called again.
    advance: function (route, token) {
        var main = $("#main");
        if (!this.isTabs(main))
            return false;
        var tab = this.tab(route.tab);
        var li = this.topItem(tab);
        if (!this.visible(li))
            return false;          // hidden for now (Admin before the player info)
        var index = this.items(main).index(li);
        if (main.tabs("option", "active") !== index) {
            this.select(main, index);
            return false;
        }
        var panel = $("#" + this.panelId(li));
        if (li.hasClass("ui-tabs-loading") || (this.isRemote(li) && panel.children().length === 0))
            return false;          // the tab's page is still loading

        var sub = this.sub(tab, route.sub);
        if (sub == null) {
            if (tab.panel && route.run == null && !$(tab.panel).length)
                return false;
            this.schedule(route, token);
            return true;
        }
        var nested = $(tab.panel);
        if (!this.isTabs(nested))
            return false;          // the page has not set up its sub-tabs yet
        var subLi = this.subItem(nested, sub);
        if (!subLi.length)
            return true;
        var subIndex = this.items(nested).index(subLi);
        if (nested.tabs("option", "active") !== subIndex)
            this.select(nested, subIndex);
        this.schedule(route, token);
        return true;
    },

    // The <li>s of a tabs widget.  jQuery UI takes the first ol / ul inside the element as its tab list, so it may
    // sit in a wrapper (the hall's #tabs, the admin page's #admin-tabs) rather than be a child of the widget.
    items: function (tabs) {
        return tabs.find("ol, ul").first().children("li");
    },

    // A sub-tab's <li>: the one whose anchor's href carries one of the sub-tab's "match" strings.
    subItem: function (nested, sub) {
        var matches = sub.match || [];
        return this.items(nested).filter(function () {
            var href = $(this).find("a").attr("href") || "";
            for (var i = 0; i < matches.length; i++) {
                if (href.indexOf(matches[i]) >= 0)
                    return true;
            }
            return false;
        }).first();
    },

    // A page whose sub-tabs appear late (Admin, which hides the tabs the player may not use) can ask the router to
    // try again now instead of waiting for the next poll.
    poke: function () {
        if (this.pending != null)
            this.go(this.pending.hash, true);
    },

    isTabs: function (element) {
        return element.length > 0 && !!element.data("ui-tabs");
    },

    // jQuery UI 1.13 links each tab's li to its panel (local or remote) with aria-controls.
    panelId: function (li) {
        return li.attr("aria-controls") || "";
    },

    isRemote: function (li) {
        return (li.find("a").attr("href") || "").charAt(0) !== "#";
    },

    select: function (tabs, index) {
        this.switching = true;
        try {
            tabs.tabs("option", "active", index);
        } finally {
            this.switching = false;
        }
    },

    // The handler runs after the scripts of the page just put in (their ready callbacks are queued the same way).
    schedule: function (route, token) {
        var that = this;
        if (typeof route.run != "function")
            return;
        setTimeout(function () {
            if (token !== that.token)
                return;
            try {
                route.run(route.match, route.hash);
            } catch (e) {
                if (window.console)
                    console.error("Opening " + route.hash + " failed", e);
            }
        }, 0);
    },

    // ---- the address bar ----

    replaceHash: function (hash) {
        if (window.location.hash === hash || !window.history || typeof window.history.replaceState != "function")
            return;
        try {
            window.history.replaceState(window.history.state, "", hash);
        } catch (ignored) {
            // a sandboxed page may refuse; the link is only a convenience
        }
    },

    // For a page showing something with a link of its own (a league's details, a filtered history).
    setLink: function (hash) {
        var route = this.parse(hash);
        if (route != null) {
            this.lastLinks[route.tab + "/" + (route.sub || "")] = hash;
            this.replaceHash(hash);
        }
    },

    // {tab, sub} of what is showing.
    current: function () {
        var main = $("#main");
        if (!this.isTabs(main))
            return null;
        var li = this.items(main).eq(main.tabs("option", "active"));
        var tab = this.tabByItemId(li.attr("id") || "");
        if (tab == null)
            return null;
        var sub = null;
        var nested = tab.panel ? $(tab.panel) : $();
        if (tab.subs && this.isTabs(nested)) {
            var activeLi = this.items(nested).eq(nested.tabs("option", "active"));
            for (var i = 0; i < tab.subs.length; i++) {
                if (activeLi.length && this.subItem(nested, tab.subs[i]).is(activeLi))
                    sub = tab.subs[i];
            }
        }
        return {tab: tab, sub: sub};
    },

    scheduleSync: function () {
        var that = this;
        clearTimeout(this.syncTimer);
        this.syncTimer = setTimeout(function () {
            that.sync();
        }, 0);
    },

    // Writes the showing tab's link into the address bar, unless the current link already points into that tab.
    sync: function () {
        if (!this.started || this.pending != null)
            return;
        var current = this.current();
        if (current == null)
            return;
        this.save(current.tab.slug);
        var subSlug = current.sub ? current.sub.slug : null;
        var route = this.parse(window.location.hash);
        if (route != null && route.tab === current.tab.slug && (route.sub || null) === subSlug)
            return;
        var remembered = this.lastLinks[current.tab.slug + "/" + (subSlug || "")];
        this.replaceHash(remembered || this.link(current.tab.slug, subSlug));
    }
};
