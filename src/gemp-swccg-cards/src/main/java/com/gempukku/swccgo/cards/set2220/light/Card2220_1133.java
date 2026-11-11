package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_133;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: •Rebel Infantry
 * PC-E1 version of 1220_133 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2220_133) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2220_3133), which prints the original words again.
 * Against the original: one change: "a" is added before "Blaster Rifle".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1133 extends Card1220_133 {
    public Card2220_1133() {
        super();
        setGameText("Deploy on table. Rebel scouts (except pilots) are troopers. Rebel troopers have armor = 4 and are immune to Physical Choke. Whenever you deploy a Rebel trooper, may immediately [download] a Blaster Rifle on that character. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1220_133", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
