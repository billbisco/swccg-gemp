package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_258;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Don't Underestimate Our Chances (V)
 * PC-E1 version of 1261_94: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1094 extends Card601_258 {
    public Card2261_1094() {
        super();
        setGameText("USED: Initiate a battle for free. STARTING: If Dantooine Operations on table, deploy from Reserve Deck Operations Center, Dantooine Engineering Corps, and up to two Effects which deploy on table (or your side of table), are always [Immune to Alter], and have no deploy cost. Place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_94", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
