package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_212;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Starship
 * Subtype: Starfighter
 * Title: Vader's Personal Shuttle (V)
 *
 * PC-E2 version of 1220_78: the PC card image (vaderspersonalshuttle.gif), dated by its server upload (2013-02-25,
 * a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2078 extends Card601_212 {
    public Card2220_2078() {
        super();
        setGameText("May add 2 pilots and 4 passengers. Vader deploys -3 aboard. While Vader piloting at a battleground, maneuver +2 and you lose no Force to Uncontrollable Fury or You Must Confront Vader.");
        setLegacyVersion(new LegacyVersion("1220_78", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
