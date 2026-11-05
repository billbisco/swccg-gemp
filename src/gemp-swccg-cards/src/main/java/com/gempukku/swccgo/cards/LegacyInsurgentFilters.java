package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

/**
 * "Insurgent" (Virtual Block 7): a character with "Insurgent" in lore (Tarfful, Bel Iblis, Rahm Kota, ...) or one
 * that a card says is an Insurgent (Declaration Of Rebellion: Bail, Leia and Mon Mothma), which gives it
 * {@link Keyword#INSURGENT}.
 */
public final class LegacyInsurgentFilters {
    private LegacyInsurgentFilters() {
    }

    public static Filter insurgent() {
        return Filters.and(Filters.character, Filters.or(Keyword.INSURGENT, Filters.loreContains("Insurgent")));
    }
}
