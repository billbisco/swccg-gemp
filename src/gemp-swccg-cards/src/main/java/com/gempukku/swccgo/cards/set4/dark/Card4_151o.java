package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: Uncertain Is The Future
 * Text state: o (as printed, April 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Uncertain Is The Future]] (Decipher archive scan); the current text is [[Uncertain Is The Future (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card4_151o extends Card4_151 {
    public Card4_151o() {
        super();
        setGameText("Use 3 Force. Each player counts cards in hand, then places entire hand and Used Pile onto Reserve Deck. Shuffle, cut and replace. Each player then draws from Reserve Deck the counted number of cards to create a new hand.");
        setTextState(new TextState("4_151", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)));
    }
}
