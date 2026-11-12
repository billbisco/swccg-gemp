// The player menu (swccg-guild/work#264, AC6; Bill 2026-11-11, t133u): click a player's name in the hall's user list,
// a hall table, the game's Players tab or game history to see the name, the account's Player ID with a Copy button,
// and the actions that already exist there. The id is never shown next to names; it is only in this menu. Guests
// show "Guest" and bots "Bot", with no id. Names go through playerNameText (chat.js, #249) and are set as text, so
// nothing from the server is pasted into HTML. ES5, no framework beyond jQuery.
var PlayerMenu = (function () {
    var SEATS = /^(light|dark)[ _]side$/i;
    var menu = null, anchorEl = null;

    function nameText(name) {
        if (typeof playerNameText === "function")
            return playerNameText(name);
        return name == null ? "" : String(name);
    }

    /** "bot", "guest" or "player" for a name the server did not describe (the same rule as PlayerCard.kind). */
    function kindOf(name) {
        var s = name == null ? "" : String(name);
        if (s.charAt(0) === "~")
            return "bot";
        if (/^guest-/.test(s) || SEATS.test(s.replace(/^\s+|\s+$/g, "")))
            return "guest";
        return "player";
    }

    /** {name, kind, playerId} from a chat <user> element (ChatRequestHandler); older servers send only the text. */
    function infoFromUser(user) {
        var text = user.childNodes.length ? user.childNodes[0].nodeValue : "";
        var name = user.getAttribute("name");
        if (name == null)
            name = String(text).replace(/^(?:&[a-zA-Z0-9#]+;|[*+]| )*/, "").replace(/\s+$/, "");
        var id = user.getAttribute("playerId");
        return {name: name, kind: user.getAttribute("kind") || kindOf(name), playerId: id && /^[0-9]+$/.test(id) ? id : null};
    }

    /** Copies text: the async Clipboard API on HTTPS, else a hidden textarea and execCommand. done(true|false). */
    function copyText(text, done) {
        done = done || function () {};
        function legacy() {
            var ok = false, ta = document.createElement("textarea");
            ta.value = text;
            ta.setAttribute("readonly", "");
            ta.style.position = "fixed";
            ta.style.top = "-1000px";
            ta.style.opacity = "0";
            document.body.appendChild(ta);
            try {
                ta.select();
                ta.setSelectionRange(0, text.length);
                ok = document.execCommand("copy");
            } catch (e) {
                ok = false;
            }
            document.body.removeChild(ta);
            return ok;
        }
        if (typeof navigator !== "undefined" && navigator.clipboard && navigator.clipboard.writeText && window.isSecureContext) {
            navigator.clipboard.writeText(text).then(function () { done(true); }, function () { done(legacy()); });
        } else {
            done(legacy());
        }
    }

    function selectText(el) {
        try {
            var range = document.createRange();
            range.selectNodeContents(el);
            var sel = window.getSelection();
            sel.removeAllRanges();
            sel.addRange(range);
        } catch (e) { /* old browser */ }
    }

    function close() {
        if (menu) {
            menu.remove();
            menu = null;
        }
        if (anchorEl) {
            $(anchorEl).attr("aria-expanded", "false");
            anchorEl = null;
        }
    }

    function idLine(info) {
        var line = $("<div class='player-menu-id'></div>");
        if (info.kind === "guest")
            return line.text("Guest");
        if (info.kind === "bot")
            return line.text("Bot");
        if (!info.playerId)
            return line.text("Player ID not available");
        var num = $("<span class='player-menu-num'></span>").text(info.playerId);
        var status = $("<span class='player-menu-copied' role='status' aria-live='polite'></span>");
        var button = $("<button type='button' class='player-menu-copy'>Copy</button>")
            .attr("aria-label", "Copy Player ID " + info.playerId)
            .bind("click", function (e) {
                e.stopPropagation();
                copyText(String(info.playerId), function (ok) {
                    if (ok) {
                        status.text("Copied");
                    } else {
                        selectText(num[0]);
                        status.text("Press Ctrl+C to copy");
                    }
                });
            });
        return line.append("Player ID ").append(num).append(" ").append(button).append(" ").append(status);
    }

    /**
     * Opens the menu next to anchor. info: {name, kind, playerId}. actions: [{label, run: function(info)} or
     * {label, href}], shown under the id line.
     */
    function open(anchor, info, actions) {
        var same = anchorEl === anchor;
        close();
        if (same)
            return null;   // a second click on the same name closes it
        anchorEl = anchor;
        $(anchor).attr("aria-expanded", "true");
        menu = $("<div class='player-menu' role='dialog'></div>").attr("aria-label", "Player " + nameText(info.name));
        menu.append($("<div class='player-menu-name'></div>").text(nameText(info.name)));
        menu.append(idLine(info));
        if (actions && actions.length) {
            var list = $("<div class='player-menu-actions'></div>");
            $.each(actions, function (i, a) {
                var item = a.href
                    ? $("<a class='player-menu-action'></a>").attr("href", a.href).text(a.label)
                    : $("<button type='button' class='player-menu-action'></button>").text(a.label);
                item.bind("click", function (e) {
                    if (a.run) {
                        e.preventDefault();
                        a.run(info);
                    }
                    close();
                });
                list.append(item);
            });
            menu.append(list);
        }
        menu.bind("click mousedown", function (e) { e.stopPropagation(); });
        $("body").append(menu);
        var r = anchor.getBoundingClientRect();
        var left = Math.max(4, Math.min(r.left + window.pageXOffset, window.pageXOffset + document.documentElement.clientWidth - menu.outerWidth() - 4));
        var top = r.bottom + window.pageYOffset + 2;
        if (r.bottom + menu.outerHeight() + 4 > document.documentElement.clientHeight)
            top = Math.max(window.pageYOffset + 4, r.top + window.pageYOffset - menu.outerHeight() - 2);
        menu.css({left: left + "px", top: top + "px"});
        var first = menu.find("button, a").first();
        if (first.length)
            first.focus();
        return menu;
    }

    /** Makes el (a name) open the menu on click, Enter or Space. getInfo() and getActions() are read on each open. */
    function bind(el, getInfo, getActions) {
        $(el).addClass("player-menu-target").attr({role: "button", tabindex: "0", "aria-haspopup": "dialog", "aria-expanded": "false"})
            .bind("click", function (e) {
                e.preventDefault();
                e.stopPropagation();
                open(this, getInfo(), getActions ? getActions() : []);
            })
            .bind("keydown", function (e) {
                if (e.key === "Enter" || e.key === " " || e.keyCode === 13 || e.keyCode === 32) {
                    e.preventDefault();
                    e.stopPropagation();
                    open(this, getInfo(), getActions ? getActions() : []);
                }
            });
        return el;
    }

    if (typeof document !== "undefined" && window.jQuery) {
        $(document).bind("mousedown", function (e) {
            if (anchorEl && (anchorEl === e.target || $.contains(anchorEl, e.target)))
                return;   // the name's own click toggles the menu
            close();
        });
        $(document).bind("keydown", function (e) {
            if (menu && (e.key === "Escape" || e.keyCode === 27)) {
                var a = anchorEl;
                close();
                if (a) a.focus();
            }
        });
        $(function () {
            if (document.getElementById("player-menu-style"))
                return;
            $("<style id='player-menu-style'></style>").text(
                ".player-menu-target{cursor:pointer}" +
                ".player-menu-target:hover,.player-menu-target:focus{text-decoration:underline}" +
                ".player-menu{position:absolute;z-index:10000;min-width:170px;max-width:280px;padding:6px 8px;background:#fff;" +
                "color:#000;border:1px solid #888;border-radius:4px;box-shadow:0 2px 8px rgba(0,0,0,.35);font-size:13px;text-align:left}" +
                ".player-menu-name{font-weight:bold;padding-bottom:4px;margin-bottom:4px;border-bottom:1px solid #ddd;overflow-wrap:anywhere}" +
                ".player-menu-id{padding:2px 0;white-space:nowrap}.player-menu-num{font-family:monospace;user-select:all}" +
                ".player-menu-copy{font-size:12px;padding:0 6px;cursor:pointer}.player-menu-copied{color:#2a7a2a;font-size:12px}" +
                ".player-menu-actions{margin-top:4px;border-top:1px solid #ddd;padding-top:4px}" +
                ".player-menu-action{display:block;width:100%;text-align:left;background:none;border:0;padding:3px 0;color:#00e;" +
                "cursor:pointer;font:inherit;text-decoration:none}.player-menu-action:hover{text-decoration:underline}"
            ).appendTo("head");
        });
    }

    var api = {open: open, close: close, bind: bind, copyText: copyText, kindOf: kindOf, infoFromUser: infoFromUser};
    if (typeof module === "object" && module.exports)
        module.exports = api;
    return api;
})();
