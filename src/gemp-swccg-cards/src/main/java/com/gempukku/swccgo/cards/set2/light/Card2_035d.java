package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Luke's Cape
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher errata text from wiki.swccg.com [[Luke's Cape (Errata)]] (Glossary 2.0); superseded by the Players Committee errata, wiki [[Luke's Cape (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_035) and differs only in its game text and text-state information.
 */
public class Card2_035d extends Card2_035 {
    public Card2_035d() {
        super();
        setGameText("Use 2 Force to deploy on a Rebel or alien. You may activate 1 extra Force each turn if Luke's Cape is not at a Tatooine location. That character is immune to attrition < 3.");
        setTextState(new TextState("2_35", "D-E1",
                "Decipher Glossary Version 2.0, November 1998",
                LocalDate.of(1998, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
