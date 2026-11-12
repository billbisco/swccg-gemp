package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InviteOnlyHallTests {
    private PlayNowTestServer server() throws Exception {
        PlayNowTestServer s = new PlayNowTestServer();
        s.hallServer.setOperational();
        var field = SwccgoServer.class.getDeclaredField("_deckDao");
        field.setAccessible(true);
        var dao = (DeckDAO) field.get(s.swccgoServer);
        when(dao.getDeckForPlayer(any(Player.class), eq("light"))).thenReturn(GuestDecks.deck(PlayNowSide.LIGHT));
        when(dao.getDeckForPlayer(any(Player.class), eq("dark"))).thenReturn(GuestDecks.deck(PlayNowSide.DARK));
        return s;
    }

    @Test
    public void InviteOnlyTableDescriptionNamesTheInviteeIgnoringCase() {
        //test1: an invite-only table whose description is the invitee's name counts that player as the invitee
        //test2: a different name, or a table that is not invite-only, does not
        //test3: surrounding spaces and letter case do not change who was invited
        assertTrue(AwaitingTable.isInvitee(true, "Luke Sky", "Luke Sky")); //test1
        assertTrue(AwaitingTable.isInvitee(true, "Luke Sky", "luke sky")); //test3
        assertTrue(AwaitingTable.isInvitee(true, "  Luke Sky  ", "Luke Sky")); //test3
        assertFalse(AwaitingTable.isInvitee(true, "Luke Sky", "Leia")); //test2
        assertFalse(AwaitingTable.isInvitee(false, "Luke Sky", "Luke Sky")); //test2
        assertFalse(AwaitingTable.isInvitee(true, null, "Luke Sky")); //test2
        assertFalse(AwaitingTable.isInvitee(true, "Luke Sky", null)); //test2
    }

    @Test
    public void InviteOnlyHallMarksTheInviteeAndRefusesOtherJoiners() throws Exception {
        //test1: the invitee's hall row is invite-only and invitedYou
        //test2: a third player's row is invite-only and not invitedYou
        //test3: the host is already seated, so playing is true and invitedYou is false
        //test4: a third player cannot join
        //test5: the invited player can join
        PlayNowTestServer s = server();
        Player host = s.member(1, "inviteHost");
        Player invitee = s.member(2, "inviteGuest");
        Player other = s.member(3, "inviteOther");
        s.hallServer.createNewTable("premiere_reflections_gold", host, "light", false, "inviteGuest", false, null,
                false, null, null, false, "default", true, null);

        HallInfoVisitor inviteeView = mock(HallInfoVisitor.class);
        s.hallServer.processHall(invitee, inviteeView);
        var inviteeRow = mockingDetails(inviteeView).getInvocations().stream()
                .filter(i -> i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow();
        assertEquals(Boolean.TRUE, inviteeRow.getArgument(21)); //test1
        assertEquals(Boolean.TRUE, inviteeRow.getArgument(22)); //test1
        assertEquals(Boolean.FALSE, inviteeRow.getArgument(12)); // not seated

        HallInfoVisitor otherView = mock(HallInfoVisitor.class);
        s.hallServer.processHall(other, otherView);
        var otherRow = mockingDetails(otherView).getInvocations().stream()
                .filter(i -> i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow();
        assertEquals(Boolean.TRUE, otherRow.getArgument(21)); //test2
        assertEquals(Boolean.FALSE, otherRow.getArgument(22)); //test2

        HallInfoVisitor hostView = mock(HallInfoVisitor.class);
        s.hallServer.processHall(host, hostView);
        var hostRow = mockingDetails(hostView).getInvocations().stream()
                .filter(i -> i.getMethod().getName().equals("visitTable")).findFirst().orElseThrow();
        assertEquals(Boolean.TRUE, hostRow.getArgument(12)); //test3
        assertEquals(Boolean.TRUE, hostRow.getArgument(21));
        assertEquals(Boolean.FALSE, hostRow.getArgument(22)); //test3

        try {
            s.hallServer.joinTableAsPlayer(otherRow.getArgument(0), other, "dark", false, null);
            fail();
        } catch (HallException ignored) {
        } //test4
        assertTrue(s.hallServer.joinTableAsPlayer(inviteeRow.getArgument(0), invitee, "dark", false, null)); //test5
    }
}
