package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** The Inner Circle: original version of 1281_13 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_013_Tests {
    @Test
    public void TheInnerCircleVersionIdentityAndText() {
        assertVersion("2281_13", "1281_13", "The Inner Circle",
                "Deploy on Conference Room (only at start of game); ▼ Sim Aloo here and [Virtual Block 4] There is No Try. Players initiate battles here for free. Once per game may ▼ Ord Mantell. May not be canceled (except by opponent) or placed in Used Pile. Immune to Ineffective Maneuver.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_13", "1281_13");
    }
}
