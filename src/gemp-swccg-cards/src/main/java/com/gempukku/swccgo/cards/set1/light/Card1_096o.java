package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Title: Move Along...
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Move Along...]] (Decipher archive scan); the current text is [[Move Along... (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card1_096o extends Card1_096 {
    public Card1_096o() {
        super();
        setGameText("Use 1 Force to temporarily suspend a battle just initiated at a site where one of your Jedi is present. Draw destiny. That number of opponent's characters with ability = 1 (your choice) must move away (for free), or are lost. Battle continues.");
        setTextState(new TextState("1_96", "Printed",
                "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
