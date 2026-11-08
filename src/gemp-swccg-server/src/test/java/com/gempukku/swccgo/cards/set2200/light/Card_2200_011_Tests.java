package com.gempukku.swccgo.cards.set2200.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** There is Another: original version of 1200_11 (PC website card list (2012-07-21)). Anything Goes only. */
public class Card_2200_011_Tests {
    @Test
    public void ThereIsAnotherVersionIdentityAndText() {
        assertVersion("2200_11", "1200_11", "There is Another",
                "Unless you have deployed a Skywalker this game, plays on Table. 'Luke' and 'Son of Vader' on opponent's (Death Star II) cards are treated as 'Leia'and 'Lady Vader,' respectively. Once during your turn, opponent may activate 1 Force.",
                "", java.time.LocalDate.of(2012, 7, 21), java.time.LocalDate.of(2013, 5, 21));
        assertSameCard("2200_11", "1200_11");
    }

    @Test
    public void ThereIsAnotherOriginalAffectsOnlyOpponentsDeathStarIICards() {
        org.junit.Assert.assertFalse(com.gempukku.swccgo.cards.set1200.light.Card_1200_011_Tests.thereIsAnotherAffectsOpponentsNonDeathStarIICard("2200_11"));
    }
}
