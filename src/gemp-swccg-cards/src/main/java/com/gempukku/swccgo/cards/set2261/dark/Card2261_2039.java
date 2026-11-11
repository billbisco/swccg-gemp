package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_039;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Epic Duel (V)
 *
 * PC-E2 version of 1261_39: the PC card image (epicduel.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2039 extends Card1261_039 {
    public Card2261_2039() {
        super();
        setGameText("Deploy on table. A Jedi's Resilience may not be played during move phases. The Circle Is Now Complete or Vader's Obsession may be played during any move phase to initiate an epic duel as follows: If Vader present with specified target, each player draws two destiny (if armed with a lightsaber, each destiny is +1); add ability. Loser (lower total) is lost, losing player loses 2 Force, and winning player may retrieve 2 Force.");
        setLegacyVersion(new LegacyVersion("1261_39", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
