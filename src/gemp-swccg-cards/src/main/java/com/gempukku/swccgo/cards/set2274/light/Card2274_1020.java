package com.gempukku.swccgo.cards.set2274.light;

import com.gempukku.swccgo.cards.set1274.light.Card1274_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Title: Temporary Foothold
 * PC-E1 version of 1274_20: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_1020 extends Card1274_020 {
    public Card2274_1020() {
        super();
        setGameText("Deploy on a battleground. While you occupy at least two battlegrounds, your Force drains here may not be canceled. During battle here or at a related location, may place this Effect in Used Pile to draw up to 2 cards from Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1274_20", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
