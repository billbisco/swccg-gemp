package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_170;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Starship
 * Title: Jedi Starfighter
 * PC-E1 version of 1250_81: the PC website card list text (by 2012-07-21; Virtual Block 5 list and Virtual master
 * list to 2013-01-11). Against the original (2250_81): “Obi-Wan and Plo” is written “Obi-wan and Plo”, with a small
 * w. The Master List of 2013-04-27 (PC-E2, the current card) prints the original words again (swccg-guild/work#236;
 * Bill 2026-11-11: every publication is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1081 extends Card601_170 {
    public Card2250_1081() {
        super();
        setGameText("May add 1 pilot and 1 astromech. * = pilot's ability. Obi-wan and Plo are matching pilots and deploy -2 aboard. While a Jedi aboard, power +2 and immune to Lateral Damage and attrition < 5.");
        setLegacyVersion(new LegacyVersion("1250_81", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
