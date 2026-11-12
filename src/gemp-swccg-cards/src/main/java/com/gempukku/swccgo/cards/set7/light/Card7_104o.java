package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Interrupt (Used)
 * Title: Stay Sharp!
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says "may add 2 to destiny draw"; the Players Committee wrote "to the total weapon destiny". The weapon draws
 * one destiny, so the two play the same.
 * The difference is wording only and plays the same in GEMP, so this state reuses the bare card's code
 * (Card7_104) and differs only in its game text and text-state information.
 */
public class Card7_104o extends Card7_104 {
    public Card7_104o() {
        super();
        setGameText("During your control phase, fire one of your starship weapons (for free). If Han or any gunner is aboard that starship, may add 2 to destiny draw. 'Hit' target is lost. OR If you just fired a weapon in battle, add that weapon's destiny number to your total power.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_104", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
