package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Theed Palace
 * Title: Booma
 * Text state: o (as printed, December 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Booma]] (Decipher archive scan); the current text is [[Booma (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card14_063o extends Card14_063 {
    public Card14_063o() {
        super();
        setGameText("Deploy on your Fambaa. Twice during battle may draw destiny. If destiny < total number of opponent's characters and vehicles present, one of them is lost (opponent's choice).");
        setTextState(new TextState("14_63", "Printed",
                "Theed Palace printing (Decipher, December 2001)",
                LocalDate.of(2001, 12, 1), LocalDate.of(2023, 7, 30)));
    }
}
