package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_065;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: •Aved Luun (V)
 * Original version of 1230_65 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the Virtual Block 3 release sheet (2009-12-07), card #65.
 * The PC website card list (PC-E1, by 2012-07-21, 2230_1065) changed it; the Virtual Cards Master List (official text
 * 2013-04-27, PC-E2, the current card) prints these words again, so this version has the same text and play as the
 * current card. Id 2230_65 was retired on 2026-11-08 (it aliases the current card in blueprintMapping), so this original takes 2230_2065. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2065 extends Card1230_065 {
    public Card2230_2065() {
        super();
        setLegacyVersion(new LegacyVersion("1230_65", "",
                "Virtual Block 3 release sheet (2009-12-07), card #65; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
