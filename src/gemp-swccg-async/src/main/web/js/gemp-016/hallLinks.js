/**
 * Hall links: the one place that decides which hall tab (and sub-tab) is showing, so that every screen of the hall
 * has a link of its own and the other pages (deck builder, game pages) can link straight into it.
 * Adapted from the LOTR GEMP hall router for this hall's jQuery UI 1.13 tabs.
 *
 *   #game-hall, #events, #history, #formats, #your-stats, #server-stats, #collection-stats, #admin    a tab
 *   #events/calendar, #events/leagues, #events/tournaments                                          an Events sub-tab
 *   #league-<type>                      Events > Leagues, with that league's details open
 *   #tournament-<id>                    Events > Tournaments, with that tournament's details open
 *   #history?opponent=..&format=..      History, filtered (keys: format, opponent, event, side, result, from, to, page)
 *
 * On load the link wins; without one the tab the player last had open (localStorage) comes back, else the Game Hall.
 * A link followed later (hashchange) is handled the same way.  Switching tabs by hand writes that tab's link into the
 * address bar with history.replaceState (no reload, no history entry).  A page that shows something more specific (a
 * league, a filtered history) writes its own link with GempLinks.setLink().
 *
 * Tabs are found by the id of their <li> ("tab-" + slug, or the tab's li); a remote tab's panel by the li's
 * aria-controls.
 */
var GempLinks = {
    STORAGE_KEY: "gemp.swccg.hall.tab",

    // login is not handled here: every hall tab needs a logged-in player, as before.
    TABS: [
        {slug: "game-hall", aliases: ["hall", "gameHall"]},
        {slug: "events", panel: "#eventsMain", subs: [
            {slug: "calendar", href: "#events-calendar"},
            {slug: "leagues", href: "#events-leagues"},
            {slug: "tournaments", href: "#events-tournaments"}
        ]},
        {slug: "history", aliases: ["game-history", "my-history"]},
        {slug: "formats", aliases: ["format-rules"]},
        {slug: "your-stats"},
        {slug: "server-stats"},
        {slug: "collection-stats"},
        {slug: "admin", li: "#admin-tab"}
    ],

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
        {pattern: /^#history\?(.*)$/, tab: "history", sub: null, run: function (m) {
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

    // {tab, sub, run, match, hash} for a link this router knows, else null.
    parse: function (hash) {
        hash = String(hash == null ? "" : hash);
        if (hash.charAt(0) !== "#")
            hash = "#" + hash;
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

    // Called once the hall's tab set exists.
    start: function () {
        var that = this;
        if (this.started)
            return;
        this.started = true;

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

        var route = this.parse(window.location.hash);
        if (route == null) {
            var saved = this.readSaved();
            if (saved != null && this.tab(saved) != null)
                route = this.parse(this.link(saved));
        }
        if (route != null)
            this.go(route.hash, true);
        else
            this.scheduleSync();
    },

    isTabSet: function (element) {
        if (element == null)
            return false;
        if (element.id === "tabs")
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
        var main = $("#tabs");
        if (!this.isTabs(main))
            return false;
        var tab = this.tab(route.tab);
        var li = this.topItem(tab);
        if (!this.visible(li))
            return false;          // hidden for now (Admin before the player info)
        var index = main.find("> ul > li").index(li);
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
        var subLi = nested.find("> ul > li").filter(function () {
            return $(this).find("a").attr("href") === sub.href;
        }).first();
        if (!subLi.length)
            return true;
        var subIndex = nested.find("> ul > li").index(subLi);
        if (nested.tabs("option", "active") !== subIndex)
            this.select(nested, subIndex);
        this.schedule(route, token);
        return true;
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
        var main = $("#tabs");
        if (!this.isTabs(main))
            return null;
        var li = main.find("> ul > li").eq(main.tabs("option", "active"));
        var tab = this.tabByItemId(li.attr("id") || "");
        if (tab == null)
            return null;
        var sub = null;
        var nested = tab.panel ? $(tab.panel) : $();
        if (this.isTabs(nested)) {
            var href = nested.find("> ul > li").eq(nested.tabs("option", "active")).find("a").attr("href");
            for (var i = 0; i < tab.subs.length; i++) {
                if (tab.subs[i].href === href)
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
