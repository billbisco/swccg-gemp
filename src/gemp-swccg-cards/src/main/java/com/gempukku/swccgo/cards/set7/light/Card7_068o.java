package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Effect (Normal)
 * Title: Legendary Starfighter
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print opens "If you just won a battle in which opponent lost a starship"; the Players Committee reworded it
 * "If opponent just lost a starship in a battle you won". The print shows no uniqueness dot; as for every text state,
 * this state keeps the bare card's uniqueness (docs/legacy-versions.md "Same card").
 * The difference is wording only and plays the same in GEMP, so this state reuses the bare card's code
 * (Card7_068) and differs only in its game text and text-state information.
 */
public class Card7_068o extends Card7_068 {
    public Card7_068o() {
        super();
        setGameText("If you just won a battle in which opponent lost a starship, deploy on your participating starfighter. Once during each of opponent's move phases, opponent loses 1 Force (2 if starfighter is Falcon or Red 5). Also, that starfighter is power +2. (Immune to Control.)");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_68", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
