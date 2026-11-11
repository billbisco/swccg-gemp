package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_071;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Droid
 * Title: Lars' Protocol Droid
 *
 * PC-E2 version of 1240_71: the PC card image (larsprotocoldroid.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2071 extends Card1240_071 {
    public Card2240_2071() {
        super();
        setGameText("'Device' on Harvest may be treated as '[A New Hope] or [Hoth] droid.' While at a farm: your characters present are forfeit +3; may raise this site (if converted) to the top; and, once per move phase, may put hand in Used Pile to place a 'crop' on Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1240_71", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
