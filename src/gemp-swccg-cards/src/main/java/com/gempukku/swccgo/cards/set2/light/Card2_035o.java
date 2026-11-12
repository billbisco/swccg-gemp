package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Luke's Cape
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Luke's Cape]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Luke's Cape (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_035) and differs only in its game text and text-state information.
 */
public class Card2_035o extends Card2_035 {
    public Card2_035o() {
        super();
        setGameText("Use 2 Force to deploy on a Rebel or alien. Immune to attrition < 3. Also, you may activate 1 extra Force each turn if Luke's Cape is present at any non-Tatooine location.");
        setTextState(new TextState("2_35", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }
}
