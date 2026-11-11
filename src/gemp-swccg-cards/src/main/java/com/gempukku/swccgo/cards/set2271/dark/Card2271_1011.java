package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_011;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Effect
 * Title: •I'll Take Them Myself
 * PC-E1 version of 1271_11 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2271_2011) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2271_3011), which prints the original words again.
 * Against the original: one change: "this Effect" becomes "this card" ("May place this card in Used Pile").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1011 extends Card1271_011 {
    public Card2271_1011() {
        super();
        setGameText("Deploy on table. Once per game, may [upload] a [Setup] card. May place this card in Used Pile to [upload] an Immediate Effect. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1271_11", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
