package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.Icon;

/** Creature classifications retained by Original Virtual AR1.7.1 and Legacy AR2.0, page9-5. */
public final class LegacyCreatureFilters {
    private LegacyCreatureFilters() {
    }

    public static final Filter packhunter = Filters.and(Filters.creature,
            Filters.not(Filters.parasite), Icon.SELECTIVE_CREATURE, Filters.landspeedMoreThan(1));

    public static final Filter ferocious = Filters.and(Filters.creature,
            Filters.not(Filters.parasite), Filters.not(packhunter));
}
