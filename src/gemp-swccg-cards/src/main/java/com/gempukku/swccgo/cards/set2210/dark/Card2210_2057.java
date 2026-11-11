package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_057;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Emergency Deployment (V)
 * Original version of 1210_57 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the Virtual Block 1 release sheet (2009-12-07), card #57.
 * The PC website card list (PC-E1, by 2012-07-21, 2210_1057) changed it; the Virtual Cards Master List (official text
 * 2013-04-27, PC-E2, the current card) prints these words again, so this version has the same text and play as the
 * current card. Id 2210_57 was retired on 2026-11-08 (it aliases the current card in blueprintMapping), so this original takes 2210_2057. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2057 extends Card1210_057 {
    public Card2210_2057() {
        super();
        setLegacyVersion(new LegacyVersion("1210_57", "",
                "Virtual Block 1 release sheet (2009-12-07), card #57; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
