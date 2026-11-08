package com.gempukku.swccgo.cards.set601.light;

/** 1261_106: current (Legacy) text. */
public class Card_601_250_Tests {
    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // PC card list (PC-E1, by 2012-07-21) is the latest text: "If opponent just deployed" (Dark Jedi and Sith are Dark Side cards, so the same play); GEMP's wording was no version.
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(java.util.Map.of("c", "1261_106")), new java.util.HashMap<>(), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetLSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("May add Harc as a pilot. During battle, adds one destiny to total power. If opponent just deployed a Dark Jedi or Sith to a related site, may deploy a related location from Reserve Deck; reshuffle. Immune to attrition < 4 (< 6 while at Bespin).", text);
        org.junit.Assert.assertFalse(text.contains("was just deployed"));
        org.junit.Assert.assertNull(scn.GetLSCard("c").getBlueprint().getLegacyVersion());
    }
}
