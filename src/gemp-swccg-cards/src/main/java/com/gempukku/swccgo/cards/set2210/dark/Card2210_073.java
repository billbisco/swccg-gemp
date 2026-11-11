package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_073;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Mos Eisley (V)
 * Original version of 1210_73: the Virtual Block 1 release sheet (2009-12-07), interior and exterior site on both sides.
 * The Interior icon was struck on 2014-03-27 (PC forum "Current Rulings/Errata" p874174; PC-E1, the current card).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_073 extends Card1210_073 {
    public Card2210_073() {
        addIcons(Icon.INTERIOR_SITE);
        setLegacyVersion(new LegacyVersion("1210_73", "",
                "Virtual Block 1 release sheet (2009-12-07), card #71", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 2, 25)));
    }
}
