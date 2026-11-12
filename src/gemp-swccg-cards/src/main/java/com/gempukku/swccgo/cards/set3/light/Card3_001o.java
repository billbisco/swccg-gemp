package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: 2-1B (Too-Onebee)
 * Text state: o (as printed, November 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[2-1B (Too-Onebee)]] (Decipher archive scan); the current text is [[2-1B (Too-Onebee) (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card3_001o extends Card3_001 {
    public Card3_001o() {
        super();
        setGameText("Once per turn, one of your non-droid characters lost from same site may go to your Used Pile rather than your Lost Pile. Subtracts 2 from X on your Bacta Tank.");
        setTextState(new TextState("3_1", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
