package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_004;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Praji
 *
 * PC-E2 version of 1210_4: the PC card image (commanderpraji.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2004 extends Card1210_004 {
    public Card2210_2004() {
        super();
        setGameText("Adds 2 to power of anything he pilots. During your control phase (twice if aboard Devastator), may use 1 Force to take an Imperial pilot of ability < 3 into hand from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1210_4", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
