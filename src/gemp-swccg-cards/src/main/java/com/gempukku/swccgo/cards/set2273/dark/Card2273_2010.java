package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_010;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Kessel Surveillance System PC-E3: the PC card image, word for word the current Master List text. (Until the B41 rework this id was an inferred original; the first printing is lost.)
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2273_2010 extends Card1273_010 {
    public Card2273_2010() {
        super();
        setLegacyVersion(new LegacyVersion("1273_10", "PC-E4",
                "PC card image (image server folder Virtual7-Dark, file dated 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
