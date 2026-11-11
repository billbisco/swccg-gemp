package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_079;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Alderaan Consular Ship
 * PC-E1 version of 1250_79: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1079 extends Card1250_079 {
    public Card2250_1079() {
        super();
        setGameText("May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Opponent must use +2 Force to initiate a battle here. Once per game, may deploy a senator aboard from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1250_79", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
