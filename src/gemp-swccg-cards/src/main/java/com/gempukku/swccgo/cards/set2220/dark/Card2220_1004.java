package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_004;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Title: Captain Bewil
 * PC-E2 version of 1220_4. Order (Bill 2026-11-10, ruling H1; 2026-11-11 every publication is a version): the
 * September 2009 first printing is lost; PC-E1 (2220_4) = the PC card image (in the vkit copy by 2009-12-09; the 9
 * September 2009 errata), “total Influence”; PC-E2 (2220_1004) = the Virtual Block 2 release sheet (2009-12-07), card
 * #04E, “total influence”; PC-E3 (2220_2004) = the PC website card list (by 2012-07-21), “total Influence”; PC-E4 =
 * the Master List (2013-04-27), “total influence”, the current card. Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2220_1004 extends Card1220_004 {
    public Card2220_1004() {
        super();
        setGameText("Agenda: logistics. While at Conference Room, opponent must lose 3 Force to 'react' away from a battle, and, if your total influence here is > 3, during your turn you may take an Interrupt into hand from Force Pile; reshuffle.");
        setLegacyVersion(new LegacyVersion("1220_4", "PC-E2",
                "Virtual Block 2 release sheet (2009-12-07), card #04E; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
