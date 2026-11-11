package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_017;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Ket Maliss, Shadow Killer
 * PC-E1 version of 1281_5: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1005 extends Card601_017 {
    public Card2281_1005() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While with opponent's character of ability > 2, Ket Maliss is power +1 and defense value + 2 and draws one battle destiny if unable to otherwise. Whenever opponent's character was lost from same site, opponent loses 1 Force.");
        setLegacyVersion(new LegacyVersion("1281_5", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
