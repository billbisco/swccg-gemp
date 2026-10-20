package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

/**
 * A short hash of the visible game position: turn, phase, current player, winner, and where every card is
 * (zone, owner, location, attachment, stack) plus the order of each player's piles. Two games that hash the
 * same at every step played the same way. Used to check a replayed move log against the original game.
 */
public final class GameStateHash {
    private static final Zone[] PILES = {Zone.HAND, Zone.RESERVE_DECK, Zone.FORCE_PILE, Zone.USED_PILE, Zone.LOST_PILE, Zone.OUT_OF_PLAY};

    private GameStateHash() {
    }

    /**
     * Hashes the current position of the game.
     * @param game the game
     * @return 16 hex characters
     */
    public static String of(SwccgGame game) {
        GameState gs = game.getGameState();
        StringBuilder sb = new StringBuilder();
        List<String> players = List.of(game.getDarkPlayer(), game.getLightPlayer());
        for (String p : players) {
            sb.append(p).append(':').append(gs.getPlayersLatestTurnNumber(p)).append(';');
        }
        sb.append('|').append(gs.getCurrentPhase()).append('|').append(gs.getCurrentPlayerId()).append('|').append(game.getWinner()).append('\n');
        List<PhysicalCard> all = new ArrayList<>(gs.getAllPermanentCards());
        all.sort(Comparator.comparingInt(PhysicalCard::getPermanentCardId));
        for (PhysicalCard c : all) {
            sb.append(c.getPermanentCardId()).append(':').append(c.getBlueprintId(true)).append(':').append(c.getZone()).append(':').append(c.getZoneOwner())
                    .append(':').append(id(c.getAtLocation())).append(':').append(id(c.getAttachedTo())).append(':').append(id(c.getStackedOn())).append('\n');
        }
        for (String p : players) {
            for (Zone z : PILES) {
                sb.append(p).append(z);
                List<PhysicalCard> pile = gs.getCardPile(p, z);
                if (pile != null) {
                    for (PhysicalCard c : pile) {
                        sb.append(',').append(c.getPermanentCardId());
                    }
                }
                sb.append('\n');
            }
        }
        try {
            byte[] h = MessageDigest.getInstance("SHA-256").digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(h, 0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static int id(PhysicalCard card) {
        return card == null ? -1 : card.getPermanentCardId();
    }
}
