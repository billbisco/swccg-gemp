package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set1262.dark.Card1262_011;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Title: So Be It, Jedi
 * PC-E1 version of 1262_11: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2262_1011 extends Card1262_011 {
    public Card2262_1011() {
        super();
        setGameText("Deploy on table. When Luke dpeloys using your Objective, he deploys to Vader's site (if possible). Duels initiated with your Objective may not be canceled. Once per game, may take Rise, My Friend into hand from Reserve Deck; reshuffle. If this Effect canceled by opponent, stack it on Insignificant Rebellion.");
        setLegacyVersion(new LegacyVersion("1262_11", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
