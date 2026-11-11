package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Jet Pack (V)
 * PC-E1 version of 1281_8: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1008 extends Card1281_008 {
    public Card2281_1008() {
        super();
        setGameText("Use 2 Force to deploy on your alien warrior (free Jodo Kast or any Fett). Character 'flies' (landspeed = 3 and maneuver = 6), may fire weapons for free, and may move as a 'react' for +1 Force.");
        setLegacyVersion(new LegacyVersion("1281_8", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
