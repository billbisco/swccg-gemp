package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_055;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used
 * Title: Collateral Damage
 *
 * PC-E2 version of 1210_55: the PC card image (collateraldamage.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2055 extends Card1210_055 {
    public Card2210_2055() {
        super();
        setGameText("If you just initiated battle where you have at least two characters (one a trooper and the other a leader), add two battle destiny. Opponent takes no battle damage this battle. OR ▼ one Dewback");
        setLegacyVersion(new LegacyVersion("1210_55", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
