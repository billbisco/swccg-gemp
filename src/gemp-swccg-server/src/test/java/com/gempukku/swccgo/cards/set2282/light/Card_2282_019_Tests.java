package com.gempukku.swccgo.cards.set2282.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Rennek: original version of 1282_19 (Virtual Block 8.2 release sheet (2012-09-14)). Anything Goes only. */
public class Card_2282_019_Tests {
    @Test
    public void RennekVersionIdentityAndText() {
        assertVersion("2282_19", "1282_19", "Rennek",
                "May be stacked on I Know That Laugh from hand, using 2 Force. While stacked on I Know That Laugh, stack face-up and during any battle may draw destiny. If destiny < number of your aliens in battle, reset a participating character's forfeit to 0.",
                "", java.time.LocalDate.of(2012, 9, 14), java.time.LocalDate.of(2012, 12, 12));
        assertSameCard("2282_19", "1282_19");
    }

    @Test
    public void RennekVersionIsTheSameUniqueCardAsTheCurrentOne() {
        assertCharacterMayNotDeployWhileOtherIsOnTable("2282_19", "1282_19");
    }

    @Test
    public void RennekOriginalMayResetACharacterWhoseForfeitMayNotBeReduced() {
        org.junit.Assert.assertTrue(com.gempukku.swccgo.cards.set1282.light.Card_1282_019_Tests.rennekMayChooseCharacterWhoseForfeitMayNotBeReduced("2282_19"));
    }
}
