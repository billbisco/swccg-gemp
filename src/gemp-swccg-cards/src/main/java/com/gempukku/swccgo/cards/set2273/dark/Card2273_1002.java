package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Admiral Pellaeon
 * PC-E1 version of 1273_2: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1002 extends Card1273_002 {
    public Card2273_1002() {
        super();
        setGameText("Adds 2 to power of anything he pilots. May be reveled from hand to take Justifier into hand from reserve deck; reshuffle and deploy both simultaneously (for -1 force each). Opponent's passengers here are forfeit = 0. Once per game, if aboard Justifier may lose one force to move away from battle as a react (at hyperspeed +2).");
        setLegacyVersion(new LegacyVersion("1273_2", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
