package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_010;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Lieutenant Grond (V)
 * Original version of 1230_10: the Virtual Block 3 release sheet text (Dec 2009), "Adds one battle destiny with Colonel Dyer" (the card list added "if").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_010 extends Card1230_010 {
    public Card2230_010() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Adds one battle destiny with Colonel Dyer or if driving a swoop. May place Lieutenant Grond in Used Pile to cancel a Force drain at a related location.");
        setLegacyVersion(new LegacyVersion("1230_10", "",
                "Virtual Block 3 release sheet (2009-12-07), card #10", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
