package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_065;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Ambush (V)
 * PC-E2 version of 1250_65: the PC card image (ambush.gif), dated by its server upload (2013-02-25, a bulk upload; the
 * image may be older). It prints the release-sheet wording again; the Master List of 2013-04-27 (the current card) follows
 * it. Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2065 extends Card1250_065 {
    public Card2250_2065() {
        super();
        setGameText("Use 1 Force to deploy up to two clones from Reserve Deck; reshuffle. OR If your clones occupy three related battlegrounds during your control phase, use 2 Force (free if at Coruscant) to retrieve any one card into hand. OR If your clone in battle, add one battle destiny.");
        setLegacyVersion(new LegacyVersion("1250_65", "PC-E2", "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
