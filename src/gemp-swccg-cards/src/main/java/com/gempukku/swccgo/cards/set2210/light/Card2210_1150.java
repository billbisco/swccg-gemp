package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set601.light.Card601_241;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Don't Tread On Me (V)
 * PC-E1 version of 1210_150 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 1 release sheet original (2210_2150) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "on" is added ("or on your side of table").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1150 extends Card601_241 {
    public Card2210_1150() {
        super();
        setGameText("USED: Use 1 Force to [download] an Effect that deploys for free on table (or on your side of table) and is always immune to Alter. STARTING: When drawing your starting hand, draw up to 12 cards instead of 8. Place this Interrupt in Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1210_150", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
