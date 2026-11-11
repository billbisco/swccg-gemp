package com.gempukku.swccgo.cards.set2281.light;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Imperial Navigation Charts: original version of 1281_32 (first Virtual Block 8.1 printing, 2012-05-11: optional steal). Anything Goes only. */
public class Card_2281_032_Tests {
    @Test
    public void ImperialNavigationChartsVersionIdentityAndText() {
        assertVersion("2281_32", "1281_32", "Imperial Navigation Charts",
                "Deploy on a Nar Shaddaa battleground site. If you control three Nar Shaddaa locations or just won a battle (or initiated a Force drain) here, may 'steal' (relocate to your side of table). Place in Used Pile if opponent controls three Nar Shaddaa locations. (Immune to Alter).",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 13));
        assertSameCard("2281_32", "1281_32");
    }

    /** Current card (PC-E1, 2012-07-13): the steal is not optional; it happens as soon as you control three Nar Shaddaa locations. */
    @Test
    public void ImperialNavigationChartsCurrentStealIsNotOptional() {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<>() {{
                    put("charts", "1281_32");
                    put("rest", "1281_39");
                    put("street", "1281_40");
                    put("undercity", "1281_41");
                    put("narshaddaa", "200_58");
                    put("lando", "601_50");
                    put("leia", "1_17");
                    put("han", "1_11");
                }},
                new java.util.HashMap<>(),
                20, 20,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("narshaddaa"));
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(scn.GetLSCard("street"));
        scn.MoveLocationToTable(scn.GetLSCard("undercity"));
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, scn.GetLSCard("lando"));
        scn.MoveCardsToLocation(scn.GetLSCard("street"), scn.GetLSCard("leia"));
        scn.MoveCardsToLocation(scn.GetLSCard("undercity"), scn.GetLSCard("han"));
        scn.SkipToLSTurn(com.gempukku.swccgo.common.Phase.CONTROL);
        scn.PassAllResponses();
        org.junit.Assert.assertEquals(com.gempukku.swccgo.common.Zone.SIDE_OF_TABLE, charts.getZone());
    }
}
