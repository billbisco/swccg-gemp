package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_102;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Jek Porkins
 *
 * PC-E2 version of 1210_102: the PC card image (jekporkins.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2102 extends Card1210_102 {
    public Card2210_2102() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While piloting Red 6: it is Heroic, immune to attrition < 4, Jek draws one battle destiny if unable to otherwise, and, during your control phase, may use 1 Force to retrieve a Rogue squadron pilot or an X-wing.");
        setLegacyVersion(new LegacyVersion("1210_102", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
