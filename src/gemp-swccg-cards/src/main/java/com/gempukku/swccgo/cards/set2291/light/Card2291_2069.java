package com.gempukku.swccgo.cards.set2291.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Republic At War PC-E2: the PC card images (both sides as the original). The back side is 2291_2069_BACK.
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2291_2069 extends Card2291_069 {
    public Card2291_2069() {
        super();
        setLegacyVersion(new LegacyVersion("1291_69", "PC-E2",
                "PC card image (image server folder Virtual9-Light, file dated 2014-02-18)", LocalDate.of(2014, 2, 18), LocalDate.of(2014, 8, 8)));
    }
}
