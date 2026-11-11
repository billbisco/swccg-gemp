package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_170;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Starship
 * Title: Jedi Starfighter
 * PC-E2 version of 1250_81. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at its
 * server upload date): original (2250_81) = the Virtual Block 5 release sheet (2009-12-07), card #82; PC-E1
 * (2250_1081) = the PC website card list (by 2012-07-21): “Obi-Wan and Plo” is written “Obi-wan and Plo”, with a
 * small w. PC-E2 (2250_2081) = the PC card image (uploaded 2013-02-25; it may have been made earlier), the original
 * words again; PC-E3 = the Master List (2013-04-27), the original words again, the current card. Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2250_2081 extends Card601_170 {
    public Card2250_2081() {
        super();
        setGameText("May add 1 pilot and 1 astromech. * = pilot's ability. Obi-Wan and Plo are matching pilots and deploy -2 aboard. While a Jedi aboard, power +2 and immune to Lateral Damage and attrition < 5.");
        setLegacyVersion(new LegacyVersion("1250_81", "PC-E2",
                "PC card image (server upload 2013-02-25; the original words again); superseded by the Master List (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
