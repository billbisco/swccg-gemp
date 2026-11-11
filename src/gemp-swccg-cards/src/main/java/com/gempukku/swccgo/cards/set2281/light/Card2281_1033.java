package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_033;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Redeemed Apprentice
 * PC-E1 version of 1281_33: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1033 extends Card1281_033 {
    public Card2281_1033() {
        super();
        setGameText("If you just forfeited a character (or opponent just drew destiny for Elis Helrot) deploy on table. Whenever you must lose Force from a [Virtual Block 4] objective targeting your non-jedi character, reduce that loss by 1. Elis Herlot and Nabrun Leids are canceled. (Immune to Control).");
        setLegacyVersion(new LegacyVersion("1281_33", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
