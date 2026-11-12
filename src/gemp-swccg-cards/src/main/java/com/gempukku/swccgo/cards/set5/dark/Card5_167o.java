package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Cloud City: Chasm Walkway (Dark)
 * Text state: o (as printed, November 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Cloud City: Chasm Walkway (Dark)]] (Decipher archive scan); the current text is [[Cloud City: Chasm Walkway (Dark) (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). GEMP already plays the PC errata here (a character about to be 'hit' here is relocated); the printed wording is not modelled separately, so this state reuses
 * the current card's game text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card5_167o extends Card5_167 {
    public Card5_167o() {
        super();
        setLocationLightSideGameText("If Weather Vane on table, characters 'hit' here are instead immediately relocated there.");
        setLocationDarkSideGameText("If Weather Vane on table, characters 'hit' here are instead immediately relocated there.");
        setTextState(new TextState("5_167", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)));
    }
}
