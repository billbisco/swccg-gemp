package com.gempukku.swccgo.cards.set1220.dark;

public class Card_1220_058_Tests {

    @org.junit.Test
    public void DefensiveFireHasTheLatestText() {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<String, String>() {{  }},
                new java.util.HashMap<String, String>() {{ put("latest", "1220_58"); }},
                10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var card = scn.GetDSCard("latest").getBlueprint();
        org.junit.Assert.assertEquals("If opponent just initiated battle, you may immediately fire one non-lightsaber, non-Permanent weapon present in that battle once (for free), adding 2 to each of its destiny draws. That weapon may fire again this battle.",
                card.getGameText());
        org.junit.Assert.assertNull(card.getLegacyVersion());
        org.junit.Assert.assertFalse(card.getGameText().contains("each weapon destiny is +2"));
    }
}
