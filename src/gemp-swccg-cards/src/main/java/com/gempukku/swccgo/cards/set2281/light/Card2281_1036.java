package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_036;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Scoundrel's Ingenuity
 * PC-E1 version of 1281_36: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1036 extends Card1281_036 {
    public Card2281_1036() {
        super();
        setGameText("Deploy from under Scoundrel's Luck on a scoundrel. Scoundrel is power +2 and adds 1 to his total weapon destiny. During your control phase, may draw destiny. If destiny > 3, your Force drains at same and related locations are +1 for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1281_36", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
