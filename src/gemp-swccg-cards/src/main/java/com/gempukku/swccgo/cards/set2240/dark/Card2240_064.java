package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_064;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Weapon
 * Title: Galen's Lightsaber
 * Original version of 1240_64: the Virtual Block 4 release sheet text (Dec 2009), naming Black Leader (Juno Eclipse, renamed
 * on the Virtual Cards Master List, Apr 2013). Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_064 extends Card1240_064 {
    public Card2240_064() {
        super();
        setGameText("Deploy on Galen. While Galen defending a battle, his and Black Leader's immunity to attrition is +2. May target a character or creature for free. Draw two destiny. Target hit, power -3, and may not be used to satisfy attrition if total destiny > defense value.");
        setLegacyVersion(new LegacyVersion("1240_64", "",
                "Virtual Block 4 release sheet (2009-12-07), card #63", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }
}
