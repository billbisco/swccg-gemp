package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_063;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Lost
 * Title: Local Trouble
 *
 * PC-E2 version of 1210_63: the PC card image (localtrouble.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2063 extends Card1210_063 {
    public Card2210_2063() {
        super();
        setGameText("At the beginning of your battle phase, use 1 Force to initiate a battle between two Imperials and an opponent's character present at same interior site. For remainder of turn, no other battles may occur here and opponent takes no battle damage here.");
        setLegacyVersion(new LegacyVersion("1210_63", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
