package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Into The Ventilation Shaft, Lefty
 * Text state: o (as printed, November 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Into The Ventilation Shaft, Lefty]] (Decipher archive scan); the current text is [[Into The Ventilation Shaft, Lefty (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). As printed a 'hit' character is not restored to normal before it is relocated; this state keeps that.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card5_054o extends Card5_054 {
    public Card5_054o() {
        super();
        setGameText("Relocate one of your characters from a Cloud City site to Weather Vane. May be played even after a battle has just been initiated.");
        setTextState(new TextState("5_54", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)));
    }

    /** As printed: no "(if 'hit,' character is first restored to normal)". */
    @Override
    protected boolean restoresHitCharacterToNormal() {
        return false;
    }
}
