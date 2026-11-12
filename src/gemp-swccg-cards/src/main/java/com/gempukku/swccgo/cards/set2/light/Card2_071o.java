package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Red 5
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Red 5]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Red 5 (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_071) and differs only in its game text and text-state information.
 */
public class Card2_071o extends Card2_071 {
    public Card2_071o() {
        super();
        setGameText("May add 1 pilot and 1 astromech. Immune to attrition < 4 if Luke piloting. Must have pilot aboard to use power, maneuver or hyperspeed. When firing in an Attack Run, adds 1 to total.");
        setTextState(new TextState("2_71", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
