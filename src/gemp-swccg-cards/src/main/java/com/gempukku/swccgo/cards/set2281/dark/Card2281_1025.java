package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_025;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Mara Jade's Lightsaber (V)
 * PC-E1 version of 1281_25: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1025 extends Card1281_025 {
    public Card2281_1025() {
        super();
        setGameText("Deploy on Mara Jade. May add 1 to Force drain where present. During battle, Mara may use a blaster and this weapon. May target a character or creature for free. Draw two destiny. Target hit, and forfeit = 0, if total destiny > defense value.");
        setLegacyVersion(new LegacyVersion("1281_25", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
