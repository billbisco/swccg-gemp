package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: Reflection
 * Text state: o (as printed, April 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Reflection]] (Decipher archive scan); the current text is [[Reflection (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). GEMP already plays the PC errata here (it retrieves 1 Force); the printed wording is not modelled separately, so this state reuses
 * the current card's game text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card4_033o extends Card4_033 {
    public Card4_033o() {
        super();
        setGameText("Deploy on Leia. Once during each of your control phases, you may use 3 Force to retrieve one card from your Lost Pile. Also, when an apprentice, adds 1 to her training destiny.");
        setTextState(new TextState("4_33", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)));
    }
}
