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

/** The player menu (swccg-guild/work#264, AC6): Player ID and Copy behind a click on a name, never beside it. */
public class PlayerMenuWebClientTests {
    private static final Path WEB = Paths.get("src", "main", "web");

    private static String read(String path) throws IOException {
        return Files.readString(WEB.resolve(path), StandardCharsets.UTF_8);
    }

    @Test
    public void PagesLoadThePlayerMenuBeforeChat() throws IOException {
        //test1: the pages with chat load js/gemp-016/playerMenu.js after the name filter and before chat.js
        for (String page : new String[]{"hall.html", "game.html", "index.html", "deckBuild.html", "merchant.html", "soloDraft.html"}) {
            String html = read(page);
            int names = html.indexOf("js/swccg-names.js"), menu = html.indexOf("js/gemp-016/playerMenu.js"), chat = html.indexOf("js/gemp-016/chat.js");
            assertTrue(names > 0 && names < menu && menu < chat, page);
        }
    }

    @Test
    public void NamesOpenTheMenu() throws IOException {
        //test2: the hall user list, the game's Players tab, hall tables and game history open the menu
        String[][] uses = {
                {"js/gemp-016/chat.js", "PlayerMenu.bind(entry[0], this.menuInfo(infos[i]), this.menuActions(infos[i]))"},
                {"js/gemp-016/chat.js", "this.playerListener(players, infos)"},
                {"js/gemp-016/chat.js", "Mute in hall chat"},
                {"js/gemp-016/gameUi.js", "PlayerMenu.bind(entry[0]"},
                {"js/gemp-016/hallUi.js", "row.append(this.playersCell(players, tablePlayerIds))"},
                {"js/gemp-016/hallUi.js", "PlayerMenu.bind(span[0], this.menuInfo(name, ids[i]))"},
                {"js/gemp-016/gameHistoryUi.js", "PlayerMenu.bind(link[0]"},
                {"js/gemp-016/gameHistoryUi.js", "opponentPlayerId: "},
        };
        for (String[] use : uses)
            assertTrue(read(use[0]).contains(use[1]), use[0] + ": " + use[1]);
        //test3: names are still shown through the #249 filter and set as text
        String menu = read("js/gemp-016/playerMenu.js");
        assertTrue(menu.contains(".text(nameText(info.name))"));
        assertFalse(menu.contains(".html("), "the menu never pastes server text into HTML");
    }

    @Test
    public void PlayerMenuScript() throws Exception {
        //test4: runs src/test/js/playerMenuTests.js under node (installed on the GitHub runners)
        Assumptions.assumeTrue(nodeAvailable(), "node not installed");
        Process p = new ProcessBuilder("node", Paths.get("src", "test", "js", "playerMenuTests.js").toString(),
                WEB.resolve("js/gemp-016/playerMenu.js").toAbsolutePath().toString(),
                WEB.resolve("js/gemp-016/hallUi.js").toAbsolutePath().toString(),
                WEB.resolve("js/gemp-016/hall-muted-users/muted-user.functions.js").toAbsolutePath().toString())
                .redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(p.waitFor(60, TimeUnit.SECONDS), "node timed out");
        assertEquals(0, p.exitValue(), out);
    }

    @Test
    public void PlayerMenuWorksOnJquery16Pages() throws IOException {
        //test5: game.html, index.html and the other old pages still run jQuery 1.6.2, which has no .on()/.off()
        String menu = read("js/gemp-016/playerMenu.js");
        assertFalse(menu.matches("(?s).*\\.(on|off)\\(.*"), "playerMenu.js must use .bind()/.unbind(), not .on()/.off()");
        assertTrue(read("game.html").contains("jquery-1.6.2.js"), "if game.html moves off jQuery 1.6.2 this check can go");
    }

    @Test
    public void MenuIsPlacedAgainAfterCopyFeedback() throws IOException {
        //test7: "Copied" widens the menu; it is kept inside the window (hall user list sits at the right edge)
        String menu = read("js/gemp-016/playerMenu.js");
        int copy = menu.indexOf("status.text(\"Press Ctrl+C to copy\");");
        assertTrue(copy > 0 && menu.indexOf("place();", copy) > copy && menu.indexOf("place();", copy) < menu.indexOf("function open("));
    }

    @Test
    public void HallShowsTheCarryOverNoticeFromTheHallUi() throws IOException {
        //test6: the notice is shown by GempSwccgHallUI's init (HallChatResizer has no such method)
        String hall = read("js/gemp-016/hallUi.js");
        int hallUi = hall.indexOf("var GempSwccgHallUI = Class.extend(");
        int call = hall.indexOf("this.showGuestCarryOverNotice();");
        assertTrue(hallUi > 0 && call > hallUi, "showGuestCarryOverNotice is called inside GempSwccgHallUI");
        assertEquals(call, hall.lastIndexOf("this.showGuestCarryOverNotice();"), "called once");
        assertTrue(hall.indexOf("showGuestCarryOverNotice: function") > hallUi);
    }

    private static boolean nodeAvailable() {
        try {
            Process p = new ProcessBuilder("node", "--version").redirectErrorStream(true).start();
            return p.waitFor(10, TimeUnit.SECONDS) && p.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
