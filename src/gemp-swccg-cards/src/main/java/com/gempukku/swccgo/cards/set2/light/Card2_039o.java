package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: They're On Dantooine
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[They're On Dantooine]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[They're On Dantooine (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_039) and differs only in its game text and text-state information.
 */
public class Card2_039o extends Card2_039 {
    public Card2_039o() {
        super();
        setGameText("Once per game, use 2 Force to deploy on Dantooine. Target Death Star. Opponent may not play Commence Primary Ignition until target reaches Dantooine, canceling Utinni Effect.");
        setTextState(new TextState("2_39", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
