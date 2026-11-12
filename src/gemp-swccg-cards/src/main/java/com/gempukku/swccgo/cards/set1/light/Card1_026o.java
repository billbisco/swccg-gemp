package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Character
 * Title: Rebel Guard
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Rebel Guard]]; reworded by [[Rebel Guard (PC Errata)]] (Players
 * Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not known)).
 * The printed power "*" (0 in an offensive battle, 4 when defending) is the current power 0 with power +4 when
 * defending.
 * So this state reuses the current card's (Card1_026) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_026o extends Card1_026 {
    public Card1_026o() {
        super();
        setGameText("Cannot move. * Power = 0 during an offensive battle, but Power = 4 when defending a battle.");
        setTextState(new TextState("1_26", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
