package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

/** Keeps older exact-title references compatible with differently titled Legacy personas. */
public final class LegacyPersonaFilters {
    public static final Filter BIGGS = Filters.or(Filters.Biggs, Persona.BIGGS);

    private LegacyPersonaFilters() {
    }
}
