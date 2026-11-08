package com.gempukku.swccgo.cards.set2282.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Captain Verrack: original version of 1282_15 (Virtual Block 8.2 release sheet (2012-09-14)). Anything Goes only. */
public class Card_2282_015_Tests {
    @Test
    public void CaptainVerrackVersionIdentityAndText() {
        assertVersion("2282_15", "1282_15", "Captain Verrack",
                "Adds 1 to power of anything he pilots. While aboard your starship, its total weapon destiny is +1. Whenever you hit a starship here, opponent loses 1 Force. During battle, may lose 1 Force to cancel [Coruscant] Watch Your Back!.",
                "", java.time.LocalDate.of(2012, 9, 14), java.time.LocalDate.of(2012, 12, 12));
        assertSameCard("2282_15", "1282_15");
    }

    @Test
    public void CaptainVerrackVersionIsTheSameUniqueCardAsTheCurrentOne() {
        assertCharacterMayNotDeployWhileOtherIsOnTable("2282_15", "1282_15");
    }
}
