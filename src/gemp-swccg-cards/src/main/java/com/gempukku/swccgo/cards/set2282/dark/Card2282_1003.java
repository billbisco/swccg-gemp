package com.gempukku.swccgo.cards.set2282.dark;

import com.gempukku.swccgo.cards.set1282.dark.Card1282_003;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Title: You May Start Your Landing (V)
 * PC-E1 version of 1282_3: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_1003 extends Card1282_003 {
    public Card2282_1003() {
        super();
        setGameText("If 5th Marker on table, deploy on table. During your control phase, opponent loses 1 Force for each marker site you control with an AT-At (limit 1 unless [Virtual Block 2] Imperial Control on table). Once per game, may take Walker Garrrison and/or 3rd Marker into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1282_3", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
