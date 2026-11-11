package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Admiral Pellaeon
 *
 * PC-E2 version of 1273_2: the PC card image (admiralpellaeon.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2002 extends Card1273_002 {
    public Card2273_2002() {
        super();
        setGameText("[Pilot] 2. May be revealed from hand to take Justifier into hand from Reserve Deck; reshuffle and deploy both simultaneously (for -1 Force each). Opponent's passengers here are forfeit = 0. Once per game, if aboard Justifier, may lose 1 Force to move away from battle as a 'react' (at hyperspeed +2).");
        setLegacyVersion(new LegacyVersion("1273_2", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
