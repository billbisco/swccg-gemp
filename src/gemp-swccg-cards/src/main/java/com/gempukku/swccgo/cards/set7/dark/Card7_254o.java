package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Interrupt (Used)
 * Title: In Range
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says the target is "power and maneuver -3 for remainder of battle"; the Players Committee Advanced Rulebook
 * (Appendix A) says "power -3 and maneuver -3 (if possible)". GEMP's UseTractorBeamEffect already applies both
 * modifiers until the end of the battle.
 * The difference is wording only and plays the same in GEMP, so this state reuses the bare card's code
 * (Card7_254) and differs only in its game text and text-state information.
 */
public class Card7_254o extends Card7_254 {
    public Card7_254o() {
        super();
        setGameText("If you have a Star Destroyer in a battle, during the weapons segment use its tractor beam for free. Add 2 to tractor beam destiny if targeting a unique (•) starship. If not captured, target is power and maneuver -3 for remainder of battle.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_254", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
