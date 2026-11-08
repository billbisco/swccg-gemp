package com.gempukku.swccgo.cards.set601.dark;

/** 1261_40: current (Legacy) text. */
public class Card_601_028_Tests {
    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // PC card list (PC-E1) text, single-spaced (GEMP had double spaces).
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(), new java.util.HashMap<>(java.util.Map.of("c", "1261_40")), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetDSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("Deploy on table. Each Amidala stacked on a Political Effect is a senator. While you have < 13 cards in hand, opponent may not peek at or remove them (except with Grimtaash). Once per turn, may take one [Virtual] Astromech Shortage into hand from Reserve Deck; reshuffle, or lose 2 Force to cancel a Political Effect. (Immune to Alter.)", text);
        org.junit.Assert.assertFalse(text.contains(".  "));
        org.junit.Assert.assertNull(scn.GetDSCard("c").getBlueprint().getLegacyVersion());
    }
}
