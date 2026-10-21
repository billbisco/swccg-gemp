package com.gempukku.swccgo.async;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Play now pages: guest.html and game.html in Play now mode. */
public class PlayNowWebClientTests {
    private static final Path WEB = Paths.get("src", "main", "web");

    private static String read(String path) throws IOException {
        return Files.readString(WEB.resolve(path), StandardCharsets.UTF_8);
    }

    @Test
    public void GuestPageIsNotIndexedAndNeverLinksToTheHall() throws IOException {
        String html = read("guest.html");
        //test1
        assertTrue(html.contains("<meta name=\"robots\" content=\"noindex, nofollow\">"));
        //test2: guests never see the hall.
        assertFalse(html.contains("hall.html"));
        assertFalse(read("js/gemp-016/guestUi.js").contains("hall.html"));
        //test3
        assertTrue(html.contains("js/gemp-016/playNow.js"));
        assertTrue(html.contains("js/gemp-016/guestUi.js"));
    }

    @Test
    public void GamePageLoadsPlayNowBeforeTheGameUi() throws IOException {
        //test4
        String html = read("game.html");
        int playNow = html.indexOf("js/gemp-016/playNow.js");
        int gameUi = html.indexOf("js/gemp-016/gameUi.js");
        assertTrue(playNow > 0 && playNow < gameUi);
    }

    @Test
    public void PlayNowGameOptionsHaveNoTimerOrCancelButtons() throws IOException {
        //test5: bot games have no timers, and a guest cannot request a cancel.
        String js = read("js/gemp-016/gameUi.js");
        int start = js.indexOf("addPlayNowOptions: function");
        int end = js.indexOf("showPlayNowEnd: function", start);
        assertTrue(start > 0 && end > start);
        String options = js.substring(start, end);
        assertTrue(options.contains("concede()"));
        assertTrue(options.contains("PlayNow.mute("));
        assertFalse(options.contains("cancel()"));
        assertFalse(options.contains("extendGameTimer"));
        assertFalse(options.contains("disableActionTimer"));
    }

    @Test
    public void GuestModeSendsGuestsBackToPlayNowNotTheHall() throws IOException {
        //test6
        String js = read("js/gemp-016/gameUi.js");
        assertTrue(js.contains("if (this.guestMode)\n            gameHallButton = false;"));
        int end = js.indexOf("showPlayNowEnd: function");
        String endScreen = js.substring(end, js.indexOf("toggleMuteObservers: function", end));
        int guestBranch = endScreen.indexOf("if (this.guestMode)");
        int elseBranch = endScreen.indexOf("} else {", guestBranch);
        assertFalse(endScreen.substring(guestBranch, elseBranch).contains("hall.html"));
    }

    @Test
    public void GuestPageOffersResumeForUnfinishedGames() throws IOException {
        //test8: the page lists unfinished games with a Resume button that asks the server to resume them
        assertTrue(read("guest.html").contains("id=\"playNowGameList\""));
        String js = read("js/gemp-016/guestUi.js");
        assertTrue(js.contains("GuestUi.showGames(status.games"));
        assertTrue(js.contains("PlayNow.resume(gameId"));
        assertTrue(read("js/gemp-016/playNow.js").contains("/guest/resume"));
    }

    @Test
    public void PlayNowHelpersPassTheirNodeTests() throws Exception {
        //test7: runs src/test/js/playNowTests.js when node is installed (it is on the GitHub runners).
        Assumptions.assumeTrue(nodeAvailable(), "node not installed");
        Process p = new ProcessBuilder("node", Paths.get("src", "test", "js", "playNowTests.js").toString(),
                WEB.resolve("js/gemp-016/playNow.js").toAbsolutePath().toString())
                .redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(p.waitFor(60, TimeUnit.SECONDS));
        assertEquals(0, p.exitValue(), out);
        assertTrue(out.contains("playNow ok"), out);
    }

    private static boolean nodeAvailable() {
        try {
            Process p = new ProcessBuilder("node", "--version").redirectErrorStream(true).start();
            return p.waitFor(10, TimeUnit.SECONDS) && p.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    @Test
    public void SignupShowsTheNoticeWhenAGuestGameCouldNotBeMoved() throws IOException {
        //test9: the signup page passes the response on and shows the server's notice before going to the hall
        assertTrue(read("js/gemp-016/communication.js").contains("callback(xml, request.status, request);"));
        String html = read("index.html");
        int notice = html.indexOf("getResponseHeader(\"X-Guest-Notice\")");
        int hall = html.indexOf("location.href = \"/gemp-swccg/hall.html\"", notice);
        assertTrue(notice > 0 && hall > notice);
        assertTrue(html.contains("js/gemp-016/communication.js?v="));
    }
}
