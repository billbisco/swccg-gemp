package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_060;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Starship
 * Title: Katana
 * PC-E1 version of 1240_60: the PC website card list text (by 2012-07-21; Virtual Block 4 list and Virtual master
 * list to 2013-01-11). Against the original (2240_60): two changes: the comma after “6 passengers” is dropped (“6
 * passengers, and 4 TIEs” becomes “6 passengers and 4 TIEs”), and “Heroic, and immune to attrition < 4” is written
 * “Heroic, and Immune to attrition < 4”, with a capital I. The Master List of 2013-04-27 (PC-E2, the current card)
 * prints the original words again (swccg-guild/work#236; Bill 2026-11-11: every publication is a version). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_1060 extends Card1240_060 {
    public Card2240_1060() {
        super();
        setGameText("May add 3 pilots, 6 passengers and 4 TIEs. Your other Dreadnaughts here are power +1, forfeit +2, Heroic, and Immune to attrition < 4. Permanent pilot provides ability of 3. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1240_60", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
