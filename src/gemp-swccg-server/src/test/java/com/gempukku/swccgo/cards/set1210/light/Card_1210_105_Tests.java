package com.gempukku.swccgo.cards.set1210.light;

public class Card_1210_105_Tests {

    @org.junit.Test
    public void LukeSkywalkerHasTheLatestText() {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<String, String>() {{ put("latest", "1210_105"); }},
                new java.util.HashMap<String, String>() {{  }},
                10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var card = scn.GetLSCard("latest").getBlueprint();
        org.junit.Assert.assertEquals("[Pilot] 3. If piloting lead starfighter, Attack Run total is +3. During your control phase, if piloting at a battleground, may [upload] one Darklighter Spin or retrieve 1 Force.",
                card.getGameText());
        org.junit.Assert.assertNull(card.getLegacyVersion());
    }
}
