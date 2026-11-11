package com.gempukku.swccgo.cards.set2291.light;

import com.gempukku.swccgo.cards.set1291.light.Card1291_069;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 9.1
 * Type: Objective
 * Title: Republic At War / Aggressive Negotiations (front)
 * Original version of 1291_69: the Virtual Block 9.1 release sheet (official text as of 2014-01-10). The Advocate
 * Council errata of 2014-08-08 (PC forum "Current Rulings/Errata" p901169) changed the 7 side (Aggressive Negotiations)
 * only. The back side is 2291_69_BACK. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2291_069 extends Card1291_069 {
    public Card2291_069() {
        super();
        setGameText(ORIGINAL_GAMETEXT);
        setLegacyVersion(new LegacyVersion("1291_69", "",
                "Virtual Block 9.1 release sheet (2014-01-10)", LocalDate.of(2014, 1, 10), LocalDate.of(2014, 2, 3)));
    }
}
