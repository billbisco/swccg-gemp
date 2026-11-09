package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Effect
 * Title: A Remote Planet (V)
 * Original version of 1282_20: the Virtual Block 8.2 release sheet text ("Gone; treat "4" on it as "2."", "(and/or their matching weapon)").
 * The PC card list (PC-E1, the current card, 2012-12-12 to 2013-01-11) writes "Gone: treat '4' on it as '2'." and "(an/or their matching
 * weapon_". Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_020 extends Card1282_020 {
    public Card2282_020() {
        super();
        setGameText("Deploy on The Hyperdrive Generator's Gone; treat \"4\" on it as \"2.\" Your Force generation at City Outskirts is +1. May deploy systems regardless of your Objective deployment restrictions. Once per game, may ▼ Qui-Gon or Obi-Wan (and/or their matching weapon). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1282_20", "",
                "Virtual Block 8.2 release sheet (2012-09-14)", LocalDate.of(2012, 9, 14), LocalDate.of(2012, 12, 12)));
    }
}
