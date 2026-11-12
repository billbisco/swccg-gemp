package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Interrupt
 * Title: Nevar Yalnal
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Nevar Yalnal]]; reworded by [[Nevar Yalnal (PC Errata)]] (Players
 * Committee errata (AR 2023 Appendix A; date is an upper bound, the first PC errata date is not known)).
 * "Lost if destiny > 2; if Undercover, lost if destiny > 0" is the same test as the current "add 2 if Undercover; lost
 * if total destiny > 2".
 * So this state reuses the current card's (Card1_261) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_261o extends Card1_261 {
    public Card1_261o() {
        super();
        setGameText("If both players have a spy at the same site, draw destiny. Opponent's spy is lost if destiny > 2. If spy is Undercover, it is lost if destiny > 0.");
        setTextState(new TextState("1_261", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
