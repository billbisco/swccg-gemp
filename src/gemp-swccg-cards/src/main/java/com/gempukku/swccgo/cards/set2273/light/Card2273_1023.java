package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_023;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Captain Antilles
 * PC-E1 version of 1273_23: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1023 extends Card1273_023 {
    public Card2273_1023() {
        super();
        setGameText("Adds 2 to the power of anything he pilots, 3: any corvette. While piloting a corvette it is immune to attrition <5 and, once per turn, may peek at a card in opponent's hand. Once per game, during battle (unless Vader here) may use 2 force to move ship away (for free).");
        setLegacyVersion(new LegacyVersion("1273_23", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
