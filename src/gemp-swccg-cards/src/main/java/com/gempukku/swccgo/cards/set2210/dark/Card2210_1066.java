package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_201;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Surface Defense (V)
 * PC-E1 version of 1210_66 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2210_66) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2210_3066), which prints the original words again.
 * Against the original: one change: "the" is added ("your side of the table").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1066 extends Card601_201 {
    public Card2210_1066() {
        super();
        setGameText("USED: Use 1 Force to [download] an Effect that deploys for free on table (or your side of the table) and is always immune to Alter. STARTING: When drawing your starting hand, draw up to 12 cards instead of 8. Place this Interrupt in Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1210_66", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
