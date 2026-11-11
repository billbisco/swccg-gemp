package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_016;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Abyssin Ornament (V)
 *
 * PC-E2 version of 1281_16: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2016 extends Card1281_016 {
    public Card2281_2016() {
        super();
        setGameText("USED: Cancel an attempt to target your non-droid assassin (or a starship piloted by a non-droid assassin) with a weapon. LOST: Use 1 Force to take a just-lost non-droid assassin into hand.");
        setLegacyVersion(new LegacyVersion("1281_16", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
