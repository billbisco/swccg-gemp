package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Slave I, Symbol Of Fear: original version of 1281_23 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_023_Tests {
    @Test
    public void SlaveISymbolOfFearVersionIdentityAndText() {
        assertVersion("2281_23", "1281_23", "Slave I, Symbol Of Fear",
                "May reveal from hand to ▲ a [Virtual Block 8] Fett and deploy both simultaneously. May add 2 pilots and 2 passengers. Any [Virtual Block 8] Fett deploys -2 aboard. Immune to attrition < 5.",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_23", "1281_23");
    }

    static float fettCostAboard(String slaveId, String fettId) {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<>(),
                new java.util.HashMap<>() {{
                    put("slave", slaveId);
                    put("boba", fettId);
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

    private static final String CLOUD_CITY_BOBA = "5_91";    // Boba Fett (Cloud City): a Fett without the [Virtual Block 8] icon
    private static final String PREPARED_HUNTER = "601_2";   // Boba Fett, Prepared Hunter: a [Virtual Block 8] Fett

    /** Release-sheet slip (batch B20): "Any [Virtual Block 8] Fett deploys -2 aboard" — a Fett without the icon pays full cost. */
    @Test
    public void SlaveISymbolOfFearOriginalOnlyVirtualBlock8FettsDeployMinus2Aboard() {
        float printed = new com.gempukku.swccgo.game.SwccgCardBlueprintLibrary().getSwccgoCardBlueprint(PREPARED_HUNTER).getDeployCost();
        org.junit.Assert.assertEquals(printed - 2, fettCostAboard("2281_23", PREPARED_HUNTER), 0.01f);
        org.junit.Assert.assertEquals(fettCostAboard("1281_23", CLOUD_CITY_BOBA), fettCostAboard("2281_23", CLOUD_CITY_BOBA), 0.01f);
        org.junit.Assert.assertEquals(fettCostAboard("1281_23", PREPARED_HUNTER), fettCostAboard("2281_23", PREPARED_HUNTER), 0.01f);
    }
}
