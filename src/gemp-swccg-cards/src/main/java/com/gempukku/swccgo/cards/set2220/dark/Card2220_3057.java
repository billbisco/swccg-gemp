package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_057;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Title: Debris Zone (V)
 * PC-E2 version of 1220_57 (Batch R8-U, Bill 2026-11-11 09:14: PC card images are versions): the PC card image, on the PC
 * server by 2013-02-25 (bulk upload date of its folder; the image may be older), which prints the original words
 * again after the card list (PC-E1, 2220_1057). The Virtual Cards Master List (official text 2013-04-27) prints the same
 * words: PC-E3, the current card. Same text and play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_3057 extends Card1220_057 {
    public Card2220_3057() {
        super();
        setLegacyVersion(new LegacyVersion("1220_57", "PC-E2",
                "PC card image (on the PC server by 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
