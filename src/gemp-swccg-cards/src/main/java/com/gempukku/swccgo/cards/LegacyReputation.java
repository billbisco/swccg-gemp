package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

/**
 * 'Reputation' (Virtual Block 8.1 Contract Killers / Feared Throughout The Galaxy, Coruscant: Casino): an assassin
 * gains 'reputation' when a card is stacked face down on it; each card stacked on an assassin is one 'reputation'.
 */
public final class LegacyReputation {
    private LegacyReputation() {
    }

    /**
     * Assassins with 'reputation' (at least one card stacked on them).
     */
    public static Filter assassinWithReputation() {
        return Filters.and(Filters.assassin, Filters.hasStacked(Filters.any));
    }
}
