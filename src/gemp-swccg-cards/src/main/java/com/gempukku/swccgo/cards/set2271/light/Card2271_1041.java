package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.cards.set1271.light.Card1271_041;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Effect
 * Title: •Oh, He's Not Dead, Not Yet
 * PC-E1 version of 1271_41 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 7.1 release sheet original (2271_41) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: the two arrows swap places ("▲ or ▼ a card"; the list writes "take into hand or deploy a card from Reserve Deck").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_1041 extends Card1271_041 {
    public Card2271_1041() {
        super();
        setGameText("Deploy on table. Once per game, if Vader on table, may [download] Obi-Wan. May place this Effect in Used Pile to [upload] a [Grabber]. If you just attempted to [upload] or [download] a card, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle.");
        setLegacyVersion(new LegacyVersion("1271_41", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
