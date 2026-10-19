package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Naboo.
 */
public class NabooLayout extends AbstractSystemLayout {

    // Layout order for Naboo:
    //  1) Sites (in forward or reverse order)
    //      A) Theed Palace Throne Room
    //      B) Interior sites
    //      C) Interior/Exterior sites
    //      D) Theed Palace Courtyard
    //      E) Exterior sites
    //      F) Underwater sites
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public NabooLayout() {
    }

    /**
     * Creates the location layout for Naboo.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public NabooLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Theed Palace Throne Room
                        new LocationGroup("Theed Palace Throne Room", Filters.Theed_Palace_Throne_Room),
                        //  B) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.or(Filters.exterior_site,
                                Filters.underwater_site, Filters.Theed_Palace_Throne_Room)), Filters.partOfSystem(systemName))),
                        //  C) Interior/Exterior sites (buffer sites only)
                        new LocationGroup("Interior/Exterior sites", Filters.and(LocationLayoutFilters.bufferInteriorExteriorSite(), Filters.partOfSystem(systemName))),
                        //  D) Theed Palace Courtyard
                        new LocationGroup("Theed Palace Courtyard", Filters.Theed_Palace_Courtyard),
                        //  E) Exterior sites (non-buffer interior/exterior sites lay out as exterior)
                        new LocationGroup("Exterior sites", Filters.and(Filters.or(
                                Filters.and(Filters.exterior_site, Filters.not(Filters.or(Filters.interior_site,
                                        Filters.Theed_Palace_Courtyard))),
                                Filters.and(LocationLayoutFilters.nonBufferInteriorExteriorSite(),
                                        Filters.not(Filters.Theed_Palace_Courtyard))),
                                Filters.partOfSystem(systemName))),
                        //  F) Underwater sites
                        new LocationGroup("Underwater sites", Filters.and(Filters.underwater_site, Filters.partOfSystem(systemName)))));

        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
