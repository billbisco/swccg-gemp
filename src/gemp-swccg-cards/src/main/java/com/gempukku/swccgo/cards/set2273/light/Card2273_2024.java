package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set601.light.Card601_222;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Derek 'Hobbie' Klivian (V)
 *
 * PC-E2 version of 1273_24: the PC card image (derekhobbieklivian.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2024 extends Card601_222 {
    public Card2273_2024() {
        super();
        setGameText("[Pilot] 2. While piloting Rogue 4 (or any X-wing), draws one battle destiny if unable to otherwise. Permanent weapon is Rogue Blaster (may target a character or creature for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value).");
        setLegacyVersion(new LegacyVersion("1273_24", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
