package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: All Too Easy
 * Text state: o (as printed, November 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[All Too Easy]] (Decipher archive scan); the current text is [[All Too Easy (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The bare card still plays the printed timing (it deploys after the character was just 'hit' by a lightsaber);
 * the PC errata's "is about to be 'hit'" is not implemented yet, so this state reuses that code.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card5_112o extends Card5_112 {
    public Card5_112o() {
        super();
        setGameText("If an opponent's character at the Carbonite Chamber was just 'hit' by a lightsaber, deploy on that character. Character is instead immediately captured and 'frozen.' If captive released, lose Immediate Effect. (Immune to Control.)");
        setTextState(new TextState("5_112", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
