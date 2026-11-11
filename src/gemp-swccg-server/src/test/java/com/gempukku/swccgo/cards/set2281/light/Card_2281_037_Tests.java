package com.gempukku.swccgo.cards.set2281.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Scoundrel's Luck: PC-E3 version of 1281_37 (Master List 2013-04-27, "(even if inactive)"); the PC card image of 2013-05-07 is current. Anything Goes only. */
public class Card_2281_037_Tests {
    @Test
    public void ScoundrelSLuckVersionIdentityAndText() {
        assertVersion("2281_37", "1281_37", "Scoundrel's Luck",
                "Deploy on Infiltration with three [Scoundrel] cards from Reserve Deck face down under here. Once during your turn, at any time, may deploy a [Scoundrel] Effect from here. If a [Scoundrel] Effect just left table, stack it under here. At the start of your turn, stack all [Scoundrel] Effects under here (even if inactive). Once per turn, may place any card in Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile. While two non-droid scoundrels present at battlegrounds, Their Fire Has Gone Out Of The Universe flips and may not flip back. (Immune to Alter.)",
                "PC-E3", java.time.LocalDate.of(2013, 4, 27), java.time.LocalDate.of(2013, 5, 7));
        assertSameCard("2281_37", "1281_37");
    }

    @Test
    public void ScoundrelsLuckMasterListStacksInactiveScoundrelEffectsAtStartOfTurn() {
        var tests = new com.gempukku.swccgo.cards.set1281.light.Card_1281_037_Tests();
        try {
            org.junit.Assert.assertTrue(tests.inactiveScoundrelEffectStackedAtStartOfTurn("2281_37"));
        } finally {
            tests.removeInfiltrationStub();
        }
    }
}
