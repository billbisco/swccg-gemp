package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections III
 * Title: Naboo: Theed Palace Generator Core (Dark)
 * Text state: o (as printed, 31 October 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Naboo: Theed Palace Generator Core (Dark)]] (Decipher archive scan); the current text is [[Naboo: Theed Palace Generator Core (Dark) (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). GEMP already plays the PC errata here (a character about to be 'hit' here goes to the Used Pile); the printed wording is not modelled separately, so this state reuses
 * the current card's game text code and differs only in its text and text-state information.
 * supersededOn is the date of the Players Committee Decipher Errata sheet of 1 June 2017, which already has it.
 */
public class Card13_077o extends Card13_077 {
    public Card13_077o() {
        super();
        setLocationLightSideGameText("Characters of ability < 5 'hit' here are placed (with all cards on them) in owner's Used Pile.");
        setLocationDarkSideGameText("Characters of ability < 5 'hit' here are placed (with all cards on them) in owner's Used Pile.");
        setTextState(new TextState("13_77", "Printed",
                "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)));
    }
}
