package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_065;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Interrupt
 * Title: Ambush (V)
 * Original version of 1250_65: the Virtual Block 5 release sheet text, "free if at Coruscant" (any Coruscant
 * location, the system included; the PC card list, PC-E1 and now the current card, says "on Coruscant"). Gameplay
 * test: Card_1250_065_Tests.AmbushOriginalVersionRetrievalIsFreeAtCoruscantIncludingItsSystem.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_065 extends Card1250_065 {
    public Card2250_065() {
        setGameText("Use 1 Force to deploy up to two clones from Reserve Deck; reshuffle. OR If your clones occupy three related battlegrounds during your control phase, use 2 Force (free if at Coruscant) to retrieve any one card into hand. OR If your clone in battle, add one battle destiny.");
        setLegacyVersion(new LegacyVersion("1250_65", "",
                "Virtual Block 5 release sheet (2009-12-07), card #65", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected Filter freeRetrievalLocations() {
        return Filters.Coruscant_location;
    }
}
