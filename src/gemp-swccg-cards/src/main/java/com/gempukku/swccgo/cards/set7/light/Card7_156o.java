package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Transport
 * Title: Sandspeeder
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says "May move as a 'react' only to Tatooine or desert sites." Decipher's Glossary Supplement (2002; wiki
 * [[Sandspeeder (Errata)]]) and the Players Committee Advanced Rulebook (Appendix A) drop "only", which does not change play, so this state reuses the bare card's code
 * (Card7_156) and differs only in its game text and text-state information.
 */
public class Card7_156o extends Card7_156 {
    public Card7_156o() {
        super();
        setGameText("May add 1 pilot or passenger. Permanent pilot provides ability of 1. May move as a 'react' only to Tatooine or desert sites.");
        // Superseded by Decipher's Glossary Supplement (29 January 2002), which has the bare card's wording.
        setTextState(new TextState("7_156", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2002, 1, 29)));
    }
}
