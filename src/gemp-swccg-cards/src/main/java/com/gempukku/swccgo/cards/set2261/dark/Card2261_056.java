package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_056;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Starship
 * Title: Black 1
 * Original version of 1261_56: the Virtual Block 6.1 release sheet text (Sep 2009), naming Black Leader (renamed Juno Eclipse on the 2013 Master List). Black Leader is the Juno persona, so play is the same as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_056 extends Card1261_056 {
    public Card2261_056() {
        setGameText("May add Black Leader as pilot. TIEs are immune to Don't Get Cocky. Once per turn, may use 1 Force to ▲ a Black Squadron TIE. Draws one battle destiny if not able to otherwise. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1261_56", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #56", LocalDate.of(2009, 9, 4), LocalDate.of(2013, 4, 27)));
    }
}
