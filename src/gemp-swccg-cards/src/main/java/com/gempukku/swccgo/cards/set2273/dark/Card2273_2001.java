package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_178;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: 5D6-RA-7 (Fivedesix) (V)
 *
 * PC-E2 version of 1273_1: the PC card image (5d6ra7.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2001 extends Card601_178 {
    public Card2273_2001() {
        super();
        setGameText("[Pilot] 2. Power +1 while with your leader. If at a Scomp link when opponent draws destiny of: 1-3, you may activate 1 Force; 4-6, you may draw top card of your Reserve Deck; 0 or 7, you may retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1273_1", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
