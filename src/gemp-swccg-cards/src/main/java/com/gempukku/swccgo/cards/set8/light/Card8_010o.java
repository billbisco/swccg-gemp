package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Endor
 * Title: Endor Scout Trooper
 * Text state: o (as printed, June 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Endor Scout Trooper]] (Decipher archive scan); the current text is
 * [[Endor Scout Trooper (PC Errata)]]. The PC errata reworded the card without changing how GEMP plays it, so this state reuses
 * the current card's game text code and differs only in its text and text-state information. The PC errata date is
 * not known; supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card8_010o extends Card8_010 {
    public Card8_010o() {
        super();
        setGameText("Power -1 while not on Endor. While Endor Scout Trooper is on Endor, Lieutenant Page and each of your Rebel scouts of ability < 3 at same and adjacent exterior sites are immune to attrition < 2.");
        setTextState(new TextState("8_10", "Printed",
                "Endor printing (Decipher, June 1998)",
                LocalDate.of(1998, 6, 1), LocalDate.of(2023, 7, 30)));
    }
}
