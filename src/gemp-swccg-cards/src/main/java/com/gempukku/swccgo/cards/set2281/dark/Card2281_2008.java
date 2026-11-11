package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Jet Pack (V)
 *
 * PC-E2 version of 1281_8: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2008 extends Card1281_008 {
    public Card2281_2008() {
        super();
        setGameText("Use 2 Force to deploy on your alien warrior (free on Jodo Kast or any Fett). Character 'flies' (landspeed = 3 and maneuver = 6), may fire weapons for free, and may move as a 'react' for +1 Force.");
        setLegacyVersion(new LegacyVersion("1281_8", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
