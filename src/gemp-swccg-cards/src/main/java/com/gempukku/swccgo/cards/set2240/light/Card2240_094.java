package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set601.light.Card601_240;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Interrupt
 * Title: Careful Planning (V)
 * Original version of 1240_94: the Virtual Block 4 release sheet text (Dec 2009), "with < 3 [Light Side Force] and up to three Effects" (the card list added "icons").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_094 extends Card601_240 {
    public Card2240_094() {
        super();
        setGameText("If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Light Side Force] and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1240_94", "",
                "Virtual Block 4 release sheet (2009-12-07), card #93E", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
