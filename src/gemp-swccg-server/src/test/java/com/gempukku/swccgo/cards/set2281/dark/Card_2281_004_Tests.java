package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Bossk With Mortar Gun: original version of 1281_4 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_004_Tests {
    @Test
    public void BosskWithMortarGunVersionIdentityAndText() {
        assertVersion("2281_4", "1281_4", "Bossk With Mortar Gun",
                "Adds 2 to the power of anything he pilots. May not be targeted by weapons unless your other slavers present are each 'hit.' Permanent weapon is •Bossk's Mortar Gun (may target a character for free; draw destiny; target hit, and its forfeit = 0, if destiny = target's destiny number).",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_4", "1281_4");
    }

    @Test
    public void BosskWithMortarGunVersionIsTheSameUniqueCardAsTheCurrentOne() {
        assertCharacterMayNotDeployWhileOtherIsOnTable("2281_4", "1281_4");
    }
}
