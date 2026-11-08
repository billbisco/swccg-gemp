package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** On The Hunt: original version of 1281_12 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_012_Tests {
    @Test
    public void OnTheHuntVersionIdentityAndText() {
        assertVersion("2281_12", "1281_12", "On The Hunt",
                "Deploy on Contract Killers. Assassins are defense value +1. Once per game may retrieve Death Mark. At end of opponent's turn, if their character targeted by Death Mark is at a non-battleground or aboard their starship, opponent loses 3 Force. (Immune to Alter.)",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_12", "1281_12");
    }
}
