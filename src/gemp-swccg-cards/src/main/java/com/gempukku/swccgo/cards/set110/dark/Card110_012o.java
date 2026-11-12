package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Title: Zuckuss In Mist Hunter
 * Text state: o (as printed, January 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Zuckuss In Mist Hunter]] (Decipher archive scan); the current text is [[Zuckuss In Mist Hunter (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card110_012o extends Card110_012 {
    public Card110_012o() {
        super();
        setGameText("May add 1 pilot and 3 passengers. Permanent pilot is •Zuckuss, who provides ability of 4 and adds 2 to power. Unless opponent has total ability > 6 piloting here, opponent's total battle destiny here = zero.");
        setTextState(new TextState("110_12", "Printed",
                "Enhanced Jabba's Palace printing (Decipher, January 2000)",
                LocalDate.of(2000, 1, 1), LocalDate.of(2023, 7, 30)));
    }
}
