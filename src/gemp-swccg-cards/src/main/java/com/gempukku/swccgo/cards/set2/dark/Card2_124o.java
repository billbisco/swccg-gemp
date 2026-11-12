package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Program Trap
 * Text state: o (as printed, July 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Program Trap]] (Decipher archive scan); the current text is [[Program Trap (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card2_124o extends Card2_124 {
    public Card2_124o() {
        super();
        setGameText("Use 2 Force to deploy on an opponent's droid (except R2-D2 and C-3PO), 1 on your droid. The next time either player draws a destiny matching the number of characters at same site, droid explodes, causing all characters present to be lost.");
        setTextState(new TextState("2_124", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2023, 7, 30)));
    }
}
