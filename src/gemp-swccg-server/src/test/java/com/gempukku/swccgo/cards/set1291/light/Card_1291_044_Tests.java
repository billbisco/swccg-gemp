package com.gempukku.swccgo.cards.set1291.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Jaina Solo (1291_44): the current (Legacy) card. */
public class Card_1291_044_Tests {
    /** Virtual Block 9.1 sheet text (2014-01-10), the latest and only published version; GEMP showed a PC data-error wording ('another Solo'). Same play. */
    @Test
    public void JainaSoloCurrentCardIsTheLatestText() {
        assertCurrentCard("1291_44", "Jaina Solo",
                "Adds 3 to power of anything she pilots. X-wings deploy -1 (and are immune to attrition < 5) here. During your control phase, may take one non-unique X-wing (or Rogue Squadron pilot) into hand from Reserve Deck; reshuffle. Adds one battle destiny with a Skywalker or a Solo. Immune to attrition < 4 (< 6 if with Leia).");
    }
}
