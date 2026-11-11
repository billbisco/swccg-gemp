package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set601.light.Card601_229;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: •Luke Skywalker (V)
 * PC-E2 version of 1210_105 (Batch R8-U, Bill 2026-11-11 09:14: PC card images are versions): the PC card image, on the PC
 * server by 2013-02-25 (bulk upload date of its folder; the image may be older), which prints the original words
 * again after the card list (PC-E1, 2210_1105). The Virtual Cards Master List (official text 2013-04-27) prints the same
 * words: PC-E3, the current card. Same text and play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_3105 extends Card601_229 {
    public Card2210_3105() {
        super();
        setLegacyVersion(new LegacyVersion("1210_105", "PC-E2",
                "PC card image (on the PC server by 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
