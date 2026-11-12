package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Moment Of Triumph
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Moment Of Triumph]]; reworded by [[Moment Of Triumph (PC
 * Errata)]] (Players Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not
 * known)).
 * The alien defending a battle alone is the player's own alien; the errata only says "your".
 * So this state reuses the current card's (Card1_260) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_260o extends Card1_260 {
    public Card1_260o() {
        super();
        setGameText("If Tarkin is defending a battle alone, add two battle destiny. OR If any alien is defending a battle alone, add one battle destiny.");
        setTextState(new TextState("1_260", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
