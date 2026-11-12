package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Gravel Storm
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Gravel Storm]]; reworded by [[Gravel Storm (PC Errata)]] (Players
 * Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not known)).
 * "Target one opponent's character at that site" targets the same characters as "present at that site".
 * So this state reuses the current card's (Card1_247) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_247o extends Card1_247 {
    public Card1_247o() {
        super();
        setGameText("Strikes at Dune Sea, Jundland Wastes, Beggar's Canyon, Lars' Moisture Farm, Jawa Camp, or Mos Eisley. Target one opponent's character at that site. Draw destiny. Target lost if destiny > ability.");
        setTextState(new TextState("1_247", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
