package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_092;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Droid
 * Title: C-3PO (See-Threepio)
 *
 * PC-E1 version of 1210_92: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1092 extends Card1210_092 {
    public Card2210_1092() {
        super();
        setGameText("During a battle at a site with R2-D2, may not be targeted by weapons, and may draw one destiny; subtract that amount from opponent's total power. Once per game, may place all your non-[Immune to Alter] Effects on table in Used Pile.");
        setLegacyVersion(new LegacyVersion("1210_92", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
