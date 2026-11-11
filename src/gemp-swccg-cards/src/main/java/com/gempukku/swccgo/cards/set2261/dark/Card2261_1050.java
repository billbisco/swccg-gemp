package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_050;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: TIE Sentry Ships (V)
 * PC-E1 version of 1261_50: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1050 extends Card1261_050 {
    public Card2261_1050() {
        super();
        setGameText("USED: If you just drew a unique (•) Imperial starship for destiny, take that starship into hand to cancel and redraw the destiny. LOST: Use 3 Force to retrieve a unique (•) Imperial starship into hand.");
        setLegacyVersion(new LegacyVersion("1261_50", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
