/*
 * Play now: helpers shared by guest.html and game.html (Play now mode).
 * The pure functions at the top have no jQuery or DOM use, so tests can run them under node.
 */
var PlayNow = {
    SERVER: "/gemp-swccg-server",
    SIDES: {light: "Light Side", dark: "Dark Side"},
    BOTS: {ozzel: "OzzelBot (beginner)", yoda: "YodaBot (advanced)"},

    /** side and bot from a query string such as "?side=dark&bot=yoda"; unknown values come back null. */
    parseChoice: function (search) {
        var result = {side: null, bot: null};
        var query = (search || "").replace(/^\?/, "");
        var parts = query.length > 0 ? query.split("&") : [];
        for (var i = 0; i < parts.length; i++) {
            var kv = parts[i].split("=");
            var key = kv[0];
            var value = (kv[1] || "").toLowerCase();
            if (key == "side" && PlayNow.SIDES.hasOwnProperty(value))
                result.side = value;
            if (key == "bot" && PlayNow.BOTS.hasOwnProperty(value))
                result.bot = value;
        }
        return result;
    },

    /** The game page for a Play now game. */
    gameUrl: function (gameId, participantId, guest) {
        return "/gemp-swccg/game.html?gameId=" + encodeURIComponent(gameId)
            + "&participantId=" + encodeURIComponent(participantId)
            + "&playNow=1" + (guest ? "&guest=1" : "");
    },

    /** What to tell the player when a Play now request failed. */
    errorText: function (status, code, message) {
        if (message)
            return message;
        if (code == "disabled")
            return "Play now is not available right now.";
        if (code == "busy")
            return "All Play now tables are in use right now. Please try again in a few minutes.";
        if (status == 429)
            return "Too many games started. Please wait a little and try again.";
        if (status == 0)
            return "Can't reach the server. Check your connection and try again.";
        return "Something went wrong (" + status + "). Please try again.";
    },

    /** Whether a game message is the end of the game ("X is the winner due to: ..."). */
    isGameOverMessage: function (message) {
        return message != null && message.indexOf(" is the winner due to: ") > 0;
    },

    /** Starts a game; calls done(json) or failed(text). */
    start: function (side, bot, done, failed) {
        $.ajax({
            type: "POST",
            url: PlayNow.SERVER + "/guest/start",
            cache: false,
            data: {side: side, bot: bot},
            dataType: "json",
            success: done,
            error: function (xhr) {
                failed(PlayNow.errorText(xhr.status, xhr.getResponseHeader("X-Guest-Error"), xhr.getResponseHeader("message")));
            }
        });
    },

    status: function (done, failed) {
        $.ajax({
            type: "GET",
            url: PlayNow.SERVER + "/guest/status",
            cache: false,
            dataType: "json",
            success: done,
            error: function (xhr) {
                failed(PlayNow.errorText(xhr.status, xhr.getResponseHeader("X-Guest-Error"), xhr.getResponseHeader("message")));
            }
        });
    },

    /** Tells the server to hide (or show again) spectators' messages for the player. */
    mute: function (gameId, muted) {
        $.ajax({
            type: "POST",
            url: PlayNow.SERVER + "/guest/mute",
            cache: false,
            data: {gameId: gameId, muted: muted ? "true" : "false"},
            dataType: "json"
        });
    }
};

if (typeof module !== "undefined")
    module.exports = PlayNow;
