package com.gempukku.swccgo.guest;

import com.gempukku.swccgo.game.Player;

/**
 * Someone acting as a guest seat ("Light Side" or "Dark Side") in their own Play now game: a guest, or a guest who
 * has since signed up and carries on the game from their account. It has no id, no password and no roles.
 */
public final class GuestSeatPlayer extends Player {
    private final String _account;

    /** A guest. */
    public GuestSeatPlayer(String seatId) {
        this(seatId, null);
    }

    /** @param account the account that adopted the game, or null for a guest */
    public GuestSeatPlayer(String seatId, String account) {
        super(-1, seatId, null, "", null, null, null, null);
        _account = account;
    }

    /** The account playing this seat, or null for a guest. */
    public String getAccount() {
        return _account;
    }

    /** True for a guest without an account. */
    public static boolean isGuest(Player player) {
        return player instanceof GuestSeatPlayer && ((GuestSeatPlayer) player)._account == null;
    }
}
