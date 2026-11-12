package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Character
 * Title: Imperial Trooper Guard
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Imperial Trooper Guard]]; reworded by [[Imperial Trooper Guard
 * (PC Errata)]] (Players Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not
 * known)).
 * The printed power "*" (0 in an offensive battle, 4 when defending) is the current power 0 with power +4 when
 * defending.
 * So this state reuses the current card's (Card1_181) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_181o extends Card1_181 {
    public Card1_181o() {
        super();
        setGameText("POWER * / Cannot move. *Power = 0 during an offensive battle, but Power = 4 when defending a battle.");
        setTextState(new TextState("1_181", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
