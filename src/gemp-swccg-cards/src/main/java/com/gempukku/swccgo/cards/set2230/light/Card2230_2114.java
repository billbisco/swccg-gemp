package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_114;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tydirium
 *
 * PC-E2 version of 1230_114: the PC card image (tydirium.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2114 extends Card1230_114 {
    public Card2230_2114() {
        super();
        setGameText("May deploy (with a pilot) without presence or Force icons. May add 2 pilots and 6 passengers. May forfeit a scout aboard to cancel all remaining battle damage against you here.");
        setLegacyVersion(new LegacyVersion("1230_114", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
