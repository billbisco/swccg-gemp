package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Death Star II
 * Title: First Officer Thaneespi
 * Text state: o (as printed, July 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[First Officer Thaneespi]] (Decipher archive scan); the current text is
 * [[First Officer Thaneespi (PC Errata)]]. The PC errata reworded the card without changing how GEMP plays it, so this state reuses
 * the current card's game text code and differs only in its text and text-state information. The PC errata date is
 * not known; supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card9_012o extends Card9_012 {
    public Card9_012o() {
        super();
        setGameText("Deploys -2 to Home One. Adds 3 to power of any capital starship she pilots. When piloting a Star Cruiser with another Mon Calamari pilot aboard, unless opponent has total ability > 6 piloting here, opponent's total battle destiny here = zero.");
        setTextState(new TextState("9_12", "Printed",
                "Death Star II printing (Decipher, July 2000)",
                LocalDate.of(2000, 7, 1), LocalDate.of(2023, 7, 30)));
    }
}
