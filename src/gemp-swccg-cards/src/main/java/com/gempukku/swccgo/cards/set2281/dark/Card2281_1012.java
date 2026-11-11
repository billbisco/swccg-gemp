package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: •On The Hunt
 * PC-E1 version of 1281_12 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2281_2012) and before the Virtual Cards Master List (2013-04-27, PC-E2, 2281_3012), which prints the original words again.
 * Against the original: two changes: a comma is added after "Once per game", and "the" is added ("At the end of"). The list writes the set icon as "(Virtual Block 8)".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1012 extends Card1281_012 {
    public Card2281_1012() {
        super();
        setGameText("Deploy on Contract Killers. Assassins are defense value +1. Once per game, may retrieve [Virtual Block 8] Death Mark. At the end of opponent's turn, if their character targeted by [Virtual Block 8] Death Mark is at a non-battleground or aboard their starship, opponent loses 3 Force. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_12", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
