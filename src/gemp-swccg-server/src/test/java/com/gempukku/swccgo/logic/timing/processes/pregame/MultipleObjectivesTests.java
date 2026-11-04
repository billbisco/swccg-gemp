package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.TestBase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#141: a deck with several Objectives (Anything Goes allows it) lets its player choose which one to
 * play, like a starting location; the others stay in the deck.
 */
public class MultipleObjectivesTests {
    private static final String DS = TestBase.DS;

    private VirtualTableScenario scenario(String... dsObjectives) {
        HashMap<String, String> ds = new HashMap<>();
        for (int i = 0; i < dsObjectives.length; i++)
            ds.put("objective-" + i, dsObjectives[i]);
        return new VirtualTableScenario(new HashMap<>(), ds, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DoNothingSetup,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                "anything_goes");
    }

    private static String pending(VirtualTableScenario scn) {
        StringBuilder sb = new StringBuilder();
        for (String p : new String[]{TestBase.DS, TestBase.LS}) {
            var d = scn.userFeedback().getAwaitingDecision(p);
            if (d != null) sb.append(p).append("=").append(d.getText()).append(" ").append(d.getDecisionParameters().keySet()).append("; ");
        }
        return sb.toString();
    }

    @Test
    public void MultipleObjectivesThePlayerChoosesOne() {
        // Bring Him Before Me and Carbon Chamber Testing
        VirtualTableScenario scn = scenario("9_151", "7_296");
        if (scn.DSDecisionAvailable("Select OK to start game")) scn.DSDecided("0");
        if (scn.LSDecisionAvailable("Select OK to start game")) scn.LSDecided("0");
        assertTrue("the Dark Side player is asked which Objective to play; pending: " + pending(scn), scn.DSDecisionAvailable("Choose your Objective"));
        String[] choices = scn.GetAwaitingDecisionParams(DS).get("cardId");
        assertEquals(2, choices.length);
        var carbonChamber = scn.GetDSCard("objective-1");
        scn.DSChooseCard(carbonChamber);
        StartingSetup.DefaultLSGroundLocation.Setup(scn);
        assertEquals("the chosen Objective is the one played; zone=" + carbonChamber.getZone() + " pending: " + pending(scn), carbonChamber,
                scn.gameState().getObjectivePlayed(DS));
        assertFalse("the other one is not played", scn.GetDSCard("objective-0").getZone().isInPlay());
    }

    @Test
    public void OneObjectiveIsPlayedWithoutAsking() {
        VirtualTableScenario scn = scenario("9_151");
        if (scn.DSDecisionAvailable("Select OK to start game")) scn.DSDecided("0");
        if (scn.LSDecisionAvailable("Select OK to start game")) scn.LSDecided("0");
        assertFalse(scn.DSDecisionAvailable("Choose your Objective"));
    }
}
