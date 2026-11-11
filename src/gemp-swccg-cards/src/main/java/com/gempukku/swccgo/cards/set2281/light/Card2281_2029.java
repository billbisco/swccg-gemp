package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_029;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Het Nkik (V)
 * PC-E2 version of 1281_29: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E3, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2029 extends Card1281_029 {
    public Card2281_2029() {
        super();
        setGameText("Deploys only on Tatooine. While with another Jawa, your Force drains and Force drain bonuses here may not be canceled. During your control phase, may draw X cards from Reserve Deck, where X = the number of deserts you control with a Jawa.");
        setLegacyVersion(new LegacyVersion("1281_29", "PC-E2",
                "PC website card list (by 2012-12-12)",
                LocalDate.of(2012, 12, 12), LocalDate.of(2013, 4, 27)));
    }
}
