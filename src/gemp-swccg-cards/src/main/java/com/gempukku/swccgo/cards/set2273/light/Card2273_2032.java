package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_032;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Bacta Infirmary
 *
 * PC-E2 version of 1273_32: the PC card image (bactainfirmary.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2032 extends Card1273_032 {
    public Card2273_2032() {
        super();
        setGameText("If Center Of Tyranny on table, deploy on table. Insurrection is lost. Rogue Squadron pilots of ability < 3 are forfeit +1 and, if just lost from table, may be placed here. During your control phase, may use 2 Force to take any character from here into hand. [Immune to Alter.]");
        setLegacyVersion(new LegacyVersion("1273_32", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
