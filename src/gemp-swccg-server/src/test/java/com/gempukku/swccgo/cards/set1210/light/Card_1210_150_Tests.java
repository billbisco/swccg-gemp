package com.gempukku.swccgo.cards.set1210.light;

public class Card_1210_150_Tests {

    @org.junit.Test
    public void DontTreadOnMeHasTheLatestText() {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<String, String>() {{ put("latest", "1210_150"); }},
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
        org.junit.Assert.assertEquals("USED: Use 1 Force to deploy an Effect that deploys for free on table (or on your side of table) and is always Immune to Alter from Reserve Deck; reshuffle. STARTING: When drawing your starting hand, draw up to 12 cards instead of 8. Place this Interrupt in Reserve Deck.",
                card.getGameText());
        org.junit.Assert.assertNull(card.getLegacyVersion());
        org.junit.Assert.assertFalse(card.getGameText().contains("(or your side of table)"));
    }
}
