package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Deploy-target queries for Legacy cards that let a card deploy "here instead of on X" (Hoth: Echo Corridor (V)
 * 1282_28: "Effects may deploy here instead of on 1st Marker"). Lives in this package so it can read a card's
 * game-text deploy restriction without widening that method on the PC base classes.
 */
public final class LegacyDeployTargets {

    private LegacyDeployTargets() {
    }

    /**
     * Filter that accepts cards (that deploy to table) whose own game text lets them deploy to at least one card on
     * table accepted by the target filter.
     * @param source the card performing the query
     * @param targetFilter the filter for the cards on table (e.g. 1st Marker)
     */
    public static Filter gameTextAllowsDeployingTo(final PhysicalCard source, final Filterable targetFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
                if (!(blueprint instanceof AbstractNonLocationPlaysToTable)) {
                    return false;
                }
                Filter gameTextFilter = ((AbstractNonLocationPlaysToTable) blueprint).getValidDeployTargetFilterByCheckingGameText(gameState.getGame(), physicalCard, null, null);
                return GameConditions.canSpotLocation(gameState.getGame(), Filters.and(targetFilter, gameTextFilter));
            }
        };
    }
}
