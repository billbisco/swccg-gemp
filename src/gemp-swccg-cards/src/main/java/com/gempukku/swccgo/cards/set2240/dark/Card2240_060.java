package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_060;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Starship
 * Title: Katana
 * Original version of 1240_60. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at
 * its server upload date): original (2240_60) = the Virtual Block 4 release sheet (2009-12-07), card #59; PC-E1
 * (2240_1060) = the PC website card list (by 2012-07-21): two changes: the comma after “6 passengers” is dropped (“6
 * passengers, and 4 TIEs” becomes “6 passengers and 4 TIEs”), and “Heroic, and immune to attrition < 4” is written
 * “Heroic, and Immune to attrition < 4”, with a capital I. PC-E2 (2240_2060) = the PC card image (uploaded
 * 2013-02-25; it may have been made earlier), the original words again; PC-E3 = the Master List (2013-04-27), the
 * original words again, the current card. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_060 extends Card1240_060 {
    public Card2240_060() {
        super();
        setGameText("May add 3 pilots, 6 passengers, and 4 TIEs. Your other Dreadnaughts here are power +1, forfeit +2, Heroic, and immune to attrition < 4. Permanent pilot provides ability of 3. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1240_60", "",
                "Virtual Block 4 release sheet (2009-12-07), card #59; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
