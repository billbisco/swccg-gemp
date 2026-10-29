package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StartingCreditsHallTests {
    private PlayNowTestServer server() throws Exception {
        var s=new PlayNowTestServer();s.hallServer.setOperational();
        var field=SwccgoServer.class.getDeclaredField("_deckDao");field.setAccessible(true);
        var dao=(DeckDAO)field.get(s.swccgoServer);
        when(dao.getDeckForPlayer(any(Player.class),eq("light"))).thenReturn(GuestDecks.deck(PlayNowSide.LIGHT));
        when(dao.getDeckForPlayer(any(Player.class),eq("dark"))).thenReturn(GuestDecks.deck(PlayNowSide.DARK));
        return s;
    }
    @Test public void StartingCreditsHallPublishesBudgetAndPreservesItWhenOpponentJoins() throws Exception {
        var s=server();var host=s.member(1,"creditHost");var opponent=s.member(2,"creditOpponent");
        var table=s.hallServer.createNewTable("premiere_reflections_gold",host,"light",false,"credits",false,null,
                false,null,null,false,"blitz",false,"4");
        assertEquals(4,table.getStartingCredits());assertEquals(HallGameTimer.BLITZ,table.getGameTimer());
        var visitor=mock(HallInfoVisitor.class);s.hallServer.processHall(opponent,visitor);
        var row=mockingDetails(visitor).getInvocations().stream().filter(i->i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow();
        assertEquals("Premiere - Reflections Gold (4 starting credits)",row.getArgument(5));
        assertTrue(s.hallServer.joinTableAsPlayer(row.getArgument(0),opponent,"dark",false,null));
        var game=s.swccgoServer.getGameById(table.getStartedGameId());
        assertEquals(4,game.getGameState().getStartingCredits());
    }
    @Test public void StartingCreditsHallRejectsInvalidAndIneligibleValuesAndDefaultsToFive() throws Exception {
        for(String value:List.of("-1","61","1.5","bogus")) {
            var s=server();var player=s.member(1,"creditHost");
            assertThrows(HallException.class,()->s.hallServer.createNewTable("premiere_reflections_gold",player,"light",false,"",false,null,
                    false,null,null,false,"default",false,value));
        }
        var s=server();var player=s.member(1,"creditHost");
        assertThrows(HallException.class,()->s.hallServer.createNewTable("open",player,"light",false,"",false,null,
                false,null,null,false,"default",false,"5"));
        var table=s.hallServer.createNewTable("anything_goes",player,"light",false,"",false,null,
                false,null,null,false,"default",false,null);
        assertEquals(5,table.getStartingCredits());
    }
}
