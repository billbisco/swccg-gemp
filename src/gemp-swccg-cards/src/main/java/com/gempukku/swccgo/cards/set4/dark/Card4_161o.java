package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: Executor: Holotheatre
 * Text state: o (as printed, April 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Executor: Holotheatre]] (Decipher archive scan); the current text is [[Executor: Holotheatre (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card4_161o extends Card4_161 {
    public Card4_161o() {
        super();
        setLocationLightSideGameText("If you control, Force drain +1 here.");
        setLocationDarkSideGameText("If you have Vader or Emperor on table, your Force generation is +1 here (+2 if both).");
        setTextState(new TextState("4_161", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)));
    }
}
