package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Title: Jabba's Space Cruiser (V)
 * Original version of 1230_55: the Virtual Block 3 release sheet text (Dec 2009), "May add 1 alien pilot and 6 passengers. Permanent pilot provides ability of 2." (the card list swapped the two sentences).
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_055 extends Card601_012 {
    public Card2230_055() {
        super();
        setGameText("Deploys and moves like a starfighter. May add 1 alien pilot and 6 passengers. Permanent pilot provides ability of 2. Your [Independent] starships may deploy here as a 'react.' If Jabba here, immune to attrition.");
        setLegacyVersion(new LegacyVersion("1230_55", "",
                "Virtual Block 3 release sheet (2009-12-07), card #55E", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
