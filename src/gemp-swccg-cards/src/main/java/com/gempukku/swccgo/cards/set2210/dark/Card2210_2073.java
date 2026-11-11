package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Tatooine: Mos Eisley (V) PC-E2: the Master List (Interior icon), as the original.
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2210_2073 extends Card2210_073 {
    public Card2210_2073() {
        super();
        setLegacyVersion(new LegacyVersion("1210_73", "PC-E2",
                "Players Committee Virtual Cards Master List (official text as of 2013-04-27)", LocalDate.of(2013, 4, 27), LocalDate.of(2014, 3, 27)));
    }
}
