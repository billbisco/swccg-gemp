package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_011;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Effect
 * Title: •I'll Take Them Myself
 * Original version of 1271_11 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the Virtual Block 7.1 release sheet (2010-06-18).
 * The PC website card list (PC-E1, by 2012-07-21, 2271_1011) changed it; the Virtual Cards Master List (official text
 * 2013-04-27, PC-E2, the current card) prints these words again, so this version has the same text and play as the
 * current card. Id 2271_11 was retired on 2026-11-08 (it aliases the current card in blueprintMapping), so this original takes 2271_2011. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_2011 extends Card1271_011 {
    public Card2271_2011() {
        super();
        setLegacyVersion(new LegacyVersion("1271_11", "",
                "Virtual Block 7.1 release sheet (2010-06-18); superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21)));
    }
}
