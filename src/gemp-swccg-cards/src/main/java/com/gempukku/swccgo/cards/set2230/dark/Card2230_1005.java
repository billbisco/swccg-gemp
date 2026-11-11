package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_005;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: •Fozec (V)
 * PC-E1 version of 1230_5 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 3 release sheet original (2230_2005) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "; may retrieve" becomes a new sentence, ". You may retrieve".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1005 extends Card1230_005 {
    public Card2230_1005() {
        super();
        setGameText("[Pilot] 2. While Undercover, opponent's Force drains at related locations are -1. Once during battle, if with an ISB agent, may cause opponent to draw destiny. You may retrieve 2 Force if destiny > 2.");
        setLegacyVersion(new LegacyVersion("1230_5", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
