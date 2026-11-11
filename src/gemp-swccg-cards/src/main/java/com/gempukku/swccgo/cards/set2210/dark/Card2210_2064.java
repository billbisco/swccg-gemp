package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_064;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Look Sir, Droids (V)
 * Original version of 1210_64 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the
 * Virtual Block 1 release sheet (2009-12-07), card #64. Later versions: PC website card list (PC-E1, by 2012-07-21, 2210_1064), PC card image (PC-E2, on the PC server by 2013-02-25, 2210_3064) and Virtual Cards Master List (PC-E3, 2013-04-27, the current card).
 * The PC card image and the Master List print these words again, so this version has the same text and play as the
 * current card. Id 2210_64 was retired on 2026-11-08 (it aliases the current card in blueprintMapping), so this original takes 2210_2064. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2064 extends Card1210_064 {
    public Card2210_2064() {
        super();
        setLegacyVersion(new LegacyVersion("1210_64", "",
                "Virtual Block 1 release sheet (2009-12-07), card #64; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
