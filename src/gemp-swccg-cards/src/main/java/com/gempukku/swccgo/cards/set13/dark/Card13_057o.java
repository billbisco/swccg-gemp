package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections III
 * Title: Blockade Flagship: Hallway
 * Text state: o (as printed, 31 October 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Blockade Flagship: Hallway]] (Decipher archive scan); the current text is [[Blockade Flagship: Hallway (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is the date of the Players Committee Decipher Errata sheet of 1 June 2017, which already has it.
 */
public class Card13_057o extends Card13_057 {
    public Card13_057o() {
        super();
        setLocationLightSideGameText("Your Jedi deploy +2 here.");
        setLocationDarkSideGameText("If you occupy, Force generation +1 here, and opponent's characters may not move from here.");
        setTextState(new TextState("13_57", "Printed",
                "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)));
    }
}
