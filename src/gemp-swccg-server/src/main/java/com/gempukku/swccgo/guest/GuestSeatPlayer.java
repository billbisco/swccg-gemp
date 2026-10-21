package com.gempukku.swccgo.guest;

import com.gempukku.swccgo.game.Player;

/**
 * A guest acting in their own Play now game. It has no account: no id, no password and no roles. Its name is the
 * guest's seat in the game ("Light Side" or "Dark Side").
 */
public final class GuestSeatPlayer extends Player {
    public GuestSeatPlayer(String seatId) {
        super(-1, seatId, null, "", null, null, null, null);
    }

    public static boolean isGuest(Player player) {
        return player instanceof GuestSeatPlayer;
    }
}
