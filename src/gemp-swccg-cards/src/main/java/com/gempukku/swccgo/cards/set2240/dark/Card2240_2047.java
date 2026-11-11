package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_047;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Interrupt
 * Subtype: Used
 * Title: Unexpected Interruption (V)
 *
 * PC-E2 version of 1240_47: the PC card image (unexpectedinterruption.gif), dated by its server upload
 * (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of
 * 2013-04-27 (the current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its
 * upload date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2047 extends Card1240_047 {
    public Card2240_2047() {
        super();
        setGameText("If a battle was just initiated, target an opponent's female non-droid character present with Xizor. Draw destiny. If destiny +2 > target's ability, target excluded from battle and adds her power to Xizor's for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1240_47", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
