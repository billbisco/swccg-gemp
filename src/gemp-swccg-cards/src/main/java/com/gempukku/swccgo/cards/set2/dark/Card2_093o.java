package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: IT-O (Eyetee-Oh)
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[IT-O (Eyetee-Oh)]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[IT-O (Eyetee-Oh) (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_093) and differs only in its game text and text-state information.
 */
public class Card2_093o extends Card2_093 {
    public Card2_093o() {
        super();
        setGameText("When at Detention Block Corridor during the control phase, your Force drains there are +X where X = the number of captives present. Immune to Restraining Bolt.");
        setTextState(new TextState("2_93", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
