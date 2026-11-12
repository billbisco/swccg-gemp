package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Character
 * Title: Obi-Wan Kenobi
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Obi-Wan Kenobi]]; reworded by [[Obi-Wan Kenobi (Errata)]]
 * (Decipher Glossary Version 2.0 (November 1998)).
 * "When present at the start of a battle" is the current "when a battle was just initiated where present".
 * So this state reuses the current card's (Card1_021) game text code and differs only in its text and text-state
 * information.
 */
public class Card1_021o extends Card1_021 {
    public Card1_021o() {
        super();
        setGameText("When present at the start of a battle, may use 1 Force to cause one opponent's character of ability = 1 (your choice) to move away (for free), or that character is immediately lost. Immune to attrition < 5.");
        setTextState(new TextState("1_21", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }
}
