package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.results.AboutToRetrieveForceResult;

/**
 * Shared rules for every printing of Secret Plans ("opponent must first use X Force or that retrieval is canceled").
 */
public final class SecretPlansRules {
    private SecretPlansRules() {
    }

    /**
     * Gets the amount of Force the retrieving player must use for Secret Plans.
     * Tauntaun Bones (V): whenever its owner retrieves Force because a creature has eaten a card (a retrieval from a
     * creature's game text), they need only use X-2 Force (to a minimum of 1).
     * @param game the game
     * @param secretPlans the Secret Plans card
     * @param retrievingPlayer the player retrieving Force
     * @param result the about to retrieve Force result
     * @return the amount of Force to use
     */
    public static float forceToUse(SwccgGame game, PhysicalCard secretPlans, String retrievingPlayer, AboutToRetrieveForceResult result) {
        float x = game.getModifiersQuerying().getVariableValue(game.getGameState(), secretPlans, Variable.X, result.getAmountOfForceToRetrieve());
        PhysicalCard source = result.getSourceCard();
        if (source != null && Filters.creature.accepts(game, source)
                && GameConditions.canSpot(game, secretPlans,
                Filters.and(Filters.owner(retrievingPlayer), Filters.title("Tauntaun Bones"), Icon.LEGACY_BLOCK_4))) {
            x = Math.max(1, x - 2);
        }
        return x;
    }
}
