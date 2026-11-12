package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Endor
 * Title: Firefight
 * Text state: o (as printed, June 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Firefight]] (Decipher archive scan); the current text is
 * [[Firefight (PC Errata)]]. The PC errata reworded the card without changing how GEMP plays it, so this state reuses
 * the current card's game text code and differs only in its text and text-state information. The PC errata date is
 * not known; supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card8_050o extends Card8_050 {
    public Card8_050o() {
        super();
        setGameText("During a battle, if you have weapons at both sites adjacent to that battle, add 3 to your total power and add one battle destiny. OR For remainder of turn, your scout at an exterior site is power +1 and adds 1 to each of that character's weapon destiny draws.");
        setTextState(new TextState("8_50", "Printed",
                "Endor printing (Decipher, June 1998)",
                LocalDate.of(1998, 6, 1), LocalDate.of(2023, 7, 30)));
    }
}
