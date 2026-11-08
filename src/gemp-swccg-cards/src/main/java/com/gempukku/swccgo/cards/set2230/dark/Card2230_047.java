package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_047;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Scout Recon (V)
 * Original version of 1230_47: the Virtual Block 3 release sheet text (Dec 2009), later reworded (undated PC card JSON). Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_047 extends Card1230_047 {
    public Card2230_047() {
        super();
        setGameText("Find one of your missing characters. OR During battle, activate 1 Force for each of your scouts participating. OR During battle, if your Imperial with opponent's alien, your total battle destiny is +3.");
        setLegacyVersion(new LegacyVersion("1230_47", "",
                "Virtual Block 3 release sheet (2009-12-07), card #48", LocalDate.of(2009, 12, 7), LocalDate.of(2014, 2, 4)));
    }
}
