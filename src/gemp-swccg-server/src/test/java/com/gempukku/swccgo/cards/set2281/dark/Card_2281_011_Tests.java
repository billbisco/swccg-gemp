package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Join Us Or Die: original version of 1281_11 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_011_Tests {
    @Test
    public void JoinUsOrDieVersionIdentityAndText() {
        assertVersion("2281_11", "1281_11", "Join Us Or Die",
                "Deploy on your Throne Room. While Emperor and Vader here, Take Your Father's Place does not deplete Life Force or cause Force loss. Once per turn, may deploy So Be It, Jedi or a docking bay from Reserve Deck; reshuffle. 'Battle' on Insignificant Rebellion is treated as 'battle or duel.' (Immune to Alter.)",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_11", "1281_11");
    }
}
