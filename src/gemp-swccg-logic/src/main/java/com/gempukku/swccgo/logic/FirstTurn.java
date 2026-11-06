package com.gempukku.swccgo.logic;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Who takes the first turn. The Dark Side seat goes first unless a card says the Light Side goes first (Death Star or
 * Massassi Throne Room as a starting location, According To My Design, ...).
 * <p>
 * swccg-guild/work#176 (Bill 2026-11-05): those cards read seat-neutrally, so they also work when both decks are the
 * same side. A Light Side card that makes "Light Side go first" makes its owner go first (Massassi Throne Room: you go
 * first); a Dark Side card makes its owner's opponent go first (Death Star: your opponent goes first). In a Light vs Dark
 * game both always name the Light Side seat, as before. When two cards name different players (both players started at
 * Death Star, or both at Massassi Throne Room) the claims cancel and the Dark Side seat goes first.
 */
public final class FirstTurn {
    private FirstTurn() {
    }

    /**
     * @return the player who takes the first turn
     */
    public static String firstPlayer(SwccgGame game) {
        GameState gameState = game.getGameState();
        Set<String> claims = claims(game);
        if (claims.size() == 1)
            return claims.iterator().next();
        return gameState.getDarkPlayer();
    }

    /**
     * @return the players named by cards that make the "Light Side" go first, without repeats
     */
    public static Set<String> claims(SwccgGame game) {
        GameState gameState = game.getGameState();
        Set<String> claims = new LinkedHashSet<String>();
        for (PhysicalCard source : game.getModifiersQuerying().getFlagActiveSources(gameState, ModifierFlag.LIGHT_SIDE_GOES_FIRST))
            claims.add(beneficiary(game, source));
        return claims;
    }

    /**
     * @return the player a "Light Side goes first" card names: its owner for a Light Side card, otherwise its owner's
     * opponent; the Light Side seat when there is no card
     */
    public static String beneficiary(SwccgGame game, PhysicalCard source) {
        GameState gameState = game.getGameState();
        if (source == null || source.getOwner() == null)
            return gameState.getLightPlayer();
        return source.getBlueprint().getSide() == Side.LIGHT ? source.getOwner() : gameState.getOpponent(source.getOwner());
    }
}
