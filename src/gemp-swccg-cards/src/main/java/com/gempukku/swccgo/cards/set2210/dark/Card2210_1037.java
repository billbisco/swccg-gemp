package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_037;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Hyperwave Scan
 * PC-E1 version of 1210_37: the PC website card list text (by 2012-07-21; Virtual Block 1 list and Virtual master
 * list to 2013-01-11). Against the original (2210_37): “3 force” is written “3 Force”, with a capital F. The Master
 * List of 2013-04-27 (PC-E2, the current card) prints the original words again (swccg-guild/work#236; Bill
 * 2026-11-11: every publication is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1037 extends Card1210_037 {
    public Card2210_1037() {
        super();
        setGameText("Deploy on table. During opponent's move phase, may use 3 Force to peek at opponent's hand or place a Political Effect (and all cards on it) in owner's Used Pile. Unless you have 13 or more cards in hand, opponent may not place those cards in Used Pile. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_37", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
