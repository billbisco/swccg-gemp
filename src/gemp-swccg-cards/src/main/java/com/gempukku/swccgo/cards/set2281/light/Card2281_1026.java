package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Chewbacca, Walking Carpet
 * PC-E1 version of 1281_26: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1026 extends Card1281_026 {
    public Card2281_1026() {
        super();
        setGameText("Adds 2 to the power of anything he pilots. Adds 2 to maneuver of any starfighter he pilots. Your total ability here may not be reduced. Once per game, may lose 1 force to deploy a droid from Lost Pile. During battle, target a just hit character. Target's forfeit is restored to normal.");
        setLegacyVersion(new LegacyVersion("1281_26", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
