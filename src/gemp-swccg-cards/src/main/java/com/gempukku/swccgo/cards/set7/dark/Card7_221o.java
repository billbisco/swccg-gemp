package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Effect (Normal)
 * Title: Bantha Herd
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says "During battle, if your Tusken Raider or bantha here"; the Players Committee wrote "During battle here".
 * GEMP's code already moves only to exterior sites and adds the destiny only in a battle here.
 * The difference is wording only and plays the same in GEMP, so this state reuses the bare card's code
 * (Card7_221) and differs only in its game text and text-state information.
 */
public class Card7_221o extends Card7_221 {
    public Card7_221o() {
        super();
        setGameText("Deploy on an exterior Tatooine site. Specify starting direction. During your control phase, moves to next adjacent exterior site (reversing direction as necessary). During battle, if your Tusken Raider or bantha here, may add one battle destiny.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_221", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
