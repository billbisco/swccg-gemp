package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections II
 * Title: Ghhhk & Those Rebels Won't Escape Us
 * Text state: o (as printed, 10 January 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Ghhhk & Those Rebels Won't Escape Us]] (Decipher archive scan); the current text is [[Ghhhk & Those Rebels Won't Escape Us (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). GEMP already plays the PC errata here (LOST plays during the damage segment when you have no cards left that can be forfeited); the printed wording is not modelled separately, so this state reuses
 * the current card's game text code and differs only in its text and text-state information.
 * supersededOn is the date of the Players Committee Decipher Errata sheet of 1 June 2017, which already has it.
 */
public class Card10_039o extends Card10_039 {
    public Card10_039o() {
        super();
        setGameText("USED: Cancel Nabrun Leids. LOST: If you just forfeited all your cards that participated in a battle you lost, cancel all remaining battle damage (immune to Sense). OR Cancel Hyper Escape, Closer?!, or one 'react.' OR During your move phase, cancel Landing Claw.");
        setTextState(new TextState("10_39", "Printed",
                "Reflections II printing (Decipher, 10 January 2001)",
                LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)));
    }
}
