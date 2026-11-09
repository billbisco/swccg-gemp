package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Aurra Sing, Deadly Assassin: original version of 1281_1 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_001_Tests {
    @Test
    public void AurraSingDeadlyAssassinVersionIdentityAndText() {
        assertVersion("2281_1", "1281_1", "Aurra Sing, Deadly Assassin",
                "Once per game may deploy a device on Aurra from Reserve Deck; reshuffle. While armed with a [Reflections III] rifle: it may not be retargeted or stolen, her total weapon destiny is +1, and characters she hits may not be used to satisfy attrition. Immune to attrition < 3 (< 4 while armed with a rifle).",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_1", "1281_1");
    }

    @Test
    public void AurraSingDeadlyAssassinVersionIsTheSameUniqueCardAsTheCurrentOne() {
        assertCharacterMayNotDeployWhileOtherIsOnTable("2281_1", "1281_1");
    }
}
