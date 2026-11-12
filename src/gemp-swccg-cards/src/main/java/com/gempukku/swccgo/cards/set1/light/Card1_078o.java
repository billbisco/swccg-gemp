package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Droid Shutdown
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Droid Shutdown]]; reworded by [[Droid Shutdown (PC Errata)]]
 * (Players Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not known)).
 * Canceling "any attempt to steal, destroy or capture one of your droids" is the current card's cancel of an attempt to
 * steal, 'hit', lose or capture your droid.
 * So this state reuses the current card's (Card1_078) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_078o extends Card1_078 {
    public Card1_078o() {
        super();
        setGameText("Cancel any attempt to steal, destroy or capture one of your droids. Droid is protected for remainder of this turn.");
        setTextState(new TextState("1_78", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
