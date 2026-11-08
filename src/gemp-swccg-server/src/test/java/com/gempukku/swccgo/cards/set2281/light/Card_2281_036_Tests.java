package com.gempukku.swccgo.cards.set2281.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Scoundrel's Ingenuity: original version of 1281_36 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_036_Tests {
    @Test
    public void ScoundrelSIngenuityVersionIdentityAndText() {
        assertVersion("2281_36", "1281_36", "Scoundrel's Ingenuity",
                "Deploy from under Scoundrel's Luck on a scoundrel. Scoundrel is power +2 and his total weapon destiny is +1. During your control phase, may draw destiny. If destiny > 3, your Force drains at same and related locations are +1 for remainder of turn.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_36", "1281_36");
    }
}
