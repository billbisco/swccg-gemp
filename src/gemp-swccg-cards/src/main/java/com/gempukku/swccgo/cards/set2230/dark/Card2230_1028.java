package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_028;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Empire's New Order
 *
 * PC-E1 version of 1230_28: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2230_1028 extends Card1230_028 {
    public Card2230_1028() {
        super();
        setGameText("Deploy on table. Whenever an opponent's alien is just lost, they lose 1 Force. While all of your ability on table is provided by Imperials and Imperial starship pilots, your admirals, generals, moffs, and Emperor are deploy -1 and are forfeit +1. Aliens are deploy +1 and forfeit -1.");
        setLegacyVersion(new LegacyVersion("1230_28", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
