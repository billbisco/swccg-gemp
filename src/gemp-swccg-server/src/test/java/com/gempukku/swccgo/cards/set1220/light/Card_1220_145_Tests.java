package com.gempukku.swccgo.cards.set1220.light;

public class Card_1220_145_Tests {

    @org.junit.Test
    public void ImpressiveMostImpressiveHasTheLatestText() {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<String, String>() {{ put("latest", "1220_145"); }},
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
        org.junit.Assert.assertEquals("Cancel All Too Easy, Imperial Barrier (targeting your Jedi), Stunning Leader (where your Jedi participating), or You Are Beaten (except when canceling Uncontrollable Fury). [Immune to Sense] OR Once per game, if opponent just initiated a battle, you may take the first weapons segment action.",
                card.getGameText());
        org.junit.Assert.assertNull(card.getLegacyVersion());
        // batch R8: the current text is the Master List wording again
        org.junit.Assert.assertTrue(card.getGameText().contains("[Immune to Sense]"));
    }
}
