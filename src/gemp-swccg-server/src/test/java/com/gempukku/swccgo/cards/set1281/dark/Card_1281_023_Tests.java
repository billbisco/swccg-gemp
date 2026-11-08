package com.gempukku.swccgo.cards.set1281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Slave I, Symbol Of Fear (1281_23): the current (Legacy) card. */
public class Card_1281_023_Tests {
    /** PC website card list text (2012-07-21, PC-E1): the latest version under Bill's option (b). GEMP's earlier text was no published version. */
    @Test
    public void SlaveISymbolOfFearCurrentCardIsTheLatestText() {
        assertCurrentCard("1281_23", "Slave I, Symbol Of Fear",
                "May reveal from hand to take a [Virtual Block 8] Fett from Reserve Deck; reshuffle; and deploy both simultaneously. May add 2 pilots and 2 passengers. Your [Virtual Block 8] Fetts deploy -2 aboard. Immune to attrition < 5.");
    }
}
