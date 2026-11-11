package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_047;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Interrupt
 * Subtype: Used
 * Title: Unexpected Interruption (V)
 *
 * PC-E1 version of 1240_47: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2240_1047 extends Card1240_047 {
    public Card2240_1047() {
        super();
        setGameText("If a battle was just initiated, target an opponent's female non-droid character present with Xizor. Draw destiny. Target excluded from battle and adds her power to Xizor's for remainder of turn if destiny +2 > target's ability.");
        setLegacyVersion(new LegacyVersion("1240_47", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
