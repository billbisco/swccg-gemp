package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_099;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Alien
 * Title: Debnoli
 *
 * PC-E2 version of 1210_99: the PC card image (debnoli.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2099 extends Card1210_099 {
    public Card2210_2099() {
        super();
        setGameText("Adds 2 to power of anything he pilots. If with an Imperial, you may initiate battle here for free. Permanent weapon is blaster rifle (may target a character or creature for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value).");
        setLegacyVersion(new LegacyVersion("1210_99", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
