package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Title: A Remote Planet (V)
 * PC-E1 version of 1282_20: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_1020 extends Card1282_020 {
    public Card2282_1020() {
        super();
        setGameText("Deploy on The Hyperdrive Generator's Gone: treat '4' on it as '2'. Your Force generation at City Outskirts is +1. May deploy systems regardless of your Objective deployment restrictions. Once per game, may deploy Qui-Gon or Obi-Wan (an/or their matching weapon_ from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1282_20", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
