package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_101;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Han Solo
 *
 * PC-E2 version of 1210_101: the PC card image (hansolo.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2101 extends Card1210_101 {
    public Card2210_2101() {
        super();
        setGameText("[Pilot] 3. Cards with 'Han' in title are destiny +3. May use 1 Force to cancel Boba Fett's or Jabba's game text for remainder of turn. If opponent just initiated battle here, may use 1 Force to take the first weapons segment action.");
        setLegacyVersion(new LegacyVersion("1210_101", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
