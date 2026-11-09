package com.gempukku.swccgo.cards.set2281.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Han Solo, Innocent Scoundrel: original version of 1281_27 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_027_Tests {
    @Test
    public void HanSoloInnocentScoundrelVersionIdentityAndText() {
        assertVersion("2281_27", "1281_27", "Han Solo, Innocent Scoundrel",
                "Adds 3 to power of anything he pilots. Draws one battle destiny if unable to otherwise. During battle, may fire a [Virtual Block 8] blaster twice. Once per turn, if opponent is about to draw weapon destiny targeting Han, they draw two and you choose one. Immune to attrition < 3 (< 4 if armed).",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 12, 12));
        assertSameCard("2281_27", "1281_27");
    }
}
