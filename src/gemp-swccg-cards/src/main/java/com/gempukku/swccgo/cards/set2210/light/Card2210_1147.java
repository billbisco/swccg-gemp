package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_147;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Beru Stew
 *
 * PC-E1 version of 1210_147: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1147 extends Card1210_147 {
    public Card2210_1147() {
        super();
        setGameText("USED: Activate 1 Force (2 if a Lars on table). OR During battle at a farm, add the destiny number of your device on this farm to your total power. LOST: During battle at a farm, after battle destinies are drawn, place a 'crop' out of play to make attrition against you = 0.");
        setLegacyVersion(new LegacyVersion("1210_147", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
