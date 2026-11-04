package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.TestBase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#142, game option "Starting: Objective optional": a deck's Objectives are offered at game start in
 * the same box as its starting locations; choosing a location plays no Objective, and the Objective stays in the deck.
 */
public class ObjectiveOptionalTests {
    private static final String DS = TestBase.DS;
    private static final String LS = TestBase.LS;

    private VirtualTableScenario scenario(boolean optional) {
        HashMap<String, String> ds = new HashMap<>();
        ds.put("bhbm", "9_151");      // Bring Him Before Me (Objective)
        ds.put("throne", "9_147");    // Death Star II: Throne Room
        ds.put("site", "12_176");     // Tatooine: Marketplace (Dark Side site)
        VirtualTableScenario scn = new VirtualTableScenario(new HashMap<>(), ds, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DoNothingSetup,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                "anything_goes");
        scn.gameState().setObjectiveOptional(optional);
        if (scn.DSDecisionAvailable("Select OK to start game")) scn.DSDecided("0");
        if (scn.LSDecisionAvailable("Select OK to start game")) scn.LSDecided("0");
        return scn;
    }

    private static String pending(VirtualTableScenario scn) {
        StringBuilder sb = new StringBuilder();
        for (String p : new String[]{DS, LS}) {
            var d = scn.userFeedback().getAwaitingDecision(p);
            if (d != null) sb.append(p).append("=").append(d.getText()).append("; ");
        }
        return sb.toString();
    }

    @Test
    public void ObjectiveOptionalOffersTheObjectiveAndTheStartingLocationsTogether() {
        VirtualTableScenario scn = scenario(true);
        assertTrue("pending: " + pending(scn), scn.DSDecisionAvailable("Choose your Objective or starting location"));
        List<String> offered = Arrays.asList(scn.GetAwaitingDecisionParams(DS).get("blueprintId"));
        assertTrue(offered.toString(), offered.contains("9_151"));
        assertTrue("the Objective is shown front side up: " + offered, offered.contains("9_151"));
        assertTrue("a starting location is offered: " + offered, offered.contains("12_176"));
    }

    @Test
    public void ObjectiveOptionalChoosingALocationPlaysNoObjective() {
        VirtualTableScenario scn = scenario(true);
        PhysicalCardImpl site = scn.GetDSCard("site");
        scn.DSChooseCard(site);
        StartingSetup.DefaultLSGroundLocation.Setup(scn);
        assertFalse("no second location box for Dark Side: " + pending(scn), scn.DSDecisionAvailable("Choose starting location"));
        assertNull("no Objective is played", scn.gameState().getObjectivePlayed(DS));
        assertTrue("the chosen site is the starting location: zone=" + site.getZone() + " pending: " + pending(scn), site.getZone().isInPlay());
        assertFalse("the Objective stays in the deck", scn.GetDSCard("bhbm").getZone().isInPlay());
    }

    @Test
    public void ObjectiveOptionalChoosingTheObjectivePlaysIt() {
        VirtualTableScenario scn = scenario(true);
        PhysicalCardImpl bhbm = scn.GetDSCard("bhbm");
        scn.DSChooseCard(bhbm);
        StartingSetup.DefaultLSGroundLocation.Setup(scn);
        assertEquals("pending: " + pending(scn), bhbm, scn.gameState().getObjectivePlayed(DS));
    }

    @Test
    public void StartingCardsTwoPlaysTheObjectiveAndALocation() {
        VirtualTableScenario scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>() {{
                    put("bhbm", "9_151");     // Bring Him Before Me
                    put("throne", "9_147");   // Death Star II: Throne Room
                    put("site", "12_176");    // Tatooine: Marketplace
                }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DoNothingSetup,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                "anything_goes");
        scn.gameState().setObjectiveOptional(true);
        scn.gameState().setStartingCardsCount(2);
        if (scn.DSDecisionAvailable("Select OK to start game")) scn.DSDecided("0");
        if (scn.LSDecisionAvailable("Select OK to start game")) scn.LSDecided("0");
        assertTrue("pending: " + pending(scn), scn.DSDecisionAvailable("Choose up to 2 Objectives and starting locations"));
        PhysicalCardImpl bhbm = scn.GetDSCard("bhbm");
        PhysicalCardImpl site = scn.GetDSCard("site");
        scn.DSChooseCards(bhbm, site);
        StartingSetup.DefaultLSGroundLocation.Setup(scn);
        // Bring Him Before Me's own setup may ask its questions; answer them with the first choice
        for (int i = 0; i < 10 && !(bhbm.getZone().isInPlay() && site.getZone().isInPlay()); i++) {
            if (scn.DSAnyDecisionsAvailable()) scn.PlayerDecided(DS, firstAnswer(scn, DS));
            else if (scn.LSAnyDecisionsAvailable()) scn.PlayerDecided(LS, firstAnswer(scn, LS));
            else break;
        }
        assertTrue("the location is the starting location: " + site.getZone() + " pending: " + pending(scn), site.getZone().isInPlay());
        assertTrue("the Objective is played: " + bhbm.getZone() + " pending: " + pending(scn), bhbm.getZone().isInPlay());
    }

    @Test
    public void StartingCardsTwoWithoutAnObjectiveDeploysTwoLocations() {
        VirtualTableScenario scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>() {{
                    put("site", "12_176");    // Tatooine: Marketplace
                    put("throne", "9_147");   // Death Star II: Throne Room
                }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DoNothingSetup,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                "anything_goes");
        scn.gameState().setObjectiveOptional(true);
        scn.gameState().setStartingCardsCount(2);
        if (scn.DSDecisionAvailable("Select OK to start game")) scn.DSDecided("0");
        if (scn.LSDecisionAvailable("Select OK to start game")) scn.LSDecided("0");
        assertTrue("pending: " + pending(scn), scn.DSDecisionAvailable("Choose up to 2 starting locations"));
        PhysicalCardImpl site = scn.GetDSCard("site");
        PhysicalCardImpl throne = scn.GetDSCard("throne");
        scn.DSChooseCards(site, throne);
        StartingSetup.DefaultLSGroundLocation.Setup(scn);
        assertTrue("pending: " + pending(scn), site.getZone().isInPlay());
        assertTrue("pending: " + pending(scn), throne.getZone().isInPlay());
    }

    private static String firstAnswer(VirtualTableScenario scn, String player) {
        var params = scn.userFeedback().getAwaitingDecision(player).getDecisionParameters();
        if (params.containsKey("cardId")) return params.get("cardId")[0];
        if (params.containsKey("results")) return "0";
        if (params.containsKey("actionId")) return params.get("actionId").length > 0 ? params.get("actionId")[0] : "";
        return "";
    }

    @Test
    public void ObjectiveRequiredByDefaultPlaysTheOnlyObjectiveWithoutAsking() {
        VirtualTableScenario scn = scenario(false);
        assertFalse(scn.DSDecisionAvailable("Choose your Objective"));
        assertFalse(scn.DSDecisionAvailable("Choose your Objective or starting location"));
    }
}
