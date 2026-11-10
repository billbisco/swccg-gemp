package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_022;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: With Thunderous Applause
 * Original version of 1250_22: the Virtual Block 5 release sheet text, "loses all immunity to attrition" (the
 * Virtual Cards Master List, PC-E2 and now the current card, says "any"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_022 extends Card1250_022 {
    public Card2250_022() {
        setGameText("Deploy on Sidious. At start of your turn, if at Senate, may draw 2 cards from Reserve Deck. During your move phase, relocate this Effect to a Rebel or Jedi at a battleground; that character loses all immunity to attrition. If this Effect just lost, relocate it to Sidious.");
        setLegacyVersion(new LegacyVersion("1250_22", "",
                "Virtual Block 5 release sheet (2009-12-07), card #25; superseded by 2011-08-26 (PC forum errata)", LocalDate.of(2009, 12, 7), LocalDate.of(2011, 8, 26)));
    }
}
