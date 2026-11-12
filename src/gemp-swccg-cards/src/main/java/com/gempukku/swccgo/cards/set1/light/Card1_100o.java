package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Old Ben
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Old Ben]]; reworded by [[Old Ben (PC Errata)]] (Players Committee
 * errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not known)).
 * "Revives (re-deploy for free) that character back to same site" is the current card's return from the Lost Pile to
 * that site.
 * So this state reuses the current card's (Card1_100) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_100o extends Card1_100 {
    public Card1_100o() {
        super();
        setGameText("Use 1 Force if any of your characters (except Obi-Wan) was just forfeited from a Tatooine site. Mysterious \"crazy wizard\" steps in and revives (re-deploy for free) that character back to same site.");
        setTextState(new TextState("1_100", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
