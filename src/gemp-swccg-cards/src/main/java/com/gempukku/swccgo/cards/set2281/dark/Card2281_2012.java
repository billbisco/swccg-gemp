package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: •On The Hunt
 * Original version of 1281_12 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the Virtual Block 8.1 release sheet (2012-05-11).
 * The PC website card list (PC-E1, by 2012-07-21, 2281_1012) changed it; the Virtual Cards Master List (official text
 * 2013-04-27, PC-E2, the current card) prints these words again, so this version has the same text and play as the
 * current card. Id 2281_12 was retired on 2026-11-08 (it aliases the current card in blueprintMapping), so this original takes 2281_2012. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2012 extends Card1281_012 {
    public Card2281_2012() {
        super();
        setLegacyVersion(new LegacyVersion("1281_12", "",
                "Virtual Block 8.1 release sheet (2012-05-11); superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
