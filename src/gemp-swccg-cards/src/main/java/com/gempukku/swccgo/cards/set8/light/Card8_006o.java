package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Endor
 * Title: Corporal Janse
 * Text state: o (as printed, June 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Corporal Janse]] (Decipher archive scan); the current text is
 * [[Corporal Janse (PC Errata)]]. The printed "When Janse fires" and the PC errata "If Janse just targeted" happen at the same point of a GEMP weapon firing (targeting), so this state reuses the current card's game text code. The PC errata date is not known; supersededOn is an upper bound (Advanced
 * Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card8_006o extends Card8_006 {
    public Card8_006o() {
        super();
        setGameText("When firing a character weapon, adds 1 to each weapon destiny draw. When Janse fires an A280 Sharpshooter Rifle at a character at an adjacent site and is using Sorry About The Mess, it is a Used Interrupt.");
        setTextState(new TextState("8_6", "Printed",
                "Endor printing (Decipher, June 1998)",
                LocalDate.of(1998, 6, 1), LocalDate.of(2023, 7, 30)));
    }
}
