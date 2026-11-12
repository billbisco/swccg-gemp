package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Crash Site Memorial
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Crash Site Memorial]]; reworded by [[Crash Site Memorial (PC
 * Errata)]] (Players Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not
 * known)).
 * Placing a just-lost vehicle, droid, weapon or device here and swapping one with a hand card once during your deploy
 * phase is the current card's behaviour.
 * So this state reuses the current card's (Card1_045) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_045o extends Card1_045 {
    public Card1_045o() {
        super();
        setGameText("Deploy on your side of table. You may place lost vehicles, droids, weapons and devices here instead of Lost Pile. Once each turn, during your deploy phase, you may exchange any one hand card with one card from your 'memorial'.");
        setTextState(new TextState("1_45", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
