package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Bacta Tank
 * Text state: o (as printed, November 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Bacta Tank]] (Decipher archive scan); the current text is [[Bacta Tank (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card3_032o extends Card3_032 {
    public Card3_032o() {
        super();
        setGameText("Use 4 Force to deploy on your side of table. A non-droid character you just lost may be placed here instead of Lost Pile. Holds 1 'patient' at a time. During your deploy phase, may use X Force to bring 'patient' to hand, where X = deploy cost of 'patient'.");
        setTextState(new TextState("3_32", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
