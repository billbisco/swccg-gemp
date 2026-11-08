package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Mara Jade's Lightsaber: original version of 1281_25 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_025_Tests {
    @Test
    public void MaraJadeSLightsaberVersionIdentityAndText() {
        assertVersion("2281_25", "1281_25", "Mara Jade's Lightsaber",
                "Deploy on Mara Jade. May add 1 to Force drain where present. During battle, Mara may use a blaster and this weapon. May target a character or creature for free. Draw two destiny. Target hit, and its forfeit = 0, if total destiny > defense value.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_25", "1281_25");
    }
}
