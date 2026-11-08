package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_039;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Epic Event
 * Title: Epic Duel (V)
 * Original version of 1261_39: the Virtual Block 6.1 release sheet text, "Loser (lower total)" (the PC card list,
 * PC-E1 and now the current card, says "lowest"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_039 extends Card1261_039 {
    public Card2261_039() {
        setGameText("Deploy on table. A Jedi's Resilience may not be played during move phases. The Circle Is Now Complete or Vader's Obsession may be played during any move phase to initiate an epic duel as follows: If Vader present with specified target, each player draws two destiny (if armed with a lightsaber, each destiny is +1); add ability. Loser (lower total) is lost, losing player loses 2 Force, and winning player may retrieve 2 Force.");
        setLegacyVersion(new LegacyVersion("1261_39", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #41", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
