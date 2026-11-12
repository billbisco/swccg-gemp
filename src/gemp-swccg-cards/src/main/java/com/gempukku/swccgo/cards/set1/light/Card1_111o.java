package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Solo Han
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Solo Han]]; reworded by [[Solo Han (PC Errata)]] (Players
 * Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not known)).
 * The alien defending a battle alone is the player's own alien; the errata only says "your".
 * So this state reuses the current card's (Card1_111) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_111o extends Card1_111 {
    public Card1_111o() {
        super();
        setGameText("If Han is defending a battle alone at a site, add two battle destiny. OR If any alien is defending a battle alone at a site, add one battle destiny.");
        setTextState(new TextState("1_111", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
