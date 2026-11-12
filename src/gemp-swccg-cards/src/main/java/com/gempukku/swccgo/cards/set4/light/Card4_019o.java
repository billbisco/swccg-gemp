package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: At Peace
 * Text state: o (as printed, April 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[At Peace]] (Decipher archive scan); the current text is [[At Peace (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card4_019o extends Card4_019 {
    public Card4_019o() {
        super();
        setGameText("Deploy on an apprentice at the beginning of your turn. Apprentice 'rests' (may not attempt Jedi Tests) until end of your next turn; then relocate Immediate Effect to Jedi Test. When attempting this test, that apprentice adds 3 to training destiny. (Immune to Control.)");
        setTextState(new TextState("4_19", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)));
    }
}
