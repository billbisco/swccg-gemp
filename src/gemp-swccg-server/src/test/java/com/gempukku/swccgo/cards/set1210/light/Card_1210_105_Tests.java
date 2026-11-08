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
        org.junit.Assert.assertEquals("Adds 3 to power of anything he pilots. If piloting lead starfighter, Attack Run total is +3. During your control phase, if piloting at a battleground, may take Darklighter Spin into hand from Reserve Deck; reshuffle or retrieve 1 Force.",
                card.getGameText());
        org.junit.Assert.assertNull(card.getLegacyVersion());
        org.junit.Assert.assertFalse(card.getGameText().contains("one Darklighter Spin"));
    }
}
