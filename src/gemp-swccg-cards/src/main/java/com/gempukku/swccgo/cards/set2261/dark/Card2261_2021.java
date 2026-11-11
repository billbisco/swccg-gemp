package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Leave Them To Me (V) PC-E2: the PC card image (D18), word for word the original.
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2261_2021 extends Card2261_021 {
    public Card2261_2021() {
        super();
        setLegacyVersion(new LegacyVersion("1261_21", "PC-E2",
                "PC card image (image server folder VirtualDS-Dark, file dated 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
