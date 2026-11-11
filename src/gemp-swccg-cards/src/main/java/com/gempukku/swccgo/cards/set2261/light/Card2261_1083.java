package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_083;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Mon Calamari Dockyards
 * PC-E1 version of 1261_83: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1083 extends Card1261_083 {
    public Card2261_1083() {
        super();
        setGameText("Deploy on table. Star Cruisers (except Home One) are deploy -2 (to a maximum of -3), ignore deployment restrictions in their game text, draw one battle destiny if not able to otherwise, and are immune to attrition < 4. Capital Support is canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_83", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
