package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Clak'dor VII
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Clak'dor VII]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Clak'dor VII (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_060) and differs only in its game text and text-state information.
 */
public class Card2_060o extends Card2_060 {
    public Card2_060o() {
        super();
        setLocationLightSideGameText("If you occupy, all Bith characters are destiny +2.");
        setLocationDarkSideGameText("If you control, all Bith characters are destiny -1 and Ghhhk is power +2 in dejarik battles.");
        setTextState(new TextState("2_60", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
