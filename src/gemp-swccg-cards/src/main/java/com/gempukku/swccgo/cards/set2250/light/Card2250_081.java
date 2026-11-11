package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_170;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Starship
 * Title: Jedi Starfighter
 * Original version of 1250_81: the text of the Virtual Block 5 release sheet (2009-12-07), card #82; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2250_1081) changes it: “Obi-Wan and Plo” is
 * written “Obi-wan and Plo”, with a small w. The Master List of 2013-04-27 prints the original words again; it is
 * listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_081 extends Card601_170 {
    public Card2250_081() {
        super();
        setGameText("May add 1 pilot and 1 astromech. * = pilot's ability. Obi-Wan and Plo are matching pilots and deploy -2 aboard. While a Jedi aboard, power +2 and immune to Lateral Damage and attrition < 5.");
        setLegacyVersion(new LegacyVersion("1250_81", "",
                "Virtual Block 5 release sheet (2009-12-07), card #82; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
