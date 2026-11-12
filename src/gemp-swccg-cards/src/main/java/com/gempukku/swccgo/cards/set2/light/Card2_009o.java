package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Het Nkik
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Het Nkik]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Het Nkik (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_009) and differs only in its game text and text-state information.
 */
public class Card2_009o extends Card2_009 {
    public Card2_009o() {
        super();
        setGameText("Het is power +1 for each Stormtrooper at same site, unless Reegesk is present. *To deploy, requires 2 Force from each player's Force Pile. Cannot deploy otherwise. Deploy only on Tatooine, but may move elsewhere.");
        setTextState(new TextState("2_9", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
