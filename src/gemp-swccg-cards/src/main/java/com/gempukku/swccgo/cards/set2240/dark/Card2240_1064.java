package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_064;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Weapon
 * Title: Galen's Lightsaber
 * PC-E1 version of 1240_64: the PC website card list text (by Jul 2012), "If total destiny > defense value, target hit, ...",
 * still naming Black Leader (Juno Eclipse). The card list spells "atttition"; corrected here. The Virtual Cards Master List
 * (Apr 2013, PC-E2) is the current text. Plays the same as the current card; only the wording differs. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2240_1064 extends Card1240_064 {
    public Card2240_1064() {
        super();
        setGameText("Deploy on Galen. While Galen defending a battle, his and Black Leader's immunity to attrition is +2. May target a character or creature for free. Draw two destiny. If total destiny > defense value, target hit, power -3, and may not be used to satisfy attrition.");
        setLegacyVersion(new LegacyVersion("1240_64", "PC-E1",
                "PC website card list (S1, seen 2012-07-21 .. 2013-01-11; date is an upper bound)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
