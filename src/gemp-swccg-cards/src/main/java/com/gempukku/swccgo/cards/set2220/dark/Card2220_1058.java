package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_198;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Defensive Fire (V)
 *
 * PC-E1 version of 1220_58: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1058 extends Card601_198 {
    public Card2220_1058() {
        super();
        setGameText("If opponent just initiated battle, you may immediately fire one non-lightsaber, non-Permanent weapon present in that battle once (for free), adding 2 to each of its destiny draws. That weapon may fire again this battle.");
        setLegacyVersion(new LegacyVersion("1220_58", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
