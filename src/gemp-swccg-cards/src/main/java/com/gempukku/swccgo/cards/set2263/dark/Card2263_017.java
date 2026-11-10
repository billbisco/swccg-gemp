package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_207;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Starship
 * Subtype: Starfighter
 * Title: Colonel Jendon In Onyx 1
 * Original version of 1263_17: the Virtual Block 6.3 release sheet text (released 2010-03-12; the PC card image and the
 * Master List of 2013-04-27 print it, the Master List copy being a stale reprint), "Permanent pilot is •Jendon who provides".
 * The PC website card list (by 2012-07-21, PC-E1, the current card) adds a comma after "•Jendon"
 * (swccg-guild/work#236: punctuation is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_017 extends Card601_207 {
    public Card2263_017() {
        super();
        setGameText("May deploy as a 'react.' Permanent pilot is •Jendon who provides ability of 2. When deployed, may [download] a weapon on Onyx 1. Adds one battle destiny if with Onyx 2. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1263_17", "",
                "Virtual Block 6.3 release sheet (2010-03-12); superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2010, 3, 12), LocalDate.of(2012, 7, 21)));
    }
}
