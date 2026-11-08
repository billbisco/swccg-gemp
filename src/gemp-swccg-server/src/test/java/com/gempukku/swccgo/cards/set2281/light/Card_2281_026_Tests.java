package com.gempukku.swccgo.cards.set2281.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Chewbacca, Walking Carpet: original version of 1281_26 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_026_Tests {
    @Test
    public void ChewbaccaWalkingCarpetVersionIdentityAndText() {
        assertVersion("2281_26", "1281_26", "Chewbacca, Walking Carpet",
                "[Pilot] 2. Adds 2 to maneuver of any starfighter he pilots. Your total ability here may not be reduced. Once per game, may lose 1 Force to deploy a droid from Lost Pile. During battle, may target a just-hit character. Target's forfeit is restored to normal.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_26", "1281_26");
    }
}
