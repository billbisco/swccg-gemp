package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Location (System)
 * Title: Bothawui
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says "If you control, Undercover is immune to Hutt Smooch"; the Players Committee clarified it as "characters
 * targeted by Undercover are immune to Hutt Smooch" (Hutt Smooch deploys on a character, not on the Undercover Effect),
 * which is how the bare card plays.
 * The difference is wording only and plays the same in GEMP, so this state reuses the bare card's code
 * (Card7_110) and differs only in its game text and text-state information.
 */
public class Card7_110o extends Card7_110 {
    public Card7_110o() {
        super();
        setLocationLightSideGameText("Your spies deploy -1 here and at related sites. If you control, Undercover is immune to Hutt Smooch.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_110", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
