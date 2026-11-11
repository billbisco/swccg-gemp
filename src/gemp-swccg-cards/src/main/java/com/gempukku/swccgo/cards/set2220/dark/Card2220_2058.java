package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_198;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Defensive Fire (V)
 *
 * PC-E2 version of 1220_58: the PC card image (defensivefire.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2058 extends Card601_198 {
    public Card2220_2058() {
        super();
        setGameText("If opponent just initiated battle, immediately fire a non-lightsaber, non-[Permanent Weapon] weapon present (for free and each weapon destiny is +2). That weapon may fire again this battle.");
        setLegacyVersion(new LegacyVersion("1220_58", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
