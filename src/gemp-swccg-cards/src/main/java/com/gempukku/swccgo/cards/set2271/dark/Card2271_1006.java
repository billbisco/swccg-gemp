package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_006;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Character
 * Title: •••Saber Squadron Pilot
 * PC-E1 version of 1271_6 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 7.1 release sheet original (2271_6) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "Force" is dropped ("to that drain").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1006 extends Card1271_006 {
    public Card2271_1006() {
        super();
        setGameText("[Pilot] 2. Matching pilot for Saber Squadron TIE and while piloting it: it may relocate using [Virtual Block 7] Combat Response, adds one destiny to total power only, and may add 1 to Force drains here (opponent loses no more than 2 Force to that drain).");
        setLegacyVersion(new LegacyVersion("1271_6", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
