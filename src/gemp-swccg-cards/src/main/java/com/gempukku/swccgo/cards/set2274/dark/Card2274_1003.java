package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.cards.set1274.dark.Card1274_003;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Title: •Baktoid Armor Workshop
 * PC-E1 version of 1274_3 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 7.4 release sheet original (2274_3) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "already" is added ("not already on table").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_1003 extends Card1274_003 {
    public Card2274_1003() {
        super();
        setGameText("Deploy on table. Your [Theed Palace] vehicles are defense value and immunity to attrition +1. May [download] Deployment Orders. Once per game, if Deployment Orders on table, may [download] an exterior battleground site not already on table. Once per game, may retrieve a [Theed Palace] vehicle into hand. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1274_3", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
