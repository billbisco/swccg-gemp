package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Ket Maliss, Shadow Killer: original version of 1281_5 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_005_Tests {
    @Test
    public void KetMalissShadowKillerVersionIdentityAndText() {
        assertVersion("2281_5", "1281_5", "Ket Maliss, Shadow Killer",
                "Adds 2 to power of anything he pilots. While with opponent's character of ability > 2, Ket Maliss is power +1, defense value +2, and draws one battle destiny if unable to otherwise. Whenever an opponent's character is lost from same site, opponent loses 1 Force.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_5", "1281_5");
    }

    @Test
    public void KetMalissShadowKillerVersionIsTheSameUniqueCardAsTheCurrentOne() {
        assertCharacterMayNotDeployWhileOtherIsOnTable("2281_5", "1281_5");
    }
}
