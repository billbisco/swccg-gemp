package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_079;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Alderaan Consular Ship
 *
 * PC-E2 version of 1250_79: the PC card image (alderaanconsularship.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2079 extends Card1250_079 {
    public Card2250_2079() {
        super();
        setGameText("May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Opponent must use +2 Force to initiate battle here. Once per game, may deploy a senator aboard from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1250_79", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
