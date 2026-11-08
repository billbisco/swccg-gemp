package com.gempukku.swccgo.cards.set2282.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Anakin Solo: original version of 1282_12 (Virtual Block 8.2 release sheet (2012-09-14)). Anything Goes only. */
public class Card_2282_012_Tests {
    @Test
    public void AnakinSoloVersionIdentityAndText() {
        assertVersion("2282_12", "1282_12", "Anakin Solo",
                "Power +2 while armed with a lightsaber. May use any 'stolen' lightsaber. Once per turn, where present, may steal an opponent's just-lost lightsaber. Immune to attrition < 3 (< 5 while armed with a 'stolen' lightsaber).",
                "", java.time.LocalDate.of(2012, 9, 14), java.time.LocalDate.of(2012, 12, 12));
        assertSameCard("2282_12", "1282_12");
    }

    @Test
    public void AnakinSoloVersionIsTheSameUniqueCardAsTheCurrentOne() {
        assertCharacterMayNotDeployWhileOtherIsOnTable("2282_12", "1282_12");
    }

    @Test
    public void AnakinSoloOriginalMayStealOutsideBattle() {
        org.junit.Assert.assertTrue(com.gempukku.swccgo.cards.set1282.light.Card_1282_012_Tests.anakinMayStealOutsideBattle("2282_12"));
    }
}
