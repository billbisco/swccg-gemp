package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_010;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Grond
 *
 * PC-E2 version of 1230_10: the PC card image (lieutenantgrond.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2010 extends Card1230_010 {
    public Card2230_2010() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Adds one battle destiny with Colonel Dyer or if driving a swoop. May place Lieutenant Grond in Used Pile to cancel a Force drain at a related location.");
        setLegacyVersion(new LegacyVersion("1230_10", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
