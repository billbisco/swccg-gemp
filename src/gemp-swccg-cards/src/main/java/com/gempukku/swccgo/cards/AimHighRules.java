package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.results.AboutToRetrieveForceResult;

/**
 * Shared rules for every printing of Aim High ("whenever opponent retrieves X cards, opponent must first use X Force or
 * that retrieval is canceled").
 */
public final class AimHighRules {
    private AimHighRules() {
    }

    /**
     * Gets the amount of Force the retrieving player must use for Aim High.
     * Krayt Dragon Bones (V): whenever its owner retrieves Force because a creature has eaten a card (any card's
     * retrieval caused by an eating, e.g. Beast-Rider (V), Sarlacc, Dark Waters; not a creature's other retrievals), they need only use X-2 Force (to a minimum of 1).
     * @param game the game
     * @param aimHigh the Aim High card
     * @param retrievingPlayer the player retrieving Force
     * @param result the about to retrieve Force result
     * @return the amount of Force to use
     */
    public static float forceToUse(SwccgGame game, PhysicalCard aimHigh, String retrievingPlayer, AboutToRetrieveForceResult result) {
        float x = result.getAmountOfForceToRetrieve();
        if (result.isBecauseCreatureAteCard()
                && GameConditions.canSpot(game, aimHigh,
                Filters.and(Filters.owner(retrievingPlayer), Filters.title(Title.Krayt_Dragon_Bones), Icon.LEGACY_BLOCK_4))) {
            x = Math.max(1, x - 2);
        }
        return x;
    }
}
