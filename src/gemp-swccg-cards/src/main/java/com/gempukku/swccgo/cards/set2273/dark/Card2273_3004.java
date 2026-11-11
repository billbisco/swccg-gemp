package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_083;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Title: •Garindan (V)
 * PC-E2 version of 1273_4 (Batch R8-U, Bill 2026-11-11 09:14: PC card images are versions): the PC card image, on the PC
 * server by 2013-02-25 (bulk upload date of its folder; the image may be older), which prints the original words
 * again after the card list (PC-E1, 2273_1004). The Virtual Cards Master List (official text 2013-04-27) prints the same
 * words: PC-E3, the current card. Same text and play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_3004 extends Card601_083 {
    public Card2273_3004() {
        super();
        setLegacyVersion(new LegacyVersion("1273_4", "PC-E2",
                "PC card image (on the PC server by 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
