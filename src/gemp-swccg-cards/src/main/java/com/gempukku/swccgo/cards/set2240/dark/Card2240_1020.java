package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Dark Reconnaissance
 *
 * PC-E1 version of 1240_20: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2240_1020 extends Card1240_020 {
    public Card2240_1020() {
        super();
        setGameText("Deploy on opponent's location. Your Force drains here cannot be modified or canceled by opponent. Wherever you have two Mistryl, your Force drains are +1. 'Stolen' on Swilla Corey is treated as 'placed in owner's Used Pile' and it may target a weapon instead of a device.");
        setLegacyVersion(new LegacyVersion("1240_20", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
