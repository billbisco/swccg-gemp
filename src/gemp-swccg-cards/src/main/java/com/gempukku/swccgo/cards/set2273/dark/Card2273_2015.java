package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_015;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Evacuate? (V)
 *
 * PC-E2 version of 1273_15: the PC card image (evacuate.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2015 extends Card1273_015 {
    public Card2273_2015() {
        super();
        setGameText("Use 1 Force to target your character of ability > 3 alone at a site. USED: During any move phase, take target into hand. LOST: If opponent has exactly one non-spy character at target's site, place target in Used Pile.");
        setLegacyVersion(new LegacyVersion("1273_15", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
