package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Rogue Bantha
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Rogue Bantha]] (Decipher archive scan); superseded by the Players Committee errata, wiki
 * [[Rogue Bantha (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP (a battle at the Bantha's site is at an exterior site,
 * and "at start of battle" is when the battle was just initiated), so this state reuses the bare card's game text code
 * (Card2_076) and differs only in its game text and text-state information.
 */
public class Card2_076o extends Card2_076 {
    public Card2_076o() {
        super();
        setGameText("May carry 2 passengers. Bantha ability = ½. May move as a ‘react.’ At start of battle at same exterior site, one Tusken Raider present (your choice) is ‘trampled’ (immediately lost).");
        setTextState(new TextState("2_76", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
