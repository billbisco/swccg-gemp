package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_028;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Empire's New Order
 *
 * PC-E2 version of 1230_28: the PC card image (empiresneworder.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2028 extends Card1230_028 {
    public Card2230_2028() {
        super();
        setGameText("Deploy on table. Whenever an opponent's alien is just lost, they lose 1 Force. While all of your ability on table is provided by Imperials and Imperial starship pilots, Emperor, your admirals, generals, and moffs are deploy -1 and are forfeit +1. Aliens are deploy +1 and forfeit -1.");
        setLegacyVersion(new LegacyVersion("1230_28", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
