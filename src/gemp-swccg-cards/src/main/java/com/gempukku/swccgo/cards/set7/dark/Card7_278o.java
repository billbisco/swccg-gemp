package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Location (Site)
 * Title: Coruscant: Imperial Square
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print names "Emperor Palpatine" on both sides; the Players Committee wrote "Emperor" (the same persona).
 * The difference is wording only and plays the same in GEMP, so this state reuses the bare card's code
 * (Card7_278) and differs only in its game text and text-state information.
 */
public class Card7_278o extends Card7_278 {
    public Card7_278o() {
        super();
        setLocationDarkSideGameText("Emperor Palpatine deploys free here. If your moff here, all Imperials are deploy -1 at sites.");
        setLocationLightSideGameText("Force drain +1 here. If you control, Emperor Palpatine may not deploy to Coruscant.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_278", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
