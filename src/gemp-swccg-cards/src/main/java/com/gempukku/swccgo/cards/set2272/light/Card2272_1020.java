package com.gempukku.swccgo.cards.set2272.light;

import com.gempukku.swccgo.cards.set1272.light.Card1272_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: •Declaration Of Rebellion
 * PC-E1 version of 1272_20 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2272_2020) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2272_3020), which prints the original words again.
 * Against the original: two changes: a comma is added after "Once per game", and "one" is added before "K'lor'slug". The list writes the ▲ as "/\\".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2272_1020 extends Card1272_020 {
    public Card2272_1020() {
        super();
        setGameText("Deploy on table. Bail, Leia, and Mon Mothma are Insurgents. While an Insurgent present at a battleground, your total battle destiny is +1, and, if with your non-unique character, each may forfeit for 4 when hit. Once per game, may [upload] one K'lor'slug. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1272_20", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
