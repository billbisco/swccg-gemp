package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

public class NumberedShieldPrintingHallTests {
    @Test
    public void NormalGameKeepsNumberedConversionsButNormalizesPcAlias() throws Exception {
        var server = new PlayNowTestServer();
        server.hallServer.setOperational();
        var field = SwccgoServer.class.getDeclaredField("_deckDao");
        field.setAccessible(true);
        var dao = (DeckDAO) field.get(server.swccgoServer);
        var dark = new SwccgDeck("dark");
        GuestDecks.deck(PlayNowSide.DARK).getCards().forEach(dark::addCard);
        dark.addCardOutsideDeck("1261_24");
        dark.addCardOutsideDeck("1261_25");
        dark.addCardOutsideDeck("1200_2");
        when(dao.getDeckForPlayer(any(Player.class), eq("dark"))).thenReturn(dark);
        when(dao.getDeckForPlayer(any(Player.class), eq("light"))).thenReturn(GuestDecks.deck(PlayNowSide.LIGHT));

        var host = server.member(1, "shieldHost");
        var opponent = server.member(2, "shieldOpponent");
        var table = server.hallServer.createNewTable("anything_goes", host, "dark", false, "shields", false,
                null, false, null, null, false, "default", false, "0");
        var visitor = mock(HallInfoVisitor.class);
        server.hallServer.processHall(opponent, visitor);
        var row = mockingDetails(visitor).getInvocations().stream()
                .filter(i -> i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow();
        assertTrue(server.hallServer.joinTableAsPlayer(row.getArgument(0), opponent, "light", false, null));

        var game = server.swccgoServer.getGameById(table.getStartedGameId());
        var outsideIds = game.getGameState().getOutsideOfDeck(host.getName()).stream()
                .map(card -> card.getBlueprintId(true)).toList();
        assertEquals(List.of("1261_24", "1261_25", "601_37"), outsideIds);
    }
}
