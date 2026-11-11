package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Luke's Blaster Pistol (V) PC-E4: the PC card image, word for word PC-E2.
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2220_4169 extends Card2220_3169 {
    public Card2220_4169() {
        super();
        setLegacyVersion(new LegacyVersion("1220_169", "PC-E4",
                "PC card image (image server folder Virtual2-Light, file dated 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
