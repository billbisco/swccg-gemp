package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Title: Recoil In Fear
 * Text state: o (as printed, April 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Recoil In Fear]] (Decipher archive scan); the current text is [[Recoil In Fear (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card4_058o extends Card4_058 {
    public Card4_058o() {
        super();
        setGameText("Use 3 Force. Each player counts cards in hand, then places entire hand and Used Pile onto Reserve Deck. Shuffle, cut and replace. Each player then draws from Reserve Deck the counted number of cards to create a new hand.");
        setTextState(new TextState("4_58", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)));
    }
}
