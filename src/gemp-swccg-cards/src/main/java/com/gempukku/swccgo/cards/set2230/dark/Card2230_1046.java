package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_046;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: •Outflank (V)
 * PC-E1 version of 1230_46 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 3 release sheet original (2230_46) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "you" is added ("you may use").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1046 extends Card1230_046 {
    public Card2230_1046() {
        super();
        setGameText("If opponent just initiated battle at a site where their total power is greater than yours, you may use 1 Force (or cause opponent to use 1 Force) to relocate one of your characters or vehicles to that site from a related location.");
        setLegacyVersion(new LegacyVersion("1230_46", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
