package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_022;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: With Thunderous Applause
 * PC-E1 version of 1250_22: the PC website card list text ("If at Senate, at start of your turn may draw 2 cards").
 * Same play as the original. Image: the current card image (no slip printed this text).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1022 extends Card1250_022 {
    public Card2250_1022() {
        setGameText("Deploy on Sidious. If at Senate, at start of your turn may draw 2 cards from Reserve Deck. During your move phase, relocate this Effect to a Rebel or Jedi at a battleground; that character loses all immunity to attrition. If this Effect just lost, relocate it to Sidious.");
        setLegacyVersion(new LegacyVersion("1250_22", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
