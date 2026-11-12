package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Red 2
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Red 2]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Red 2 (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_070) and differs only in its game text and text-state information.
 */
public class Card2_070o extends Card2_070 {
    public Card2_070o() {
        super();
        setGameText("May add 1 pilot and 1 astromech. Immune to attrition < 3 if Wedge piloting. Must have pilot aboard to use power, maneuver or hyperspeed.");
        setTextState(new TextState("2_70", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
