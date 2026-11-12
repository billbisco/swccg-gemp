package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Death Star II
 * Title: Saber 4
 * Text state: o (as printed, July 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Saber 4]] (Decipher archive scan); the current text is
 * [[Saber 4 (PC Errata)]]. The current card plays the PC errata "a battle where you have a TIE"; this state keeps the printed condition (a 'react' to a battle initiated against a TIE, i.e. one where your TIE is defending). The PC errata date is not known; supersededOn is an upper bound (Advanced
 * Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card9_165o extends Card9_165 {
    public Card9_165o() {
        super();
        setGameText("May deploy with a pilot as a 'react' to a battle initiated against a TIE (for free if TIE is in Saber Squadron). May add 1 pilot. Immune to attrition < 4 when DS-181-4 piloting.");
        setTextState(new TextState("9_165", "Printed",
                "Death Star II printing (Decipher, July 2000)",
                LocalDate.of(2000, 7, 1), LocalDate.of(2023, 7, 30)));
    }

    /** As printed: a 'react' to a battle initiated against a TIE, i.e. one where your TIE is defending. */
    @Override
    protected Filter reactToBattleTIEFilter() {
        return Filters.defendingBattle;
    }
}
