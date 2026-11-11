package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Batch R8-P: the card-listversion 2210_1103 of 1210_103 keeps the behaviour the current card had before the Master List text (2013) became current; these are the former Card_1210_103_VersionsTests tests run on 2210_1103. */
public class Card_2210_1103_Tests {
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
        var scn = battle("2210_1103", true);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Cancel a leader's game text"));
    }

    @Test
    public void CurrentLeiaMayNotTargetOwnLeader() {
        var scn = battle("2210_1103", false);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Cancel a leader's game text"));
    }

    @Test
    public void OriginalLeiaMayCancelAnyLeaderWithHer() {
        var scn = battle("2210_103", false);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Cancel a leader's game text"));
    }
}
