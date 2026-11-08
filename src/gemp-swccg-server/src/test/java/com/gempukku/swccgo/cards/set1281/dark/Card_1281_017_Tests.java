package com.gempukku.swccgo.cards.set1281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Sonic Bombardment (1281_17): the current (Legacy) card. */
public class Card_1281_017_Tests {
    /** PC website card list text (2012-07-21, PC-E1): the latest version under Bill's option (b). GEMP's earlier text was no published version. */
    @Test
    public void SonicBombardmentCurrentCardIsTheLatestText() {
        assertCurrentCard("1281_17", "Sonic Bombardment",
                "Once per game, may take into hand a prison from Reserve Deck; reshuffle. OR Use 1 Force to take into hand a non-droid assassin or slaver of ability < 5 from Reserve Deck; reshuffle. OR During battle involving an assassin, each of opponent's battle destiny draws is -1. OR Cancel opponent's attempt to modify the power of a starship piloted by your alien.");
    }
}
