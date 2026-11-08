package com.gempukku.swccgo.cards.set2282.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** You Will Take Me To Jabba Now: original version of 1282_25 (Virtual Block 8.2 release sheet (2012-09-14)). Anything Goes only. */
public class Card_2282_025_Tests {
    @Test
    public void YouWillTakeMeToJabbaNowVersionIdentityAndText() {
        assertVersion("2282_25", "1282_25", "You Will Take Me To Jabba Now",
                "When you play this Interrupt, may place a Rep stacked on your Objective in Used Pile. Deploy Seeking An Audience or an alien to Audience Chamber (for -1 Force) from Reserve Deck; reshuffle. OR Reveal up to 3 aliens stacked on I Know That Laugh or from hand (X = number of aliens revealed); activate X Force.",
                "", java.time.LocalDate.of(2012, 9, 14), java.time.LocalDate.of(2012, 12, 12));
        assertSameCard("2282_25", "1282_25");
    }
}
