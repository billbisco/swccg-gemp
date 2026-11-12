package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Endor
 * Type: Objective
 * Title: Rebel Strike Team / Garrison Destroyed
 * Text state: o (as printed, June 1998; docs/legacy-versions.md "Text states")
 * <p>
 * The 7 side of the printed state 8_78o (Card8_078o): flipping 8_78o loads 8_78o_BACK. The 7 side's text was not
 * changed by the errata, so it is the current back (Card8_078_BACK) with the text-state information.
 */
public class Card8_078o_BACK extends Card8_078_BACK {
    public Card8_078o_BACK() {
        super();
        setTextState(new TextState("8_78_BACK", "Printed",
                "Endor printing (Decipher, June 1998)",
                LocalDate.of(1998, 6, 1), LocalDate.of(2002, 1, 29)));
    }
}
