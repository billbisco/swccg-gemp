package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_056;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Coordinated Attack
 *
 * PC-E2 version of 1210_56: the PC card image (coordinatedattack.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2056 extends Card1210_056 {
    public Card2210_2056() {
        super();
        setGameText("USED: If you just hit a target with a non-lightsaber, non-[Permanent Weapon] weapon, place target (and all cards on it) in owner's Used Pile. LOST: Cancel Heroic (and any immunity to attrition) on a character or starship this turn.");
        setLegacyVersion(new LegacyVersion("1210_56", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
