package com.gempukku.swccgo.cards.set2274.light;

import com.gempukku.swccgo.cards.set1274.light.Card1274_017;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Type: Device
 * Title: •Motivator
 * PC-E2 version of 1274_17 (Batch R8-U, Bill 2026-11-11 09:14: PC card images are versions): the PC card image, on the PC
 * server by 2013-02-25 (bulk upload date of its folder; the image may be older), which prints the original words
 * again after the card list (PC-E1, 2274_1017). The Virtual Cards Master List (official text 2013-04-27) prints the same
 * words: PC-E3, the current card. Same text and play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_3017 extends Card1274_017 {
    public Card2274_3017() {
        super();
        setLegacyVersion(new LegacyVersion("1274_17", "PC-E2",
                "PC card image (on the PC server by 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
