package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Filters for Legacy-only attributes (influence, later agendas/missions). Never add these to Filters.java.
 */
public class LegacyFilters {

    private LegacyFilters() {
    }

    /**
     * Filter that accepts cards whose current influence is greater than 0.
     */
    public static Filter hasInfluence() {
        return influenceMoreThan(0);
    }

    /**
     * Filter that accepts cards whose current influence is greater than n.
     */
    public static Filter influenceMoreThan(final float n) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getInfluence(gameState, physicalCard) > n;
            }
        };
    }
}
