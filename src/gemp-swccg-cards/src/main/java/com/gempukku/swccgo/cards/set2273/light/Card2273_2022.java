package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_022;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Title: •Dack Ralter (V)
 * Original version of 1273_22 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the Virtual Block 7.3 release sheet (2011-06-03).
 * The PC website card list (PC-E1, by 2012-07-21, 2273_1022) changed it; the Virtual Cards Master List (official text
 * 2013-04-27, PC-E2, the current card) prints these words again, so this version has the same text and play as the
 * current card. Id 2273_22 was retired on 2026-11-08 (it aliases the current card in blueprintMapping), so this original takes 2273_2022. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2022 extends Card1273_022 {
    public Card2273_2022() {
        super();
        setLegacyVersion(new LegacyVersion("1273_22", "",
                "Virtual Block 7.3 release sheet (2011-06-03); superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21)));
    }
}
