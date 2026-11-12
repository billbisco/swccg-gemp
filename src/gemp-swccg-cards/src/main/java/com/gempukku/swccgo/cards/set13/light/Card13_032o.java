package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections III
 * Title: Naboo: Theed Palace Generator Core
 * Text state: o (as printed, 31 October 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Naboo: Theed Palace Generator Core]] (Decipher archive scan); the current text is [[Naboo: Theed Palace Generator Core (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). GEMP already plays the PC errata here (a character about to be 'hit' here goes to the Used Pile); the printed wording is not modelled separately, so this state reuses
 * the current card's game text code and differs only in its text and text-state information.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card13_032o extends Card13_032 {
    public Card13_032o() {
        super();
        setGameText("Light: Any characters of ability < 5 'hit' here (and all cards on them) are placed in owner's Used Pile. Dark: Any characters of ability < 5 'hit' here (and all cards on them) are placed in owner's Used Pile.");
        setTextState(new TextState("13_32", "Printed",
                "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2023, 7, 30)));
    }
}
