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
import static org.junit.jupiter.api.Assertions.assertTrue;

/** work#221: fix a deck and save a copy without leaving the join screen. */
public class DeckFixOnJoinWebClientTests {
    private static final Path WEB = Paths.get("src", "main", "web");

    private static String read(String path) throws IOException {
        return Files.readString(WEB.resolve(path), StandardCharsets.UTF_8);
    }

    @Test
    public void PagesLoadTheDraftHistoryBeforeTheirUi() throws IOException {
        //test1: both pages load deckDraftHistory.js before the UI that uses it
        for (String page : new String[]{"hall.html", "deckBuild.html"}) {
            String html = read(page);
            int history = html.indexOf("js/gemp-016/deckDraftHistory.js");
            int ui = html.indexOf(page.equals("hall.html") ? "js/gemp-016/hallUi.js" : "js/gemp-016/deckBuildingUi.js");
            assertTrue(history > 0 && history < ui, page);
        }
    }

    @Test
    public void JoinFailureOffersEditInTheSameTabAndRetry() throws IOException {
        String hall = read("js/gemp-016/hallUi.js");
        //test2: a deck-specific refusal of a player's own deck offers Edit deck
        assertTrue(hall.contains("sampleDeck !== \"true\" && DeckDraftHistory.isDeckJoinFailure(message)"));
        //test3: the editor is a frame over the join screen (no new tab), carrying the deck, format and reason
        int open = hall.indexOf("openJoinDeckEditor:function");
        int close = hall.indexOf("closeJoinDeckEditor:function", open);
        String editor = hall.substring(open, close);
        assertTrue(editor.contains("<iframe"));
        assertTrue(editor.contains("joinFix=1") && editor.contains("&deckName=") && editor.contains("&format="));
        assertTrue(!editor.contains("window.open"));
        //test4: only messages from that frame on this origin are trusted
        assertTrue(editor.contains("event.origin !== window.location.origin"));
        //test5: after a save the saved deck is selected and the button reads Retry join; the join goes to the server
        assertTrue(hall.contains("\"Retry join\""));
    }

    @Test
    public void EditorSavesAsNewWithoutOverwriteAndChecksOverwrites() throws IOException {
        String ui = read("js/gemp-016/deckBuildingUi.js");
        //test6
        assertTrue(ui.contains("{createOnly: true} : {expectedRevision: fix.revision}"));
        //test7: a refused save keeps the draft and says why
        assertTrue(ui.contains("\"409\": function (xhr)"));
        //test8: cancel with unsaved edits asks first
        assertTrue(ui.contains("fix.history.isDirty() && !confirm("));
        String comm = read("js/gemp-016/communication.js");
        assertTrue(comm.contains("data.createOnly = \"true\"") && comm.contains("data.expectedRevision = options.expectedRevision"));
    }

    @Test
    public void DraftHistoryPassesItsNodeTests() throws Exception {
        //test9: runs src/test/js/deckDraftHistoryTests.js when node is installed (it is on the GitHub runners).
        Assumptions.assumeTrue(nodeAvailable(), "node not installed");
        Process p = new ProcessBuilder("node", Paths.get("src", "test", "js", "deckDraftHistoryTests.js").toString(),
                WEB.resolve("js/gemp-016/deckDraftHistory.js").toAbsolutePath().toString())
                .redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(p.waitFor(60, TimeUnit.SECONDS));
        assertEquals(0, p.exitValue(), out);
        assertTrue(out.contains("deckDraftHistory ok"), out);
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
