package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Effect (Normal)
 * Title: Pride Of The Empire
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print opens "If you just won a battle in which opponent lost a starship"; Decipher's Glossary Supplement (2002)
 * reworded it "If opponent just lost a starship in a battle you won" (the bare card's text). The print shows no
 * uniqueness dot; as for every text state, this state keeps the bare card's uniqueness (docs/legacy-versions.md
 * "Same card").
 * The difference is wording only and plays the same in GEMP, so this state reuses the bare card's code
 * (Card7_236) and differs only in its game text and text-state information.
 */
public class Card7_236o extends Card7_236 {
    public Card7_236o() {
        super();
        setGameText("If you just won a battle in which opponent lost a starship, deploy on your participating starfighter. Once during each of opponent's move phases, opponent loses 1 Force (2 if starfighter is a TIE/ln). Also, that starfighter is power +2. (Immune to Control.)");
        // Superseded by Decipher's Glossary Supplement (29 January 2002), which has the bare card's wording.
        setTextState(new TextState("7_236", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2002, 1, 29)));
    }
}
