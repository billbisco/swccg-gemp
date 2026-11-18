package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.tournament.PlayerMadeQueue;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProjectiveTelepathyHallTests {
    private PlayNowTestServer server() throws Exception {
        var s = new PlayNowTestServer();
        s.hallServer.setOperational();
        var field = SwccgoServer.class.getDeclaredField("_deckDao");
        field.setAccessible(true);
        var dao = (DeckDAO) field.get(s.swccgoServer);
        var light = new com.gempukku.swccgo.logic.vo.SwccgDeck("light");
        for (int i = 0; i < 60; i++) light.addCard("1_28");
        when(dao.getDeckForPlayer(any(Player.class), eq("light"))).thenReturn(light);
        var dark = new com.gempukku.swccgo.logic.vo.SwccgDeck("dark");
        dark.addCard("5_149o");
        for (int i = 1; i < 60; i++) dark.addCard("1_167");
        when(dao.getDeckForPlayer(any(Player.class), eq("dark"))).thenReturn(dark);
        return s;
    }

    @Test
    public void ProjectiveTelepathyCasualChoiceReachesActualCardsAndReadOnlySummary() throws Exception {
        for (String choice : java.util.List.of("printed", "current")) {
            var s = server();
            var host = s.member(1, "telepathyHost");
            var joiner = s.member(2, "telepathyJoiner");
            var table = s.hallServer.createNewTable("open", host, "light", false, "Rules", false, null,
                    false, null, null, false, false, Map.of(GameOptions.PROJECTIVE_TELEPATHY, choice));
            var visitor = mock(HallInfoVisitor.class);
            s.hallServer.processHall(joiner, visitor);
            var row = mockingDetails(visitor).getInvocations().stream()
                    .filter(i -> i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow();
            assertEquals("Rules: " + choice + " Telepathy", row.getArgument(20));
            assertEquals(table.getGameSetup(), s.hallServer.getDeckPickerSetup(row.getArgument(0), null));
            s.hallServer.joinTableAsPlayer(row.getArgument(0), joiner, "dark", false, null);
            var game = s.swccgoServer.getGameById(table.getStartedGameId());
            assertNotNull(game);
            String selected = choice.equals("printed") ? "5_149o" : "5_149";
            var telepathy = game.getGameState().getAllPermanentCards().stream()
                    .filter(c -> c.getTitle().equals("Projective Telepathy")).findFirst().orElseThrow();
            assertEquals(selected, telepathy.getBlueprintId(true));
            assertEquals(selected, game.getFormat().resolveCardId("5_149o"));
            assertEquals(table.getGameSetup(), game.getGameSetup());
            var summary = JsonParser.parseString(game.getGameRulesSummary()).getAsJsonObject();
            assertEquals("Open", summary.get("format").getAsString());
            assertEquals(choice.equals("printed") ? "Printed text" : "Current errata",
                    summary.getAsJsonObject("options").get("Rules: Projective Telepathy").getAsString());
        }
    }

    @Test
    public void ProjectiveTelepathyEventChoiceIsLockedForJoiners() throws Exception {
        var s = server();
        var host = s.member(1, "telepathyOrganizer");
        s.hallServer.createPlayerMadeQueue(host, "Rules", "open", "swiss", 4, 4, 0, false,
                "light", false, "dark", false, null, null, "constructed", null, null, 0,
                Map.of(GameOptions.PROJECTIVE_TELEPATHY, "printed"));
        var field = HallServer.class.getDeclaredField("_tournamentQueues");
        field.setAccessible(true);
        var queue = ((Map<?, ?>) field.get(s.hallServer)).values().stream()
                .filter(q -> q instanceof PlayerMadeQueue).map(q -> (PlayerMadeQueue) q).findFirst().orElseThrow();
        var formats = VirtualTableScenario._formatLibrary;
        var chosen = queue.getGameSetup(formats);
        assertEquals(chosen, s.hallServer.getDeckPickerSetup(null, queue.getQueueId()));
        assertNull(s.hallServer.getDeckPickerSetup("missing", null));
        assertEquals("printed", chosen.get(GameOptions.PROJECTIVE_TELEPATHY, formats.getFormat("open")));
        assertTrue(s.hallServer.getEventRulesSummary(queue.getQueueId()).contains("Options: Rules: printed Telepathy"));
        assertTrue(s.hallServer.joinPlayerMadeQueue(queue.getQueueId(), s.member(2, "telepathyEntrant"),
                "light", false, "dark", false, null, null));
        assertEquals(chosen, queue.getGameSetup(formats));
        assertThrows(IllegalStateException.class, () -> queue.setGameSetup(chosen.with(GameOptions.PROJECTIVE_TELEPATHY, "current")));
    }
}
