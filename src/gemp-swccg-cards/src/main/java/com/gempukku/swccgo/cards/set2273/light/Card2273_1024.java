package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set601.light.Card601_222;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Derek 'Hobbie' Klivian (V)
 * PC-E1 version of 1273_24: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1024 extends Card601_222 {
    public Card2273_1024() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While piloting Rogue 4 (or any X-Wing), draws one battle destiny if unable to otherwise. Permanent weapon is Rogue Blaster (may target a character or creature for free; draw destiny; target hit and it's forfeit = 0, if destiny + 1 > defense value).");
        setLegacyVersion(new LegacyVersion("1273_24", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
