package com.gempukku.swccgo.async;

import com.gempukku.swccgo.service.PlayerNames;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Player names in the browser (swccg-guild/work#249, t129u): name boxes filter as typed, names filter as shown. */
public class PlayerNamesWebClientTests {
    private static final Path WEB = Paths.get("src", "main", "web");
    private static final String[] PAGES = {"index.html", "hall.html", "game.html", "deckBuild.html", "merchant.html",
            "soloDraft.html"};

    private static String read(String path) throws IOException {
        return Files.readString(WEB.resolve(path), StandardCharsets.UTF_8);
    }

    @Test
    public void PagesLoadTheNameFilterBeforeChat() throws IOException {
        //test1: every page that shows names loads js/swccg-names.js before chat.js (which defines playerNameText).
        for (String page : PAGES) {
            String html = read(page);
            int names = html.indexOf("js/swccg-names.js");
            int chat = html.indexOf("js/gemp-016/chat.js");
            assertTrue(names > 0 && names < chat, page);
        }
        String index = read("index.html");
        assertTrue(index.indexOf("js/swccg-names.js") < index.indexOf("js/swccg-login-box.js"));
    }

    @Test
    public void NameBoxesAreFiltered() throws IOException {
        //test2: classic signup and the sign-in box's signup mode filter the name box; sign-in does not.
        String form = read("includes/registrationForm.html");
        assertTrue(form.contains("id='login' type='text' maxlength='32'"));
        assertTrue(form.contains("id='loginNameNote'"));
        assertTrue(read("index.html").contains("SwccgNames.attach(document.getElementById(\"login\")"));
        String box = read("js/swccg-login-box.js");
        assertTrue(box.contains("SwccgNames.attach(this.name"));
        assertTrue(box.contains("box.mode.value === \"signup\""));
        assertTrue(box.contains("this.name.setAttribute(\"maxlength\", signup ? \"32\" : \"40\")"));
    }

    @Test
    public void NamesAreShownThroughTheFilter() throws IOException {
        //test3: the places that show names use playerNameText.
        String[][] uses = {
                {"js/gemp-016/chat.js", "this.escapeHtml(playerNameText(from))"},
                {"js/gemp-016/chat.js", "chatListEntryHtml(userName)"},
                {"js/gemp-016/hallUi.js", "playersStr += playerNameText(players[playerI])"},
                {"js/gemp-016/hallUi.js", "lastField.text(playerNameText(winner))"},
                {"js/gemp-016/hallUi.js", "this.playerListText(playersStr)"},
                {"js/gemp-016/gameUi.js", "playerNameText(this.allPlayerIds[i])"},
                {"js/gemp-016/gameHistoryUi.js", "filterLink(playerNameText(row.opponent)"},
                {"js/gemp-016/tournamentResultsUi.js", ".text(playerNameText(standing.player))"},
                {"js/gemp-016/leagueResultsUi.js", "playerNameText(match.getAttribute(\"winner\"))"},
                {"js/gemp-016/playerPicker.js", "playerNameText(item.label)"},
        };
        for (String[] use : uses)
            assertTrue(read(use[0]).contains(use[1]), use[0] + ": " + use[1]);
    }

    @Test
    public void NameFilterScriptMirrorsPlayerNames() throws Exception {
        //test4: runs src/test/js/playerNamesTests.js when node is installed (it is on the GitHub runners): the
        // browser filter matches PlayerNames.forDisplay for every BMP character (both read the Unicode 15.0 table).
        Assumptions.assumeTrue(nodeAvailable(), "node not installed");
        StringBuilder sb = new StringBuilder();
        for (int cp = 0; cp <= 0xFFFF; cp++) {
            if (cp >= 0xD800 && cp <= 0xDFFF)
                continue;
            String shown = PlayerNames.forDisplay("a" + (char) cp + "b");
            if (shown.indexOf('\n') >= 0 || shown.indexOf('\r') >= 0)
                continue;
            sb.append(cp).append('\t').append(shown).append('\n');
        }
        Path expected = Files.createTempFile("player-names", ".tsv");
        try {
            Files.writeString(expected, sb, StandardCharsets.UTF_8);
            Process p = new ProcessBuilder("node", Paths.get("src", "test", "js", "playerNamesTests.js").toString(),
                    WEB.resolve("js/swccg-names.js").toAbsolutePath().toString(), expected.toAbsolutePath().toString())
                    .redirectErrorStream(true).start();
            String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(p.waitFor(60, TimeUnit.SECONDS), "node timed out");
            assertEquals(0, p.exitValue(), out);
        } finally {
            Files.deleteIfExists(expected);
        }
    }

    @Test
    public void NameFilterScriptHasPlayerNamesTable() throws IOException {
        //test5: js/swccg-names.js carries the same Unicode 15.0 table as PlayerNames (and the sign-in service; all
        // copies come from the sign-in service's scripts/gen-namechars.py).
        String js = read("js/swccg-names.js");
        int a = js.indexOf("var CHARS15 = "), b = js.indexOf("/* END CHARS15 */");
        assertTrue(a > 0 && b > a, "CHARS15 block");
        StringBuilder table = new StringBuilder();
        String[] parts = js.substring(a, b).split("\"", -1);
        for (int i = 1; i < parts.length; i += 2)
            table.append(parts[i]);
        assertEquals(PlayerNames.charTable(), table.toString());
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
