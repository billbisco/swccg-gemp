package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Leia (V) (Virtual Block 1), Legacy batch B32. Current card 1210_103 (class 601_227) = PC card list text (PC-E1, 2012):
 * "Once per battle at her location, may cancel game text of opponent's leader of ability &lt; 5 present". The release-sheet
 * text (2210_103): "During battle, may cancel game text of a leader of ability &lt; 5 with Leia" (either side's leader).
 */
public class Card_1210_103_VersionsTests {
    private VirtualTableScenario scenario(String leia, boolean opposingLeader) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("leia", leia); put("dodonna", "1_10");
        }}, new HashMap<>() {{ put("vader", "1_168"); if (opposingLeader) put("motti", "1_164"); }}, 60, 60,
                StartingSetup.LSStartingLocation("1_136"), StartingSetup.DSStartingLocation("12_176"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario battle(String leia, boolean opposingLeader) {
        var scn = scenario(leia, opposingLeader); scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("leia"), scn.GetLSCard("dodonna"), scn.GetDSCard("vader"));
        if (opposingLeader) scn.MoveCardsToLocation(site, scn.GetDSCard("motti"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        return scn;
    }

    @Test
    public void CurrentLeiaMayCancelOpponentsLeaderPresent() {
        var scn = battle("1210_103", true);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Cancel a leader's game text"));
    }

    @Test
    public void CurrentLeiaMayNotTargetOwnLeader() {
        var scn = battle("1210_103", false);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Cancel a leader's game text"));
    }

    @Test
    public void OriginalLeiaMayCancelAnyLeaderWithHer() {
        var scn = battle("2210_103", false);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Cancel a leader's game text"));
    }
}
