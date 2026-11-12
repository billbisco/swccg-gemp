package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Lirin Car'n
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Lirin Car'n]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Lirin Car'n (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_094) and differs only in its game text and text-state information.
 */
public class Card2_094o extends Card2_094 {
    public Card2_094o() {
        super();
        setGameText("When at same site as other musicians, Lirin collects a \"cover charge.\" For each other musician present, add 1 to the 'cost' for any character to move or deploy to same site.");
        setTextState(new TextState("2_94", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
