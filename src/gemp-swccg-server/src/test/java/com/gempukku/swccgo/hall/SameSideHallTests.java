package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** swccg-guild/work#176/#177: "Deck: same side allowed" lets two decks of one side play each other, bots included. */
public class SameSideHallTests {
    private PlayNowTestServer server() throws Exception {
        var s = new PlayNowTestServer();
        s.hallServer.setOperational();
        var field = SwccgoServer.class.getDeclaredField("_deckDao");
        field.setAccessible(true);
        var dao = (DeckDAO) field.get(s.swccgoServer);
        when(dao.getDeckForPlayer(any(Player.class), eq("dark"))).thenReturn(GuestDecks.deck(PlayNowSide.DARK));
        when(dao.getDeckForPlayer(any(Player.class), eq("light"))).thenReturn(GuestDecks.deck(PlayNowSide.LIGHT));
        return s;
    }

    private static AwaitingTable table(PlayNowTestServer s, String format, Player host, Map<String, String> options) throws HallException {
        return s.hallServer.createNewTable(format, host, "dark", false, "same side", false, null,
                false, null, null, false, false, options);
    }

    private static String tableId(PlayNowTestServer s, Player viewer) {
        var visitor = mock(HallInfoVisitor.class);
        s.hallServer.processHall(viewer, visitor);
        return mockingDetails(visitor).getInvocations().stream()
                .filter(i -> i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow().getArgument(0).toString();
    }

    @Test
    public void SameSideDecksAreRefusedByDefaultOutsideAnythingGoes() throws Exception {
        var s = server();
        table(s, "open", s.member(1, "ssHost"), new LinkedHashMap<>());
        var opponent = s.member(2, "ssOpponent");
        HallException e = assertThrows(HallException.class, () -> s.hallServer.joinTableAsPlayer(tableId(s, opponent), opponent, "dark", false, null));
        assertTrue(e.getMessage(), e.getMessage().contains("same side"));
    }

    @Test
    public void SameSideAllowedLetsTwoDarkDecksPlay() throws Exception {
        var s = server();
        var options = new LinkedHashMap<String, String>();
        options.put("sameSide", "true");
        var table = table(s, "open", s.member(1, "ssHost2"), options);
        var opponent = s.member(2, "ssOpponent2");
        assertTrue(s.hallServer.joinTableAsPlayer(tableId(s, opponent), opponent, "dark", false, null));
        var game = s.swccgoServer.getGameById(table.getStartedGameId());
        assertTrue(game.getGameState().isSameSideGame());
        assertTrue(game.getGameState().getLastMessages().stream().anyMatch(m -> m.contains("coin toss")));
    }

    @Test
    public void SameSideAllowedIsOnByDefaultInAnythingGoesOnly() {
        var library = com.gempukku.swccgo.framework.VirtualTableScenario._formatLibrary;
        var option = com.gempukku.swccgo.game.setup.GameOptions.get(com.gempukku.swccgo.game.setup.GameOptions.SAME_SIDE);
        assertEquals("true", option.getDefault(library.getFormat("anything_goes")));
        assertEquals("false", option.getDefault(library.getFormat("open")));
        assertTrue("offered everywhere", option.isAvailable(library.getFormat("open")));
    }

    private static AwaitingTable botTable(PlayNowTestServer s, Player host, Map<String, String> options) throws HallException {
        return s.hallServer.createNewTable("open", host, "dark", false, "same side bot", false, null,
                true, "BEGINNER", "dark", false, false, options);
    }

    @Test
    public void SameSideBotTableNeedsTheOption() throws Exception {
        var s = server();
        HallException e = assertThrows(HallException.class, () -> botTable(s, s.member(1, "ssBotHost"), new LinkedHashMap<>()));
        assertTrue(e.getMessage(), e.getMessage().contains("opposite side"));
        var options = new LinkedHashMap<String, String>();
        options.put("sameSide", "true");
        var table = botTable(s, s.member(2, "ssBotHost2"), options);
        assertNotNull("the bot game starts", table.getStartedGameId());
        assertTrue(s.swccgoServer.getGameById(table.getStartedGameId()).getGameState().isSameSideGame());
    }
}
