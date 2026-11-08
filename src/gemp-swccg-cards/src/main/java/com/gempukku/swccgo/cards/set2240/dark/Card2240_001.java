package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_180;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Arica
 * Original version of 1240_1: the Virtual Block 4 release sheet text (Dec 2009, slip 01); the PC website card list
 * (by 2012-07-21) has the current wording ("Immue to Undercover" became "Immune to Undercover"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_001 extends Card601_180 {
    public Card2240_001() {
        super();
        setGameText("Power +1 on Tatooine. Once per game, during your move phase, may relocate Arica to Luke's location. During your control phase, if present at a battleground (and Luke is not), may retrieve 1 Force. Immue to Undercover and attrition < 4.");
        setLegacyVersion(new LegacyVersion("1240_1", "",
                "Virtual Block 4 release sheet (2009-12-07), card #01; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
