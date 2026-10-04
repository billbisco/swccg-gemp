/**
 * PlayerPicker: choose ONE player from hall users or by typing to search registered players.
 * Port of LOTR GEMP playerPicker.js to jQuery 1.6.2 + jQuery UI 1.8.16 (Class.extend, .bind, no ES6).
 */
var PlayerPicker = Class.extend({
    input:null,
    toggle:null,
    wrapper:null,
    options:null,
    online:null,
    pinned:null,
    selfName:"",
    cache:null,
    wasOpen:false,

    init:function (input, options) {
        var that = this;
        this.input = $(input);
        this.options = options || {};
        this.cache = {};
        this.online = [];
        this.pinned = [];
        this.selfName = "";
        this.wasOpen = false;

        this.input.attr({autocomplete:"off", role:"combobox", "aria-autocomplete":"list"});
        this.input.addClass("player-picker-input");
        this.wrapper = $("<span class='player-picker'></span>");
        this.input.after(this.wrapper);
        this.wrapper.append(this.input);
        this.toggle = $("<button type='button' class='player-picker-toggle' tabindex='-1'></button>")
            .attr({"aria-label":"Show the players in the hall", title:"Show the players in the hall"})
            .text("▾");
        this.wrapper.append(this.toggle);

        this.input.autocomplete({
            minLength:0,
            delay:this.options.delay != null ? this.options.delay : 250,
            autoFocus:false,
            position:{my:"left top", at:"left bottom", collision:"flipfit"},
            source:function (request, response) {
                that.suggest(request.term, response);
            },
            search:function () {
                var host = that.options.appendTo;
                if (!host) {
                    var front = that.input.closest("#create-table-popup, .play-flow-panel, .ui-dialog");
                    host = front.length ? front[0] : document.body;
                }
                that.input.autocomplete("option", "appendTo", host);
            },
            focus:function () {
                return false;
            },
            select:function (event, ui) {
                if (ui.item && ui.item.value != null && ui.item.value !== "") {
                    that.input.val(ui.item.value);
                    that.changed();
                }
                return false;
            }
        });
        this.input.autocomplete("widget").addClass("player-picker-menu");
        var widget = this.input.data("autocomplete");
        if (widget) {
            widget._renderMenu = function (ul, items) {
                var self = this;
                var heading = null;
                $.each(items, function (index, item) {
                    if (item.heading != null && item.heading !== heading) {
                        heading = item.heading;
                        $("<li class='player-picker-heading ui-menu-item' role='presentation'></li>").text(heading).appendTo(ul);
                    }
                    if (item.note) {
                        $("<li class='player-picker-heading player-picker-note ui-menu-item' role='presentation'></li>").text(item.note).appendTo(ul);
                        return;
                    }
                    self._renderItem(ul, item);
                });
            };
            widget._renderItem = function (ul, item) {
                return $("<li></li>")
                    .data("item.autocomplete", item)
                    .append($("<a></a>").text(item.label))
                    .appendTo(ul);
            };
        }

        this.toggle.bind("mousedown", function () {
            that.wasOpen = that.input.autocomplete("widget").is(":visible");
        }).bind("click", function (event) {
            event.preventDefault();
            if (that.input.prop("disabled"))
                return;
            if (that.wasOpen) {
                that.wasOpen = false;
                that.input.autocomplete("close");
                return;
            }
            that.openList();
        });

        this.input.bind("click", function () {
            if (!that.input.prop("disabled") && $.trim(that.input.val()) === "" && !that.input.autocomplete("widget").is(":visible"))
                that.openList();
        });
        this.input.bind("keydown", function (event) {
            if (event.which == 40 && !that.input.autocomplete("widget").is(":visible") && (event.altKey || $.trim(that.input.val()) === "")) {
                event.preventDefault();
                that.openList();
            }
        });
        this.input.bind("change", function () {
            that.changed();
        });
    },

    setPlayers:function (names) {
        var seen = {};
        var list = [];
        var i;
        names = names || [];
        for (i = 0; i < names.length; i++) {
            var name = String(names[i] == null ? "" : names[i]).replace(/^\s+|\s+$/g, "");
            if (name === "")
                continue;
            var key = name.toLowerCase();
            if (seen[key])
                continue;
            seen[key] = true;
            list.push(name);
        }
        list.sort(PlayerPicker.compareNames);
        this.online = list;
    },

    setSelf:function (name) {
        this.selfName = String(name == null ? "" : name).replace(/^\s+|\s+$/g, "");
    },

    val:function (name) {
        if (arguments.length == 0)
            return $.trim(this.input.val());
        name = String(name == null ? "" : name).replace(/^\s+|\s+$/g, "");
        this.input.val(name);
        if (name !== "" && !PlayerPicker.contains(this.online, name) && !PlayerPicker.contains(this.pinned, name))
            this.pinned.push(name);
        return this;
    },

    enable:function (enabled) {
        this.input.prop("disabled", !enabled);
        this.toggle.prop("disabled", !enabled);
        if (!enabled)
            this.input.autocomplete("close");
        return this;
    },

    openList:function () {
        this.input.trigger("focus");
        this.input.autocomplete("search", "");
    },

    isSelf:function (name) {
        return this.selfName !== "" && String(name).toLowerCase() === this.selfName.toLowerCase();
    },

    suggest:function (term, response) {
        var that = this;
        term = $.trim(term || "");
        if (typeof this.options.players == "function")
            this.setPlayers(this.options.players());
        if (typeof this.options.self == "function")
            this.setSelf(this.options.self());
        var items = [];
        var inHall = PlayerPicker.rank(this.online, term);
        var filteredHall = [];
        var i;
        for (i = 0; i < inHall.length; i++) {
            if (!that.isSelf(inHall[i]))
                filteredHall.push(inHall[i]);
        }
        for (i = 0; i < filteredHall.length; i++)
            items.push({label:filteredHall[i], value:filteredHall[i], heading:"In the hall"});

        var others = function (names) {
            var list = [];
            var n;
            names = names || [];
            for (n = 0; n < names.length; n++) {
                var name = names[n];
                if (name == null || that.isSelf(name) || PlayerPicker.contains(filteredHall, name) || PlayerPicker.contains(list, name))
                    continue;
                list.push(String(name));
            }
            return list;
        };

        if (term === "") {
            var pinnedEmpty = others(this.pinned);
            for (i = 0; i < pinnedEmpty.length; i++)
                items.push({label:pinnedEmpty[i], value:pinnedEmpty[i], heading:"Other players"});
            if (items.length == 0)
                items.push({note:"Nobody else is in the hall. Type a name to search all players."});
            response(items);
            return;
        }

        var search = this.options.search;
        if (term.length < PlayerPicker.MIN_SEARCH || typeof search != "function") {
            var pinnedHits = others(PlayerPicker.rank(this.pinned, term));
            for (i = 0; i < pinnedHits.length; i++)
                items.push({label:pinnedHits[i], value:pinnedHits[i], heading:"Other players"});
            response(items);
            return;
        }

        var key = term.toLowerCase();
        var finish = function (names) {
            var extra = others(names || []);
            var x;
            for (x = 0; x < extra.length; x++)
                items.push({label:extra[x], value:extra[x], heading:"Other players"});
            if (items.length == 0)
                items.push({note:"No player's name starts with \"" + term + "\"."});
            response(items);
        };
        if (this.cache.hasOwnProperty(key)) {
            finish(this.cache[key]);
            return;
        }
        search(term, PlayerPicker.LIMIT, function (names) {
            that.cache[key] = $.isArray(names) ? names.slice(0, PlayerPicker.LIMIT) : [];
            finish(that.cache[key]);
        }, function () {
            response(items);
        });
    },

    changed:function () {
        if (typeof this.options.onChange == "function")
            this.options.onChange(this.val());
    }
});

