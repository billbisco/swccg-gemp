package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Chewbacca, Walking Carpet
 * Original version of 1281_26 (Virtual Block 8.1 release sheet): the sheet wording, before the PC website card list rephrase (2012-07-21, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_026 extends Card1281_026 {
    public Card2281_026() {
        super();
        setGameText("[Pilot] 2. Adds 2 to maneuver of any starfighter he pilots. Your total ability here may not be reduced. Once per game, may lose 1 Force to deploy a droid from Lost Pile. During battle, may target a just-hit character. Target's forfeit is restored to normal.");
        setLegacyVersion(new LegacyVersion("1281_26", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
