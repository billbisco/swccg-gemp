package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_001;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Aurra Sing, Deadly Assassin
 * PC-E1 version of 1281_1: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1001 extends Card1281_001 {
    public Card2281_1001() {
        super();
        setGameText("Once per game, deploy a device on Aurra from Reserve Deck; reshuffle. While armed with a [Reflections III] rifle, it may not be retargeted or stolen, her total weapon destiny is +1, and characters she hits may not be used to satisfy attrition. Immune to attrition < 3 (or < 4 if armed with a rifle).");
        setLegacyVersion(new LegacyVersion("1281_1", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
