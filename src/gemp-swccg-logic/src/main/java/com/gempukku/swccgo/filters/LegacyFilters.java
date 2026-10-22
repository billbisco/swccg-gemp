package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
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

    /**
     * Filter that accepts systems that may not be 'liberated' (Geonosis 1291_15).
     */
    public static Filter mayNotBeLiberated() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_LIBERATED, physicalCard).isEmpty();
            }
        };
    }

    /**
     * Filter that accepts systems that can be 'liberated': a battleground system that is not already 'liberated' and
     * is not protected from it.
     */
    public static Filter canBeLiberated() {
        return Filters.and(Filters.battleground_system, Filters.except(Filters.liberated_system), Filters.except(mayNotBeLiberated()));
    }
}
