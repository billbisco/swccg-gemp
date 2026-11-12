package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections II
 * Title: Yoda Stew & •You Do Have Your Moments
 * Text state: o (as printed, 10 January 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Yoda Stew & •You Do Have Your Moments]] (Decipher archive scan); the current text is [[Yoda Stew & •You Do Have Your Moments (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). GEMP already plays the PC errata here (LOST plays when the initial attrition is calculated, at the start of the damage segment); the printed wording is not modelled separately, so this state reuses
 * the current card's game text code and differs only in its text and text-state information.
 * supersededOn is the date of the Players Committee Decipher Errata sheet of 1 June 2017, which already has it.
 */
public class Card10_027o extends Card10_027 {
    public Card10_027o() {
        super();
        setGameText("USED: During opponent's turn, take up to four cards from your hand and place them on top of your Force Pile. LOST: During a battle, before any cards have been forfeited, cause all attrition for both sides to be reduced to zero.");
        setTextState(new TextState("10_27", "Printed",
                "Reflections II printing (Decipher, 10 January 2001)",
                LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)));
    }
}
