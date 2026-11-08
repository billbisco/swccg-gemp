package com.gempukku.swccgo.cards.set2281.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Supreme Chancellor Valorum: original version of 1281_30 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_030_Tests {
    @Test
    public void SupremeChancellorValorumVersionIdentityAndText() {
        assertVersion("2281_30", "1281_30", "Supreme Chancellor Valorum",
                "Agenda: order. May be targeted by Might Of The Republic. Your characters with politics (except Wedge and senators) are deploy -1 (and gain [Republic]) here. During your turn, may use 1 Force to take into hand from Reserve Deck a card with 'governor,' 'political,' or 'Valorum' in lore; reshuffle.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_30", "1281_30");
    }

    @Test
    public void SupremeChancellorValorumVersionIsTheSameUniqueCardAsTheCurrentOne() {
        assertCharacterMayNotDeployWhileOtherIsOnTable("2281_30", "1281_30");
    }
}
