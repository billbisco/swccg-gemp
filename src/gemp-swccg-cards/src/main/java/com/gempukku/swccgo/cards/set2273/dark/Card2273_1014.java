package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_014;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: A Dark Time For The Rebellion (V)
 * PC-E1 version of 1273_14: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1014 extends Card1273_014 {
    public Card2273_1014() {
        super();
        setGameText("If opponent went first this game, retrieve 1 force when you play this Interrupt. Add or subtract 1 from opponent's just drwn destiny. OR Activate up to 2 Force. Or take Control into hand from Reserve Deck; reshuffle. OR Until end of turn, battle destiny draws may not be modified or canceled (unless being redrawn).");
        setLegacyVersion(new LegacyVersion("1273_14", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
