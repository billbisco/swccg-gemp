package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_004;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Bossk With Mortar Gun (V)
 *
 * PC-E2 version of 1281_4: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2004 extends Card1281_004 {
    public Card2281_2004() {
        super();
        setGameText("Adds 2 to the power of anything he pilots. May not be targeted by weapons unless your other slavers present are each 'hit.' Permanent weapon is •Bossk's Mortar Gun (may target a character for free; draw destiny; target hit, and its forfeit = 0, if destiny = target's destiny number).");
        setLegacyVersion(new LegacyVersion("1281_4", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
