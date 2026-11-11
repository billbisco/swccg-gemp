package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_032;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: •Flagship Operations (V)
 * PC-E1 version of 1230_32 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 3 release sheet original (2230_32) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "you" is added ("you may add").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1032 extends Card1230_032 {
    public Card2230_1032() {
        super();
        setGameText("Deploy on a system. Your TIEs may relocate (as a regular move) to a system from 0 to 3 parsecs away. For each battleground system you control with two TIEs piloted by Imperials, you may add 3 to your total power in battles. Opponent's Force drain is +3 here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1230_32", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
