package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Tatooine: Mos Eisley (V) PC-E1: the PC card image (Interior icon), as the original.
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2210_1073 extends Card2210_073 {
    public Card2210_1073() {
        super();
        setLegacyVersion(new LegacyVersion("1210_73", "PC-E1",
                "PC card image (image server folder Virtual1-Dark, file dated 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
