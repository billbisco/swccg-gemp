package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_172;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Ascension Guns (V)
 * Original version of 1261_90: the Virtual Block 6.1 release sheet wording (Sep 2009), "a related Throne Room"; same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_090 extends Card601_172 {
    public Card2261_090() {
        setGameText("USED: If opponent occupies your site, peek at the top two cards of Reserve Deck and take one into hand. \nLOST: During any move phase, lose 2 Force to relocate up to 3 of your characters from an interior Theed Palace site to a related Throne Room (or vice versa).");
        setLegacyVersion(new LegacyVersion("1261_90", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #92", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 8, 21)));
    }
}
