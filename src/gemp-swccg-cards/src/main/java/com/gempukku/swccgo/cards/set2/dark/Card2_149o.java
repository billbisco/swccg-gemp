package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Sandcrawler: Droid Junkheap
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Sandcrawler: Droid Junkheap]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Sandcrawler: Droid Junkheap (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_149) and differs only in its game text and text-state information.
 */
public class Card2_149o extends Card2_149 {
    public Card2_149o() {
        super();
        setLocationLightSideGameText("Your characters may enter/exit here for 1 Force each. \"Nighttime conditions\" here.");
        setLocationDarkSideGameText("Deploy on your Sandcrawler. Jawas forfeit +2 here. \"Nighttime conditions\" here.");
        setTextState(new TextState("2_149", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
