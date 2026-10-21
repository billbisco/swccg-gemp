/* guest.html: pick a side and a bot, then start a Play now game. guest.html?side=&bot= starts right away. */
var GuestUi = {
    starting: false,

    init: function () {
        $("#playNowButton").button().click(function () {
            GuestUi.startGame($("input[name=side]:checked").val(), $("input[name=bot]:checked").val());
        });
        var choice = PlayNow.parseChoice(window.location.search);
        if (choice.side != null)
            $("input[name=side][value=" + choice.side + "]").attr("checked", true);
        if (choice.bot != null)
            $("input[name=bot][value=" + choice.bot + "]").attr("checked", true);

        PlayNow.status(function (status) {
            if (!status.enabled) {
                GuestUi.showError(PlayNow.errorText(503, "disabled", null));
                $("#playNowPicker").hide();
                return;
            }
            if (choice.side != null && choice.bot != null)
                GuestUi.startGame(choice.side, choice.bot);
        }, GuestUi.showError);
    },

    startGame: function (side, bot) {
        if (GuestUi.starting)
            return;
        GuestUi.starting = true;
        $("#playNowError").hide();
        $("#playNowStarting").show();
        PlayNow.start(side, bot, function (game) {
            window.location.href = PlayNow.gameUrl(game.gameId, game.participantId, game.guest);
        }, function (text) {
            GuestUi.starting = false;
            $("#playNowStarting").hide();
            GuestUi.showError(text);
        });
    },

    showError: function (text) {
        $("#playNowError").text(text).show();
    }
};

$(document).ready(function () {
    GuestUi.init();
});
