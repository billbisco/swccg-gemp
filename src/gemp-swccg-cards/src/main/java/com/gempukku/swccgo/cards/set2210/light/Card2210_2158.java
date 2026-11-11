package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_158;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Panic (V)
 * Original version of 1210_158 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the
 * Virtual Block 1 release sheet (2009-12-07), card #159. Later versions: PC website card list (PC-E1, by 2012-07-21, 2210_1158), PC card image (PC-E2, on the PC server by 2013-02-25, 2210_3158) and Virtual Cards Master List (PC-E3, 2013-04-27, the current card).
 * The PC card image and the Master List print these words again, so this version has the same text and play as the
 * current card. Id 2210_158 was retired on 2026-11-08 (it aliases the current card in blueprintMapping), so this original takes 2210_2158. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2158 extends Card1210_158 {
    public Card2210_2158() {
        super();
        setLegacyVersion(new LegacyVersion("1210_158", "",
                "Virtual Block 1 release sheet (2009-12-07), card #159; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