PlayerPicker.MIN_SEARCH = 2;
PlayerPicker.LIMIT = 10;

PlayerPicker.compareNames = function (a, b) {
    return a.toLowerCase().localeCompare(b.toLowerCase());
};

PlayerPicker.contains = function (list, name) {
    var lower = String(name).toLowerCase();
    var i;
    list = list || [];
    for (i = 0; i < list.length; i++) {
        if (String(list[i]).toLowerCase() === lower)
            return true;
    }
    return false;
};

PlayerPicker.rank = function (names, term) {
    var q = String(term || "").toLowerCase();
    names = names || [];
    if (q === "") {
        var copy = names.slice();
        copy.sort(PlayerPicker.compareNames);
        return copy;
    }
    var hits = [];
    var i;
    for (i = 0; i < names.length; i++) {
        var name = names[i];
        var folded = String(name).toLowerCase();
        var rank = folded === q ? 0 : folded.indexOf(q) === 0 ? 1 : folded.indexOf(q) >= 0 ? 2 : -1;
        if (rank >= 0)
            hits.push({name:name, rank:rank});
    }
    hits.sort(function (a, b) {
        if (a.rank !== b.rank)
            return a.rank - b.rank;
        return PlayerPicker.compareNames(a.name, b.name);
    });
    var out = [];
    for (i = 0; i < hits.length; i++)
        out.push(hits[i].name);
    return out;
};
