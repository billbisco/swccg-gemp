package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_003;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: •Captain Yorr (V)
 * PC-E1 version of 1230_3 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2230_2003) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2230_3003), which prints the original words again.
 * Against the original: one change: "a" is added ("participating in a battle").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1003 extends Card1230_003 {
    public Card2230_1003() {
        super();
        setGameText("[Pilot] 2, 3: any starfighter. If your starship participating in a battle is about to be excluded (even if that starship may not participate in battle) or made lost by opponent, you may choose for it to be 'hit' instead.");
        setLegacyVersion(new LegacyVersion("1230_3", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
