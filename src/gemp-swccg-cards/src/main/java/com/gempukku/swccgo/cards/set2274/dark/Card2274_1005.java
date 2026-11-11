package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.cards.set1274.dark.Card1274_005;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Title: Desperate Counter (v)
 * PC-E1 version of 1274_5: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_1005 extends Card1274_005 {
    public Card2274_1005() {
        super();
        setGameText("Unless Imperial Propaganda on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 4 (ignore other modifiers); opponent may immediately take any one card into hand from Reserve Deck; reshuffle. [Immune to Control]. OR Use 2 Force to deploy on table and stack one just-played interrupt here.");
        setLegacyVersion(new LegacyVersion("1274_5", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
