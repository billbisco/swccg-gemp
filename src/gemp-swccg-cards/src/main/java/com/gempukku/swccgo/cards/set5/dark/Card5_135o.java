package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Dark Strike
 * Text state: o (as printed, November 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Dark Strike]] (Decipher archive scan); the current text is [[Dark Strike (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). The PC errata reworded the card without changing how GEMP plays it, so this state reuses the current card's game
 * text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card5_135o extends Card5_135 {
    public Card5_135o() {
        super();
        setGameText("If you just targeted with a lightsaber, add 3 to your weapon destiny. OR Cancel Swing-And-A-Miss. OR Lose 1 Force to cause one opponent's character just 'hit' to be immediately placed in Lost Pile.");
        setTextState(new TextState("5_135", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
