package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Local Trouble
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Local Trouble]]; reworded by [[Local Trouble (PC Errata)]]
 * (Players Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not known)).
 * "Any two Stormtroopers in the Cantina" battle for you, so they are your Stormtroopers, as the errata says.
 * So this state reuses the current card's (Card1_256) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_256o extends Card1_256 {
    public Card1_256o() {
        super();
        setGameText("Use 1 Force at the beginning of your battle phase to allow any two Stormtroopers in the Cantina to battle any one opponent's character (your choice). You may add one battle destiny. No other battles may occur in Cantina this turn.");
        setTextState(new TextState("1_256", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
