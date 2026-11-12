package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** swccg-guild/work#224: a table with "Deck: 420" holds the host's deck to it, naming the card. */
public class FourTwentyHallTests {
    private static SwccgDeck deck(String name, String extra) {
        SwccgDeck deck = new SwccgDeck(name);
        deck.addCard("1_281"); // Alderaan (location)
        deck.addCard("1_188"); // MSE-6 'Mouse' Droid, destiny 0
        if (extra != null)
            deck.addCard(extra);
        while (deck.getCards().size() < 60)
            deck.addCard("1_193"); // R4-M9, destiny 4
        return deck;
    }

    private PlayNowTestServer server() throws Exception {
        var s = new PlayNowTestServer();
        s.hallServer.setOperational();
        var field = SwccgoServer.class.getDeclaredField("_deckDao");
        field.setAccessible(true);
        var dao = (DeckDAO) field.get(s.swccgoServer);
        when(dao.getDeckForPlayer(any(Player.class), eq("dark420"))).thenReturn(deck("dark420", null));
        when(dao.getDeckForPlayer(any(Player.class), eq("darkObjective"))).thenReturn(deck("darkObjective", "7_296"));
        when(dao.getDeckForPlayer(any(Player.class), eq("darkOne"))).thenReturn(deck("darkOne", "1_194"));
        return s;
    }

    private static AwaitingTable table(PlayNowTestServer s, String format, Player host, String deck,
            Map<String, String> options) throws HallException {
        return s.hallServer.createNewTable(format, host, deck, false, "420", false, null,
                false, null, null, false, false, options);
    }

    private static String tableId(PlayNowTestServer s, Player viewer) {
        var visitor = mock(HallInfoVisitor.class);
        s.hallServer.processHall(viewer, visitor);
        var row = mockingDetails(visitor).getInvocations().stream()
                .filter(i -> i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow();
        assertEquals("the table row shows the option", "Deck: 420", row.getArgument(20));
        return row.getArgument(0);
    }

    @Test
    public void HostDeckIsCheckedAgainst420WithACardSpecificReason() throws Exception {
        var s = server();
        var host = s.member(1, "host420");
        HallException objective = assertThrows(HallException.class,
                () -> table(s, "premiere_ds2", host, "darkObjective", Map.of("fourTwenty", "true")));
        assertTrue(objective.getMessage(), objective.getMessage().contains("Carbon Chamber Testing")
                && objective.getMessage().contains("Objectives are not permitted"));
        HallException one = assertThrows(HallException.class,
                () -> table(s, "premiere_ds2", host, "darkOne", Map.of("fourTwenty", "true")));
        assertTrue(one.getMessage(), one.getMessage().contains("Stormtrooper") && one.getMessage().contains("printed destiny 1"));
        // with 420 off the same deck gets the ordinary format decision
        var plain = table(s, "premiere_ds2", s.member(3, "plainHost"), "darkOne", Map.of());
        assertTrue(plain.getGameSetup().isDefault());
    }

    @Test
    public void TableShows420AndAcceptsAQualifyingDeck() throws Exception {
        var s = server();
        var host = s.member(1, "host420ok");
        var table = table(s, "premiere_ds2", host, "dark420", Map.of("fourTwenty", "true"));
        assertEquals("Deck: 420", table.getGameSetup().chipsText());
        assertEquals("premiere_ds2", table.getGameSetup().getFormatCode());
        assertNotNull(tableId(s, s.member(2, "viewer420")));
    }
}
