package com.gempukku.swccgo.cards.set2291.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * AT-RT PC-E2: the Master List, word for word PC-E1.
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2291_3071 extends Card2291_1071 {
    public Card2291_3071() {
        super();
        setLegacyVersion(new LegacyVersion("1291_71", "PC-E2",
                "Players Committee Virtual Cards Master List (PDF built 2014-02-03)", LocalDate.of(2014, 2, 3), LocalDate.of(2014, 2, 18)));
    }
}
