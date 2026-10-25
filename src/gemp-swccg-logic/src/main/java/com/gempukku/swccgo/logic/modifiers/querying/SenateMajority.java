package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

/**
 * Senate majority queries that cannot live in Politics.java.
 */
public interface SenateMajority extends Politics {

    @Override
    default String getPlayerWithSenateMajority(GameState gameState) {
        String player = Politics.super.getPlayerWithSenateMajority(gameState);
        if (player != null && playerMayNotHaveSenateMajority(gameState, player)) {
            return null;
        }
        return player;
    }

    private boolean playerMayNotHaveSenateMajority(GameState gameState, String playerId) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_HAVE_SENATE_MAJORITY)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }
}
