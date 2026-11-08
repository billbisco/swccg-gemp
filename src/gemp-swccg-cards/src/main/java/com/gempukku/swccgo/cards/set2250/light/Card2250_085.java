package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_164;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Weapon
 * Title: Elegant Lightsaber
 * Original version of 1250_85: the Virtual Block 5 release sheet text (the hit clause comes first; the PC card
 * list, PC-E1, reorders it). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_085 extends Card601_164 {
    public Card2250_085() {
        setGameText("Deploy on a Jedi. May add 1 to Force drain at opponent's site where present. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition If total destiny > defense value.");
        setLegacyVersion(new LegacyVersion("1250_85", "",
                "Virtual Block 5 release sheet (2009-12-07), card #86", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
