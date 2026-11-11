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
 * PC-E1 version of 1240_71: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2240_1071 extends Card1240_071 {
    public Card2240_1071() {
        super();
        setGameText("'Device' on Harvest may be treated as '[A New Hope] or [Hoth] droid'. While at a farm: your characters present are forfeit +3; may raise this site (if converted) to the top; and, once per move phase, may put hand in Used Pile to place a 'crop' on Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1240_71", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
