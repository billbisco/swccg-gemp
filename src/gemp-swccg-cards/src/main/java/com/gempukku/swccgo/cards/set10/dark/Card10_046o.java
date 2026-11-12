package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections II
 * Title: Sense & Uncertain Is The Future
 * Text state: o (as printed, 10 January 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Sense & Uncertain Is The Future]] (Decipher archive scan); the current text is [[Sense & Uncertain Is The Future (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is the date of the Players Committee Decipher Errata sheet of 1 June 2017, which already has it.
 */
public class Card10_046o extends Card10_046 {
    public Card10_046o() {
        super();
        setGameText("USED: Target your highest-ability character and one just-played Interrupt. Draw destiny. If destiny < ability of target character, cancel target Interrupt. LOST: Use 3 Force. Each player counts cards in hand, then places entire hand and Used Pile onto Reserve Deck; reshuffle. Each player then draws from Reserve Deck the counted number of cards to create a new hand.");
        setTextState(new TextState("10_46", "Printed",
                "Reflections II printing (Decipher, 10 January 2001)",
                LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)));
    }
}
