package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Character (Rebel)
 * Title: Commander Wedge Antilles
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says "draws one battle destiny if not able to otherwise"; the Players Committee dropped "one".
 * The difference is wording only and plays the same in GEMP, so this state reuses the bare card's code
 * (Card7_012) and differs only in its game text and text-state information.
 */
public class Card7_012o extends Card7_012 {
    public Card7_012o() {
        super();
        setGameText("Adds 3 to power of anything he pilots. When piloting Rogue 3, also adds 2 to maneuver and draws one battle destiny if not able to otherwise. May use 2 Force to take one One More Pass into hand from Reserve Deck; reshuffle.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_12", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
