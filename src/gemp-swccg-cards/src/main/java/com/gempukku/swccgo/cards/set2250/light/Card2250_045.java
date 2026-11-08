package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_163;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Mace Windu (V)
 * Original version of 1250_45: the Virtual Block 5 release sheet text ("During battle, Mace may fire one weapon
 * twice"; the PC card list, PC-E1, says "he may fire one weapon twice per battle"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_045 extends Card601_163 {
    public Card2250_045() {
        setGameText("Mace's game text may not be canceled (except at Senate). During battle, Mace may fire one weapon twice. Boba Fett may not add battle destiny draws here. Immune to attrition.");
        setLegacyVersion(new LegacyVersion("1250_45", "",
                "Virtual Block 5 release sheet (2009-12-07), card #44E", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
