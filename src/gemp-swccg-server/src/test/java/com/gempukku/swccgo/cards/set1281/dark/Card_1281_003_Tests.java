package com.gempukku.swccgo.cards.set1281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Boba Fett, Prepared Hunter (1281_3): the current (Legacy) card. */
public class Card_1281_003_Tests {
    /** Virtual Block 8.1 sheet text (2012-05-11), the latest and only published version; GEMP showed an unpublished wording. Same play. */
    @Test
    public void BobaFettPreparedHunterCurrentCardIsTheLatestText() {
        assertCurrentCard("1281_3", "Boba Fett, Prepared Hunter",
                "Adds 3 to power of anything he pilots. May be revealed from hand to take Slave I into hand from Reserve Deck; reshuffle; and deploy both simultaneously. While piloting Slave I, it is maneuver +3 and hyperspeed +1. Draws one battle destiny if unable to otherwise (if present at a site, may add a destiny to total power instead).");
    }
}
