package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Ng'ok
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Ng'ok]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Ng'ok (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_136) and differs only in its game text and text-state information.
 */
public class Card2_136o extends Card2_136 {
    public Card2_136o() {
        super();
        setGameText("If opponent just deployed four or more characters at same location this turn, prevent all of those characters from battling this turn. OR If opponent just 'reacted' to a battle, cancel the battle.");
        setTextState(new TextState("2_136", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
