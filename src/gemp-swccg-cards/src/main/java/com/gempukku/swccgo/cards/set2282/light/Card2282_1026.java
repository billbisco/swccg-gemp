package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Title: Under Jabba's Protection
 * PC-E1 version of 1282_26: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_1026 extends Card1282_026 {
    public Card2282_1026() {
        super();
        setGameText("When you play this Interrupt, may place a Rep on your objective in Used Pile. Lose X cards stacked on I Know That Laugh to reduce a weapon or battle destiny total by X. OR Use 1 Force to: take a card stacked on I Know That Laugh in hand (or stack top card of lost pile on it).");
        setLegacyVersion(new LegacyVersion("1282_26", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
