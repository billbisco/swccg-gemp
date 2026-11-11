package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_066;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Title: •Shocking Revelation (V)
 * PC-E1 version of 1220_66 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 2 release sheet original (2220_2066) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "a" before "character" is dropped. The list writes the two icons out as "took into hand or deployed character … from Reserve Deck".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1066 extends Card1220_066 {
    public Card2220_1066() {
        super();
        setGameText("Unless you have a senate majority, if opponent just [upload] or [download] character (except with an Effect with 'matching' in game text) or just drew a card from Reserve Deck using a character's game text, they must place 2 other cards from hand on Used Pile (if possible).");
        setLegacyVersion(new LegacyVersion("1220_66", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
