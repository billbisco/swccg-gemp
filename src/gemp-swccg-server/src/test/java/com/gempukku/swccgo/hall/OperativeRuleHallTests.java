package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.tournament.PlayerMadeQueue;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class OperativeRuleHallTests {
    private PlayNowTestServer server() throws Exception {
        var s = new PlayNowTestServer();
        s.hallServer.setOperational();
        var field = SwccgoServer.class.getDeclaredField("_deckDao");
        field.setAccessible(true);
        var dao = (DeckDAO) field.get(s.swccgoServer);
        when(dao.getDeckForPlayer(any(Player.class), eq("light"))).thenReturn(GuestDecks.deck(PlayNowSide.LIGHT));
        when(dao.getDeckForPlayer(any(Player.class), eq("dark"))).thenReturn(GuestDecks.deck(PlayNowSide.DARK));
        return s;
    }

    @Test
    public void OperativeRuleCasualChoiceReachesTheGameAndItsReadOnlySummary() throws Exception {
        for (boolean enabled : new boolean[]{true, false}) {
            var s = server();
            var host = s.member(1, "operativeHost");
            var joiner = s.member(2, "operativeJoiner");
            var table = s.hallServer.createNewTable("open", host, "light", false, "Rules", false, null,
                    false, null, null, false, false, Map.of(GameOptions.OPERATIVE_RULE, String.valueOf(enabled)));
            var visitor = mock(HallInfoVisitor.class);
            s.hallServer.processHall(joiner, visitor);
            var row = mockingDetails(visitor).getInvocations().stream()
                    .filter(i -> i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow();
            assertEquals(enabled ? "" : "Rules: no operative rule", row.getArgument(20));
            s.hallServer.joinTableAsPlayer(row.getArgument(0), joiner, "dark", false, null);
            var game = s.swccgoServer.getGameById(table.getStartedGameId());
            assertNotNull(game);
            assertEquals(enabled, game.getGameState().isOperativeRuleEnabled());
            assertEquals(table.getGameSetup(), game.getGameSetup());
            var summary = JsonParser.parseString(game.getGameRulesSummary()).getAsJsonObject();
            assertEquals("Open", summary.get("format").getAsString());
            assertEquals(enabled ? "On (default)" : "Off",
                    summary.getAsJsonObject("options").get("Rules: operative rule").getAsString());
        }
    }

    @Test
    public void OperativeRuleEventChoiceIsLockedForJoiners() throws Exception {
        var s = server();
        var host = s.member(1, "operativeOrganizer");
        s.hallServer.createPlayerMadeQueue(host, "Rules", "open", "swiss", 4, 4, 0, false,
                "light", false, "dark", false, null, null, "constructed", null, null, 0,
                Map.of(GameOptions.OPERATIVE_RULE, "false"));
        var field = HallServer.class.getDeclaredField("_tournamentQueues");
        field.setAccessible(true);
        var queue = ((Map<?, ?>) field.get(s.hallServer)).values().stream()
                .filter(q -> q instanceof PlayerMadeQueue).map(q -> (PlayerMadeQueue) q).findFirst().orElseThrow();
        var formats = VirtualTableScenario._formatLibrary;
        var chosen = queue.getGameSetup(formats);
        assertEquals("false", chosen.get(GameOptions.OPERATIVE_RULE, formats.getFormat("open")));
        assertTrue(s.hallServer.getEventRulesSummary(queue.getQueueId()).contains("Options: Rules: no operative rule"));
        assertTrue(s.hallServer.joinPlayerMadeQueue(queue.getQueueId(), s.member(2, "operativeEntrant"),
                "light", false, "dark", false, null, null));
        assertEquals(chosen, queue.getGameSetup(formats));
        assertThrows(IllegalStateException.class, () -> queue.setGameSetup(chosen.with(GameOptions.OPERATIVE_RULE, "true")));
    }
}
