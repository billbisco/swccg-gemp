package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_051;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Let The Wookiee Win (V)
 * Original version of 1261_97: the Virtual Block 6.1 release sheet text, "▼ an [Immune to Alter] Effect and any
 * number of <> sites" (the PC card list, PC-E1 and now the current card, says "an always [Immune to Alter] Effect and
 * two <> sites"). As with the current card, the "< 3 total [Light Side Force]" limit is not enforced (GEMP TODO).
 * Test: Card_601_051_Tests.StartingInterruptDeploysAtMostTwoSitesAndAnAlwaysAlterImmuneEffect.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_097 extends Card601_051 {
    public Card2261_097() {
        setGameText("USED: Reveal the top 3 cards of Reserve Deck; place one in hand, and the others on bottom of Used Pile (in any order). STARTING: If your starting location was a non-[Special Edition] location, ▼ an [Immune to Alter] Effect and any number of <> sites with < 3 total [Light Side Force]. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1261_97", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #98", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected Filter startingEffectFilter() {
        return Filters.and(Filters.Effect, Filters.immune_to_Alter);
    }

    @Override
    protected int maxStartingSites() {
        return Integer.MAX_VALUE;
    }
}
