package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Beldon's Eye
 * Text state: o (as printed, November 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Beldon's Eye]] (Decipher archive scan); the current text is [[Beldon's Eye (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card5_018o extends Card5_018 {
    public Card5_018o() {
        super();
        setGameText("Deploy on Cloud City. Your Tibanna Gas Miners deploy free to Cloud City sites and double the Force they activate.");
        setTextState(new TextState("5_18", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
