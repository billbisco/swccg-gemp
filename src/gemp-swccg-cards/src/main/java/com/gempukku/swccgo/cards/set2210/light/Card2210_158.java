package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_158;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Panic (V)
 * Original version of 1210_158 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the Virtual Block 1 release sheet (2009-12-07), card #159.
 * The PC website card list (PC-E1, by 2012-07-21, 2210_1158) changed it; the Virtual Cards Master List (official text
 * 2013-04-27, PC-E2, the current card) prints these words again, so this version has the same text and play as the
 * current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_158 extends Card1210_158 {
    public Card2210_158() {
        super();
        setLegacyVersion(new LegacyVersion("1210_158", "",
                "Virtual Block 1 release sheet (2009-12-07), card #159; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
