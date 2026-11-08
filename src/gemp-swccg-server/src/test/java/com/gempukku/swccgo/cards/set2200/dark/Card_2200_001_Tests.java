package com.gempukku.swccgo.cards.set2200.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Firepower: original version of 1200_1 (PC website card list (2012-07-21)). Anything Goes only. */
public class Card_2200_001_Tests {
    @Test
    public void FirepowerVersionIdentityAndText() {
        assertVersion("2200_1", "1200_1", "Firepower",
                "Plays on table. If opponent moves from a location you occupy during your turn, they lose 2 Force. At end of opponent's turn, if you control two battlegrounds (a site and system) and opponent deployed a card with ability and did not initiate a battle, you may retrieve 1 Force.",
                "", java.time.LocalDate.of(2012, 7, 21), java.time.LocalDate.of(2013, 5, 21));
        assertSameCard("2200_1", "1200_1");
    }
}
