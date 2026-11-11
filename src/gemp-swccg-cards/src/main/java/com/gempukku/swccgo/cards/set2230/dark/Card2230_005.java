package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_005;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: •Fozec (V)
 * Original version of 1230_5 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the Virtual Block 3 release sheet (2009-12-07), card #05.
 * The PC website card list (PC-E1, by 2012-07-21, 2230_1005) changed it; the Virtual Cards Master List (official text
 * 2013-04-27, PC-E2, the current card) prints these words again, so this version has the same text and play as the
 * current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_005 extends Card1230_005 {
    public Card2230_005() {
        super();
        setLegacyVersion(new LegacyVersion("1230_5", "",
                "Virtual Block 3 release sheet (2009-12-07), card #05; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
