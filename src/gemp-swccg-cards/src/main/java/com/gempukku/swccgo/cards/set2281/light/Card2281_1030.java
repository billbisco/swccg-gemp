package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_030;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Supreme Chancellor Valorum (V)
 * PC-E1 version of 1281_30: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1030 extends Card1281_030 {
    public Card2281_1030() {
        super();
        setGameText("Agendas: order. May be targeted by Might Of The Republic. Your characters with politics (except Wedge and senators) are deploy -1 and gain [Republic] here. During your turn, may use 1 Force to take into hand a card with 'Valorum', 'governor' or 'political' in lore from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1281_30", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
