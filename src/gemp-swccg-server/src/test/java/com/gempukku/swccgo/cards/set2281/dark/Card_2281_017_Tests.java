package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Sonic Bombardment: original version of 1281_17 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_017_Tests {
    @Test
    public void SonicBombardmentVersionIdentityAndText() {
        assertVersion("2281_17", "1281_17", "Sonic Bombardment",
                "Once per game ▲ a prison. OR Use 1 Force to ▲ a non-droid assassin (or slaver) of ability < 5. OR During battle involving an assassin, opponent's battle destiny draws are -1. OR Cancel opponent's attempt to modify the power of a starfighter piloted by your alien.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_17", "1281_17");
    }
}
