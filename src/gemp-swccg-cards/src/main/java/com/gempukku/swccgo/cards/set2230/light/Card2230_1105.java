package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_105;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Ke Chu Ke Kukuta? (V)
 * PC-E1 version of 1230_105 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2230_2105) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2230_3105), which prints the original words again.
 * Against the original: one change: "you" is added ("you may use").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1105 extends Card1230_105 {
    public Card2230_1105() {
        super();
        setGameText("If you have an information broker on table, draw destiny. If destiny > the number of opponent's characters on table, you may use 1 Force to [upload] any one card.");
        setLegacyVersion(new LegacyVersion("1230_105", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
