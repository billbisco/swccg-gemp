package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_045;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: •Imperial Tyranny (V)
 * PC-E1 version of 1230_45 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2230_2045) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2230_3045), which prints the original words again.
 * Against the original: three changes: "on table" is added after "Empire's New Order", "the" is added ("at the same site"), and "your" is added ("on top of your Reserve Deck").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1045 extends Card1230_045 {
    public Card2230_1045() {
        super();
        setGameText("USED: [download] Empire's New Order on table. OR If Vader just won a battle at the same site as an alien, place that alien out of play. LOST: If a battle was just initiated where you have a character weapon card, place the top card of your Lost Pile on top of your Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1230_45", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
