package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: •Bib Fortuna (V)
 * PC-E1 version of 1230_2 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2230_2002) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2230_3002), which prints the original words again.
 * Against the original: one change: "you" is added ("you may place").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1002 extends Card1230_002 {
    public Card2230_1002() {
        super();
        setGameText("Opponent's non-Jedi characters are deploy +1 to [Jabba's Palace] sites. Once during your control phase, you may place three cards from hand in Used Pile to reveal top card of Reserve Deck; if it is an alien, take it into hand.");
        setLegacyVersion(new LegacyVersion("1230_2", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
