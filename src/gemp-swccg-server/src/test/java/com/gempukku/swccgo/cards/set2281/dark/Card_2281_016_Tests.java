package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Abyssin Ornament: original version of 1281_16 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_016_Tests {
    @Test
    public void AbyssinOrnamentVersionIdentityAndText() {
        assertVersion("2281_16", "1281_16", "Abyssin Ornament",
                "USED: Cancel an attempt to target your non-droid assassin (or a starship piloted by a non-droid assassin) with a weapon. LOST: Use 1 Force to take a just-lost non-droid assassin into hand.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_16", "1281_16");
    }
}
