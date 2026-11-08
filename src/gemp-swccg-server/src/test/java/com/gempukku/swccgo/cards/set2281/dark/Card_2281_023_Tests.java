package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Slave I, Symbol Of Fear: original version of 1281_23 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_023_Tests {
    @Test
    public void SlaveISymbolOfFearVersionIdentityAndText() {
        assertVersion("2281_23", "1281_23", "Slave I, Symbol Of Fear",
                "May reveal from hand to take a Fett from Reserve Deck; reshuffle; and deploy both simultaneously. May add 2 pilots and 2 passengers. Any Fett deploys -2 aboard. Immune to attrition < 5.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_23", "1281_23");
    }

    static float bobaCostAboard(String slaveId) {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<>(),
                new java.util.HashMap<>() {{
                    put("slave", slaveId);
                    put("boba", "5_91"); // Boba Fett (Cloud City): a Fett without the [Virtual Block 8] icon
                    put("system", "1_289");
                }},
                20, 20,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var slave = scn.GetDSCard("slave");
        var boba = scn.GetDSCard("boba");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), slave);
        scn.MoveCardsToDSHand(boba);
        return scn.game().getModifiersQuerying().getDeployCost(scn.game().getGameState(), boba, boba, slave, false, null, false, 0, null, false);
    }

    /** Original: "Any Fett deploys -2 aboard", so a Fett without the [Virtual Block 8] icon deploys -2 too. */
    @Test
    public void SlaveISymbolOfFearOriginalAnyFettDeploysMinus2Aboard() {
        org.junit.Assert.assertEquals(bobaCostAboard("1281_23") - 2, bobaCostAboard("2281_23"), 0.01f);
    }
}
