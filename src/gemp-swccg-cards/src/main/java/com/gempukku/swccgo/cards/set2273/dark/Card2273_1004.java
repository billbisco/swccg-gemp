package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_083;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Title: •Garindan (V)
 * PC-E1 version of 1273_4 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 7.3 release sheet original (2273_4) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "weapon destiny" is written "Weapons Destiny" (plural, with capitals).
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1004 extends Card601_083 {
    public Card2273_1004() {
        super();
        setGameText("[Pilot] 1. May deploy -1 as a 'react'; shuffle any Reserve Deck. Ignores Objective deployment restrictions. Unless alone, may place Garindan in Used Pile to cancel a just drawn Weapons Destiny here. [Virtual Block 2] Imperial Domination ignores Garindan.");
        setLegacyVersion(new LegacyVersion("1273_4", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
