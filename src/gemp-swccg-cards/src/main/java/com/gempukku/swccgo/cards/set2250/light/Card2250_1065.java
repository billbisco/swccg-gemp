package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_065;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Ambush (V)
 * PC-E1 version of 1250_65: the PC website card-list text (by 2012-07-21; generated from Holotable data), "free if on
 * Coruscant" (sites only). The PC card image (2013-02-25) and the Master List (2013-04-27, the current card) print the
 * release-sheet "free if at Coruscant" again (any Coruscant location). Plays differently from the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1065 extends Card1250_065 {
    public Card2250_1065() {
        super();
        setGameText("Use 1 Force to deploy up to two clones from Reserve Deck; reshuffle. OR If your clones occupy three related battlegrounds during your control phase, use 2 Force (free if on Coruscant) to retrieve any one card into hand. OR If your clone in battle, add one battle destiny.");
        setLegacyVersion(new LegacyVersion("1250_65", "PC-E1", "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    /** "free if on Coruscant": Coruscant sites only (not the system). */
    @Override
    protected Filter freeRetrievalLocations() {
        return Filters.Coruscant_site;
    }
}
