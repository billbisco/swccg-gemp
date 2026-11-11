package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Nothing Can Get Through Our Shield (V)
 * PC-E2 version of 1250_1: the Players Committee card image nothingcangetthroughourshield.gif (folder Virtual5-Dark, upload
 * date 2013-02-25; Bill 2026-11-11: the server date places the image). It prints the PC website card-list words (PC-E1,
 * 2250_1001) and plays as that version. The Master List (2013-04-27, PC-E3) repeats the sheet words and is the current
 * card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2001 extends Card2250_1001 {
    public Card2250_2001() {
        super();
        setLegacyVersion(new LegacyVersion("1250_1", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
