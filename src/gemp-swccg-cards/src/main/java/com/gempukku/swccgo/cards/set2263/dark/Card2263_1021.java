package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set1263.dark.Card1263_021;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Title: Droid Walker
 * PC-E1 version of 1263_21: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_1021 extends Card1263_021 {
    public Card2263_1021() {
        super();
        setGameText("Deploys -1 to same site as your MTT. AT-AT Cannon may deploy aboard, and targets it hits cannot be used to satisfy attrition. Unless alone, draws one battle destiny if not able to otherwise.");
        setLegacyVersion(new LegacyVersion("1263_21", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
