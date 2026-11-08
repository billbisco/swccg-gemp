package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_046;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Outflank (V)
 * Original version of 1230_46: the Virtual Block 3 release sheet text (Dec 2009), "may use 1 Force" (the card list added "you").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_046 extends Card1230_046 {
    public Card2230_046() {
        super();
        setGameText("If opponent just initiated battle at a site where their total power is greater than yours, may use 1 Force (or cause opponent to use 1 Force) to relocate one of your characters or vehicles to that site from a related location.");
        setLegacyVersion(new LegacyVersion("1230_46", "",
                "Virtual Block 3 release sheet (2009-12-07), card #46", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
