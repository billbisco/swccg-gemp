package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Laser Gate
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Laser Gate]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Laser Gate (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_113) and differs only in its game text and text-state information.
 */
public class Card2_113o extends Card2_113 {
    public Card2_113o() {
        super();
        setGameText("Deploy between any two interior mobile sites. To pass, a character must have (power + ability) > 4 or use a Lift Tube (all other vehicles are blocked). Laser Gate armor = 3; may be targeted by a character weapon from either site.");
        setTextState(new TextState("2_113", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
