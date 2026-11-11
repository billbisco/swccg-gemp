package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_153;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Title: •Shocking Information (V)
 * PC-E1 version of 1220_153 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 2 release sheet original (2220_2153) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "▲ or ▼ a character" becomes "▲ a character, ▼ a character" (the character is named twice), with a comma added before "or just drew". The list writes the icons out as "took a character into hand from Reserve Deck, deployed a character from Reserve Deck".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1153 extends Card1220_153 {
    public Card2220_1153() {
        super();
        setGameText("Unless you have a senate majority, if opponent just [upload] a character, [download] a character (except with an Effect with 'matching' in game text), or just drew a card from Reserve Deck using a character's game text, they must place 2 other cards from hand on Used Pile (if possible).");
        setLegacyVersion(new LegacyVersion("1220_153", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
