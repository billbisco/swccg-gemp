package com.gempukku.swccgo.cards.set2281.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Scoundrel's Luck: original version of 1281_37 (PC card image (Virtual Block 8.1, 2012-05-11)). Anything Goes only. */
public class Card_2281_037_Tests {
    @Test
    public void ScoundrelSLuckVersionIdentityAndText() {
        assertVersion("2281_37", "1281_37", "Scoundrel's Luck",
                "Deploy on Infiltration with three [Scoundrel] cards from Reserve Deck face down under here. Once during your turn, at any time, may deploy a [Scoundrel] Effect from here. If a [Scoundrel] Effect just left table, stack it under here. At the start of your turn, stack all [Scoundrel] Effects under here. Once per turn, may place any card in Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile. While two non-droid scoundrels present at battlegrounds, Their Fire Has Gone Out Of The Universe flips and may not flip back. (Immune to Alter.)",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 18));
        assertSameCard("2281_37", "1281_37");
    }

    @Test
    public void ScoundrelsLuckOriginalDoesNotStackInactiveScoundrelEffectsAtStartOfTurn() {
        var tests = new com.gempukku.swccgo.cards.set1281.light.Card_1281_037_Tests();
        try {
            org.junit.Assert.assertFalse(tests.inactiveScoundrelEffectStackedAtStartOfTurn("2281_37"));
        } finally {
            tests.removeInfiltrationStub();
        }
    }
}
