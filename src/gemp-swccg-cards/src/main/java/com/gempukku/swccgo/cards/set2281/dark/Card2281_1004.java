package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_004;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Bossk With Mortar Gun (V)
 * PC-E1 version of 1281_4: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1004 extends Card1281_004 {
    public Card2281_1004() {
        super();
        setGameText("Adds 2 to the power of anything he pilots. May not be targeted by weapons unless your other slavers present are each 'hit'. Permanent weapon is •Bossk's Mortar Gun (may target a character for free. Draw destiny. Target hit and forfeit =0, if destiny matches target's destiny number).");
        setLegacyVersion(new LegacyVersion("1281_4", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
