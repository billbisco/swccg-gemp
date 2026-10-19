package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

/**
 * Layout helpers for Appendix E interior/exterior sites (PLAN E13 / RULINGS R13).
 */
public final class LocationLayoutFilters {
    private LocationLayoutFilters() {
    }

    /**
     * Buffer interior/exterior sites: Entrance Cavern, Ewok Village, interior/exterior docking bays.
     */
    public static Filter bufferInteriorExteriorSite() {
        return Filters.and(Filters.interior_site, Filters.exterior_site,
                Filters.or(Filters.docking_bay, Filters.Entrance_Cavern, Filters.Ewok_Village));
    }

    /**
     * Non-buffer interior/exterior sites. On named Ap. E planet layouts these deploy as exterior sites.
     */
    public static Filter nonBufferInteriorExteriorSite() {
        return Filters.and(Filters.interior_site, Filters.exterior_site, Filters.not(bufferInteriorExteriorSite()));
    }
}
