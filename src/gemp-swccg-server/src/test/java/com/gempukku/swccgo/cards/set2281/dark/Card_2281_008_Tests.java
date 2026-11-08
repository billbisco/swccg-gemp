package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Jet Pack: original version of 1281_8 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_008_Tests {
    @Test
    public void JetPackVersionIdentityAndText() {
        assertVersion("2281_8", "1281_8", "Jet Pack",
                "Use 2 Force to deploy on your alien warrior (free on Jodo Kast or any Fett). Character 'flies' (landspeed = 3 and maneuver = 6), may fire weapons for free, and may move as a 'react' for +1 Force.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_8", "1281_8");
    }
}
