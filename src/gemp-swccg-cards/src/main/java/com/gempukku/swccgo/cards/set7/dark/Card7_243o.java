package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Effect (Normal)
 * Title: Tarkin's Bounty
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says "captured by a bounty hunter or Imperial"; the Players Committee wrote "captured, seized by a bounty
 * hunter or Imperial". GEMP's code checks a capture by a bounty hunter or Imperial, as printed.
 * The difference is wording only and plays the same in GEMP, so this state reuses the bare card's code
 * (Card7_243) and differs only in its game text and text-state information.
 */
public class Card7_243o extends Card7_243 {
    public Card7_243o() {
        super();
        setGameText("Deploy on opponent's spy or Rebel leader. If subsequently captured by a bounty hunter or Imperial and then transferred to Detention Block Corridor, retrieve Force equal to character's forfeit (+4 if Leia) and lose Effect. (Immune to Alter.)");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_243", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
