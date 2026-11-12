package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Monnok
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Monnok]] (Decipher archive scan); superseded by Decipher errata, wiki [[Monnok (Errata)]] (Glossary; upper bound November 1998).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_135) and differs only in its game text and text-state information.
 */
public class Card2_135o extends Card2_135 {
    public Card2_135o() {
        super();
        setGameText("USED: If opponent has 13 or more cards in hand, place all but 8 (random selection) in Used Pile. LOST: Use 4 Force to cause opponent to reveal hand. All cards opponent has two or more of are lost.");
        setTextState(new TextState("2_135", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(1998, 11, 1)));
    }
}
