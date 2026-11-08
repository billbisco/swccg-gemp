package com.gempukku.swccgo.cards.set2282.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Assassin's Blaster Rifle: original version of 1282_11 (Virtual Block 8.2 release sheet (2012-09-14)). Anything Goes only. */
public class Card_2282_011_Tests {
    @Test
    public void AssassinSBlasterRifleVersionIdentityAndText() {
        assertVersion("2282_11", "1282_11", "Assassin's Blaster Rifle",
                "Deploy on your assassin; assassin is power +1. May target a character at same or adjacent site for free. Draw destiny. If destiny +1 > defense value: target hit, may not be captured this turn, and you may place a bounty on target in Used Pile to retrieve 2 Force.",
                "", java.time.LocalDate.of(2012, 9, 14), java.time.LocalDate.of(2012, 12, 12));
        assertSameCard("2282_11", "1282_11");
    }

    @Test
    public void AssassinsBlasterRifleOriginalHitTargetMayNotBeCapturedOnlyThisTurn() {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<>() {{
                    put("leia", "1_17");
                }},
                new java.util.HashMap<>() {{
                    put("rifle", "2282_11");
                }},
                20, 20,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        scn.StartGame();
        var rifle = (Card2282_011) scn.GetDSCard("rifle").getBlueprint();
        var effect = rifle.mayNotBeCapturedEffect(null, scn.GetDSCard("rifle"), scn.GetLSCard("leia"));
        org.junit.Assert.assertTrue(effect instanceof com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect);
    }
}
