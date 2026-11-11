package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_127;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Leia Of Alderaan
 *
 * PC-E1 version of 1220_127: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1127 extends Card1220_127 {
    public Card2220_1127() {
        super();
        setGameText("Deploy on Leia. At same location, your unique (•) Rebels are deploy -1 and their immunity to attrition is +1. Dark side Republic characters are deploy +2 and forfeit -2. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1220_127", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
