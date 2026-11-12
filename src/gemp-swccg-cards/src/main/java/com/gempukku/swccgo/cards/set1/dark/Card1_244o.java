package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Emergency Deployment
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Emergency Deployment]]; reworded by [[Emergency Deployment (PC
 * Errata)]] (Players Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not
 * known)).
 * "Draw up to 3 cards from your Reserve Deck" (to deploy from) is the current card's "reveal up to 3 cards".
 * So this state reuses the current card's (Card1_244) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_244o extends Card1_244 {
    public Card1_244o() {
        super();
        setGameText("If opponent just initiated a battle where opponent has more than double your power, draw up to 3 cards from your Reserve Deck. Of those 3, deploy anywhere (for free) any characters, starships, vehicles, devices or weapons. Any others are lost.");
        setTextState(new TextState("1_244", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
