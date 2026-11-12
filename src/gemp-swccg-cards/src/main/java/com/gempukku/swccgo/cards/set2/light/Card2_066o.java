package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Sandcrawler: Loading Bay
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Sandcrawler: Loading Bay]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Sandcrawler: Loading Bay (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_066) and differs only in its game text and text-state information.
 */
public class Card2_066o extends Card2_066 {
    public Card2_066o() {
        super();
        setLocationLightSideGameText("Deploy on your Sandcrawler. Jawas forfeit +2 here. \"Nighttime conditions\" here.");
        setLocationDarkSideGameText("Your characters may enter/exit here for 1 Force each. \"Nighttime conditions\" here.");
        setTextState(new TextState("2_66", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
