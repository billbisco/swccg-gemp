package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Kessel: Spice Mines - Extraction Facility PC-E2: the PC card image shows the Episode I icon again.
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2240_2055 extends Card2240_055 {
    public Card2240_2055() {
        super();
        setLegacyVersion(new LegacyVersion("1240_55", "PC-E2",
                "PC card image (image server folder Virtual4-Dark, file dated 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
