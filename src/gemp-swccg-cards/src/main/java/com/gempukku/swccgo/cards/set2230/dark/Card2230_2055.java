package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Subtype: Capital
 * Title: Jabba's Space Cruiser (V)
 *
 * PC-E1 version of 1230_55: the PC card image (jabbasspacecruiser.gif, slip 54E), the same image as in
 * swccgvkit-legacy svn r1 (2009-12-09), so made by then; no firmer date is known, so it is placed at that proven
 * upper bound (Bill 2026-11-11 08:23) and may be older. It prints a different order ("Permanent pilot provides
 * ability of 2." first). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2055 extends Card601_012 {
    public Card2230_2055() {
        super();
        setGameText("Deploys and moves like a starfighter. Permanent pilot provides ability of 2. May add 1 alien pilot and 6 passengers. Your [Independent] starships may deploy here as a 'react.' If Jabba here, immune to attrition.");
        setLegacyVersion(new LegacyVersion("1230_55", "PC-E1",
                "PC card image (slip 54E; in swccgvkit-legacy svn r1, 2009-12-09; may be older)", LocalDate.of(2009, 12, 9), LocalDate.of(2012, 7, 21)));
    }
}
