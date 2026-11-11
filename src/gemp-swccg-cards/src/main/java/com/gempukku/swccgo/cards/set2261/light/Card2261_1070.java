package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_070;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Obi-Wan, Crazy Wizard
 * PC-E1 version of 1261_70: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1070 extends Card1261_070 {
    public Card2261_1070() {
        super();
        setGameText("Old Ben is a Used Interrupt and may target this site even if not a Tatooine site. Dark Jedi may not 'swing' lightsabers more than once per battle here. When leaving table, place all your cards on Obi-Wan in Used Pile. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1261_70", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
