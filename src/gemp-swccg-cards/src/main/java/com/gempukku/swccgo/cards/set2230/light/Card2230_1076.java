package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_076;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Rebel
 * Title: Master Luke
 *
 * PC-E1 version of 1230_76: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2230_1076 extends Card1230_076 {
    public Card2230_1076() {
        super();
        setGameText("Adds 2 to power of anything he pilots. During battle, may use 2 Force to place an opponent's alien (except Jabba or a Rep) present with Luke in owner's Force Pile (opponent may use or lose 3 Force instead). Immune to attrition < 4 (< 7 if with an alien leader).");
        setLegacyVersion(new LegacyVersion("1230_76", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
