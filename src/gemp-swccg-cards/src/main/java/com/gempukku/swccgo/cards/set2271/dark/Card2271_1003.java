package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_182;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Character
 * Title: •DS-61-5
 * PC-E1 version of 1271_3 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2271_2003) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2271_3003), which prints the original words again.
 * Against the original: one change: "one" is dropped ("any card").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1003 extends Card601_182 {
    public Card2271_1003() {
        super();
        setGameText("[Pilot] 2, 3: Black 5. While piloting Black 5, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any card into hand from Force Pile; reshuffle.");
        setLegacyVersion(new LegacyVersion("1271_3", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
