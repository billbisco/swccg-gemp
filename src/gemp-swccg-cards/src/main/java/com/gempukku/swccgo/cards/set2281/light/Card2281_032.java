package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_032;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Imperial Navigation Charts PC-E3: the Master List (mandatory steal), word for word PC-E1 and the current PC card image. (Until the B41 rework this id was an inferred optional original; the first printing is lost.)
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2281_032 extends Card1281_032 {
    public Card2281_032() {
        super();
        setLegacyVersion(new LegacyVersion("1281_32", "PC-E4",
                "Players Committee Virtual Cards Master List (official text as of 2013-04-27)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
