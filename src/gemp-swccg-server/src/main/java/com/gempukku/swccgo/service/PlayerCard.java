package com.gempukku.swccgo.service;

import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.guest.GuestIdentity;
import com.gempukku.swccgo.guest.PlayNowSide;

/**
 * What the player menu may show about a name (swccg-guild/work#264, AC6; Bill 2026-11-11, t133u): the account's
 * player id, which game history and replays already key on, or that the name is a guest or a bot. Nothing else
 * about the account (no email, address, type or dates) leaves the server through this.
 */
public final class PlayerCard {
    public static final String PLAYER = "player";
    public static final String GUEST = "guest";
    public static final String BOT = "bot";

    private PlayerCard() {
    }

    /** "bot", "guest" (a guest's id or a Play now seat) or "player". Never touches the database. */
    public static String kind(String name) {
        if (Player.isBot(name))
            return BOT;
        if (GuestIdentity.isGuestId(name) || PlayNowSide.isSeatName(name))
            return GUEST;
        return PLAYER;
    }

    /** The account's player id for a name, or null for guests, bots and names with no (active) account. */
    public static Integer playerId(PlayerDAO playerDao, String name) {
        if (name == null || name.isEmpty() || playerDao == null || !PLAYER.equals(kind(name)))
            return null;
        Player player = playerDao.getPlayer(name);
        return player == null ? null : player.getId();
    }
}
