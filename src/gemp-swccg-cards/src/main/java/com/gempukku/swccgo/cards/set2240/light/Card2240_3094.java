package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set601.light.Card601_240;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Interrupt
 * Title: •Careful Planning (V)
 * PC-E1 version of 1240_94 (Batch R8-U5, Bill 2026-11-11: every publication is a version; PC card
 * images are versions at their proven date): PC card image (the 2009-09-09 errata; in the V-kit by 2009-12-09). Order so far: PC-E1 pc_h1 (2009-09-09); the current card is the
 * Virtual Cards Master List (2013-04-27). Same play as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_3094 extends Card601_240 {
    public Card2240_3094() {
        super();
        setGameText("If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Light Side Force] icons and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1240_94", "PC-E1",
                "PC card image (the 2009-09-09 errata; in the V-kit by 2009-12-09)", LocalDate.of(2009, 9, 9), LocalDate.of(2009, 12, 7)));
    }
}
