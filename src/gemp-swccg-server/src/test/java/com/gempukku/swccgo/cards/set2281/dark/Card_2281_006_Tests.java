package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** R'tic H'weei: original version of 1281_6 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_006_Tests {
    @Test
    public void RTicHWeeiVersionIdentityAndText() {
        assertVersion("2281_6", "1281_6", "R'tic H'weei",
                "Once per game, may ▲ Jawa Blaster or a card with 'sandcrawler' in title or gametext. Once per turn, if present at a Tatooine battleground and there is more than 1 Force in opponent's Force Pile, you may use 1 Force in opponent's Force Pile.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_6", "1281_6");
    }

    @Test
    public void RTicHWeeiVersionIsTheSameUniqueCardAsTheCurrentOne() {
        assertCharacterMayNotDeployWhileOtherIsOnTable("2281_6", "1281_6");
    }
}
