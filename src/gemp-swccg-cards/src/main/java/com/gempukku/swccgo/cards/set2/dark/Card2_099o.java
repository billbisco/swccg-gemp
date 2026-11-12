package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: R2-Q2 (Artoo-Kyootoo)
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[R2-Q2 (Artoo-Kyootoo)]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), wiki [[R2-Q2 (Artoo-Kyootoo) (Errata)]].
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_099) and differs only in its game text and text-state information.
 */
public class Card2_099o extends Card2_099 {
    public Card2_099o() {
        super();
        setGameText("While aboard any starfighter, adds 1 to power, maneuver and hyperspeed. When at a Scomp link during your draw phase, may use 1 Force to look at top three cards of your Reserve Pile.");
        setTextState(new TextState("2_99", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }
}
