package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_108;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: •Owen Lars (V)
 * PC-E1 version of 1210_108 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 1 release sheet original (2210_2108) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: three changes: "a card … with a 'crop' card" becomes "one card … with one 'crop' card", and "and" before "cards with "lightsaber" in title" is dropped.
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1108 extends Card1210_108 {
    public Card2210_1108() {
        super();
        setGameText("Opponent activates no Force from Beru Stew. While present with Lars' Vaporator: during your control phase, may use 3 Force to exchange one card from hand with one 'crop' card; cards with 'lightsaber' in title may not deploy here.");
        setLegacyVersion(new LegacyVersion("1210_108", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
