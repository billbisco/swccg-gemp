package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_015;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Title: Captain Verrack (V)
 * PC-E1 version of 1282_15: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_1015 extends Card1282_015 {
    public Card2282_1015() {
        super();
        setGameText("Adds 1 to power of anything he pilots. While aboard your starship, adds 1 to its total weapon destiny. Whenever you hit a starship here, opponent loses 1 Force. During battle, may lose 1 Force to cancel [Coruscant] Watch Your Back!.");
        setLegacyVersion(new LegacyVersion("1282_15", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
