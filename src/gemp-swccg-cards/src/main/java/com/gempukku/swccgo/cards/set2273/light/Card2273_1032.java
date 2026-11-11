package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_032;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Bacta Infirmary
 * PC-E1 version of 1273_32: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1032 extends Card1273_032 {
    public Card2273_1032() {
        super();
        setGameText("If Center of Tyranny on Table, deploy on table. Insurrection is lost. Rogue Squadron pilots of ability < 3 are forfeit +1 and if lost from table, may be placed here. During your control phase may use 2 force to take a character here into hand. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1273_32", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
