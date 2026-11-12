package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: You Do Have Your Moments
 * Text state: o (as printed, April 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[You Do Have Your Moments]] (Decipher archive scan); the current text is [[You Do Have Your Moments (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). GEMP already plays the PC errata here (it plays when the initial attrition is calculated, at the start of the damage segment); the printed wording is not modelled separately, so this state reuses
 * the current card's game text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card4_074o extends Card4_074 {
    public Card4_074o() {
        super();
        setGameText("During a battle, before any cards have been forfeited, cause all attrition for both sides to be reduced to zero.");
        setTextState(new TextState("4_74", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)));
    }
}
