package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_011;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Join Us Or Die
 * PC-E1 version of 1281_11: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1011 extends Card1281_011 {
    public Card2281_1011() {
        super();
        setGameText("Deploy on your Throne Room. While Emperor and Vader here, Take Your Father's Place does not deplete Life Force or cause Force loss. Once per turn, may deploy a docking bay or So Be It, Jedi from Reserve Deck; reshuffle. 'Battle' on Insignificant Rebellion is treated as 'battle or duel. (Immune to Alter).");
        setLegacyVersion(new LegacyVersion("1281_11", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
