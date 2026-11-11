package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set601.light.Card601_063;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Yub Yub, Commander
 * PC-E1 version of 1273_35: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1035 extends Card601_063 {
    public Card2273_1035() {
        super();
        setGameText("USED: Deploy a Rogue Squadron pilot to a location you occupy with a Rogue Squadron pilot from Reserve Deck; reshuffle. \nLOST: Cancel an attempt by opponent to target a Rogue Sqaudron pilot to be captured or excluded from battle. Or if two Rogue Squadron pilots are in battle together, add one battle destiny.");
        setLegacyVersion(new LegacyVersion("1273_35", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
