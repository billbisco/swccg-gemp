package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections II
 * Title: Houjix & Out Of Nowhere
 * Text state: o (as printed, 10 January 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Houjix & Out Of Nowhere]] (Decipher archive scan); the current text is [[Houjix & Out Of Nowhere (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). GEMP already plays the PC errata here (LOST plays during the damage segment when you have no cards left that can be forfeited); the printed wording is not modelled separately, so this state reuses
 * the current card's game text code and differs only in its text and text-state information.
 * supersededOn is the date of the Players Committee Decipher Errata sheet of 1 June 2017, which already has it.
 */
public class Card10_008o extends Card10_008 {
    public Card10_008o() {
        super();
        setGameText("USED: If opponent just initiated a battle at a system or sector where you have a starship with maneuver > 3, use 1 Force to add one battle destiny. LOST: If you just forfeited all your cards that participated in a battle you lost, cancel all remaining battle damage (immune to Sense).");
        setTextState(new TextState("10_8", "Printed",
                "Reflections II printing (Decipher, 10 January 2001)",
                LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)));
    }
}
