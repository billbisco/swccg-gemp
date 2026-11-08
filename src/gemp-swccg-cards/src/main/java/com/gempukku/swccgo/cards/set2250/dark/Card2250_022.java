package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_022;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: With Thunderous Applause
 * PC-E1 version of 1250_22: the Virtual Cards Master List text (2013-04-27), "loses any immunity to attrition"
 * (the Dec 2009 sheet, which GEMP's current card shows, says "all"). Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_022 extends Card1250_022 {
    public Card2250_022() {
        setGameText("Deploy on Sidious. At start of your turn, if at Senate, may draw 2 cards from Reserve Deck. During your move phase, relocate this Effect to a Rebel or Jedi at a battleground; that character loses any immunity to attrition. If this Effect just lost, relocate it to Sidious.");
        setLegacyVersion(new LegacyVersion("1250_22", "PC-E1",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), null));
    }
}
