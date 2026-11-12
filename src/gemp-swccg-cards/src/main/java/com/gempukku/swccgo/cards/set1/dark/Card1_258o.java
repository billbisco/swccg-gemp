package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Lone Warrior
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Lone Warrior]]; reworded by [[Lone Warrior (PC Errata)]] (Players
 * Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not known)).
 * The warrior defending a battle alone is the player's own warrior; the errata only says "your".
 * So this state reuses the current card's (Card1_258) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_258o extends Card1_258 {
    public Card1_258o() {
        super();
        setGameText("If any warrior is defending a battle alone at a site, add one battle destiny. OR If Tagge is defending a battle alone at a site, add two battle destiny.");
        setTextState(new TextState("1_258", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
