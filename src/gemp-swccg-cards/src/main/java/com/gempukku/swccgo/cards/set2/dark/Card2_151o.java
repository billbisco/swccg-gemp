package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Black 4
 * Text state: o (as printed, July 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Black 4]] (Decipher archive scan); the current text is [[Black 4 (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). As printed it may deploy as a 'react' only to a battle initiated against a TIE (your TIE defending); the PC errata allows "a battle where you have a TIE". This state keeps the printed condition.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card2_151o extends Card2_151 {
    public Card2_151o() {
        super();
        setGameText("May add 1 pilot. May deploy with a pilot as a 'react' to a battle initiated against a TIE (for free if TIE is Black 2 or 3). Must have pilot aboard to use power or maneuver.");
        setTextState(new TextState("2_151", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2023, 7, 30)));
    }

    /** As printed: a 'react' to a battle initiated against a TIE, i.e. one where your TIE is defending. */
    @Override
    protected Filter reactToBattleTIEFilter() {
        return Filters.defendingBattle;
    }
}
