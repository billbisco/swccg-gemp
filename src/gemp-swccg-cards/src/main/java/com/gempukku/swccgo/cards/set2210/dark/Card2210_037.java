package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_037;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Hyperwave Scan
 * Original version of 1210_37. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at
 * its server upload date): original (2210_37) = the Virtual Block 1 release sheet (2009-12-07), card #37; PC-E1
 * (2210_1037) = the PC website card list (by 2012-07-21): “3 force” is written “3 Force”, with a capital F. PC-E2
 * (2210_2037) = the PC card image (uploaded 2013-02-25; it may have been made earlier), the original words again;
 * PC-E3 = the Master List (2013-04-27), the original words again, the current card. Same play. Anything Goes only
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
