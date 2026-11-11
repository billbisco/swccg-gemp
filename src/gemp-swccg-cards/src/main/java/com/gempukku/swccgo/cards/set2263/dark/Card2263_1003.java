package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Title: •IG-88, Renegade Droid
 * PC-E1 version of 1263_3 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2263_2003) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2263_3003), which prints the original words again.
 * Against the original: one change: "a" is dropped ("may draw destiny").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_1003 extends Card601_008 {
    public Card2263_1003() {
        super();
        setGameText("[Pilot] 2, 3: IG-2000. Deploys -3 aboard IG-2000. Adds one battle destiny if alone or with a smuggler. During battle, if piloting a starship, may draw destiny. Starship is immune to attrition < X, or its immunity is +X (limit +2), where X = destiny.");
        setLegacyVersion(new LegacyVersion("1263_3", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
