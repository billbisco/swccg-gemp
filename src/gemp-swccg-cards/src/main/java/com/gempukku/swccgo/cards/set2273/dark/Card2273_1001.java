package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_178;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: 5D6-RA-7 (Fivedesix) (V)
 * PC-E1 version of 1273_1: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1001 extends Card601_178 {
    public Card2273_1001() {
        super();
        setGameText("Adds 2 to the power of anything he pilots. Power +1 while with your leader. If at a scomp link when opponent draws a destiny of: 1-3, you may activate one force; 4-6, you may draw top card of your reserve deck; 0 or 7 you may retrieve 1 force.");
        setLegacyVersion(new LegacyVersion("1273_1", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
