package com.gempukku.swccgo.service;

import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/** What the player menu may show about a name (swccg-guild/work#264, AC6). */
public class PlayerCardTests {
    @Test
    public void kinds() {
        //test1: bots, guests (a guest id or a Play now seat, both spellings) and players
        assertEquals(PlayerCard.BOT, PlayerCard.kind("~OzzelBot"));
        for (String n : new String[]{"guest-AbCdEf123456", "Light Side", "Dark_Side", "light side"})
            assertEquals(PlayerCard.GUEST, PlayerCard.kind(n), n);
        for (String n : new String[]{"Luke Sky", "Light Side 2", "Guestimate"})
            assertEquals(PlayerCard.PLAYER, PlayerCard.kind(n), n);
    }

    @Test
    public void playerIdIsTheAccountId() {
        PlayerDAO dao = mock(PlayerDAO.class);
        when(dao.getPlayer("Luke Sky")).thenReturn(new Player(3812, "Luke Sky", "hash", "u", null, null, null, null));
        //test1: an account's id
        assertEquals(3812, PlayerCard.playerId(dao, "Luke Sky"));
        //test2: no id for an unknown name, guests and bots, and those never reach the database
        assertNull(PlayerCard.playerId(dao, "Nobody"));
        assertNull(PlayerCard.playerId(dao, "~OzzelBot"));
        assertNull(PlayerCard.playerId(dao, "Light Side"));
        assertNull(PlayerCard.playerId(dao, "guest-AbCdEf123456"));
        assertNull(PlayerCard.playerId(dao, ""));
        assertNull(PlayerCard.playerId(null, "Luke Sky"));
        verify(dao, times(2)).getPlayer(anyString());
    }
}
