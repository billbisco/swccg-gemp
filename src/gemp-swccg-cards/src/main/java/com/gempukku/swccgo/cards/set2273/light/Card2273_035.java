package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set601.light.Card601_063;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Interrupt
 * Title: Yub Yub, Commander
 * Original version of 1273_35: the Virtual Block 7.3 release sheet text ("Rogue Squadron", "OR If"). The PC card list (PC-E1, the current card 601_63,
 * 2012-07-21 to 2013-01-11) spells "Rogue Sqaudron" and writes "Or if". Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_035 extends Card601_063 {
    public Card2273_035() {
        super();
        setGameText("USED: ▼ a Rogue Squadron pilot to a location you occupy with a Rogue Squadron pilot. LOST: Cancel an attempt by opponent to target a Rogue Squadron pilot to be captured or excluded from battle. OR If two Rogue Squadron pilots are in battle together, add one battle destiny.");
        setLegacyVersion(new LegacyVersion("1273_35", "",
                "Virtual Block 7.3 release sheet (2011-06-03)", LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21)));
    }
}
