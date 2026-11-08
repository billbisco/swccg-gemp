package com.gempukku.swccgo.cards.set1291.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Ominous Rumors (1291_12): the current (Legacy) card. */
public class Card_1291_012_Tests {
    /** Virtual Block 9.1 sheet text (2014-01-10), the latest and only published version; GEMP showed an unpublished wording ('at least three'). Same play. */
    @Test
    public void OminousRumorsCurrentCardIsTheLatestText() {
        assertCurrentCard("1291_12", "Ominous Rumors",
                "Deploy on Bunker. Your scouts aboard speeder bikes are defense value = 5 and immune to Clash Of Sabers. Once per turn, if you control Bunker, may deploy an [Endor] Imperial here (except Fenson or Grond) or a forest from Reserve Deck; reshuffle. Canceled if opponent controls three Endor sites. (Immune to Alter.)");
    }
}
