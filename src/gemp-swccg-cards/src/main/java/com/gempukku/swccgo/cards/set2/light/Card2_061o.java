package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Corellia
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Corellia]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Corellia (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_061) and differs only in its game text and text-state information.
 */
public class Card2_061o extends Card2_061 {
    public Card2_061o() {
        super();
        setLocationLightSideGameText("Your Corellian Corvettes and Millennium Falcon may deploy here as a 'react.'");
        setLocationDarkSideGameText("Your starships are hyperspeed +1 when moving from here.");
        setTextState(new TextState("2_61", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
