package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_037;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Hyperwave Scan
 * Original version of 1210_37: the text of the Virtual Block 1 release sheet (2009-12-07), card #37; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2210_1037) changes it: “3 force” is written “3
 * Force”, with a capital F. The Master List of 2013-04-27 prints the original words again; it is listed as its own
 * version (PC-E2, the current card; Bill 2026-11-11: every publication is a version). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2210_037 extends Card1210_037 {
    public Card2210_037() {
        super();
        setGameText("Deploy on table. During opponent's move phase, may use 3 force to peek at opponent's hand or place a Political Effect (and all cards on it) in owner's Used Pile. Unless you have 13 or more cards in hand, opponent may not place those cards in Used Pile. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_37", "",
                "Virtual Block 1 release sheet (2009-12-07), card #37; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
