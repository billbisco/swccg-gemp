package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.TestBase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.setup.GameOptions;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#142 R3, game option "Objectives: deploy from hand" (Bill 2026-11-05, design A1): an Objective in
 * hand may be deployed for free during its owner's deploy phase when the cards its setup deploys are in hand or already
 * on the table; the setup then takes them from hand instead of Reserve Deck.
 */
public class ObjectivesFromHandTests {
    private static final String DS = TestBase.DS;
    private static final String LS = TestBase.LS;

    private VirtualTableScenario scenario(boolean fromHand) {
        VirtualTableScenario scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>() {{
                    put("bhbm", "9_151");       // Bring Him Before Me (Objective)
                    put("throne", "9_147");     // Death Star II: Throne Room
                    put("rebellion", "9_127");  // Insignificant Rebellion
                    put("destiny", "9_134");    // Your Destiny
                }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                "anything_goes");
        scn.gameState().setObjectiveOptional(true);
        scn.gameState().setObjectivesFromHand(fromHand);
        scn.StartGame();
        assertFalse("the Objective is not played at game start", scn.GetDSCard("bhbm").getZone().isInPlay());
        return scn;
    }

    private static String firstAnswer(VirtualTableScenario scn, String player) {
        var params = scn.userFeedback().getAwaitingDecision(player).getDecisionParameters();
        if (params.containsKey("cardId")) return params.get("cardId")[0];
        if (params.containsKey("results")) return "0";
        return "";
    }

    /** Plays the Objective and answers its setup's questions (and passes responses) until the setup is done. */
    private static void deployObjective(VirtualTableScenario scn, PhysicalCardImpl objective) {
        scn.DSPlayCard(objective);
        for (int i = 0; i < 40 && !(objective.getZone().isInPlay() && objective.isObjectiveDeploymentComplete()); i++) {
            if (scn.DSAnyDecisionsAvailable()) {
                var d = scn.userFeedback().getAwaitingDecision(DS);
                if (d.getDecisionParameters().containsKey("actionId")) scn.DSPass();
                else scn.PlayerDecided(DS, firstAnswer(scn, DS));
            }
            else if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
            else break;
        }
    }

    @Test
    public void DeploysFromHandAndTakesItsCardsFromHand() {
        VirtualTableScenario scn = scenario(true);
        PhysicalCardImpl bhbm = scn.GetDSCard("bhbm");
        PhysicalCardImpl throne = scn.GetDSCard("throne");
        PhysicalCardImpl rebellion = scn.GetDSCard("rebellion");
        PhysicalCardImpl destiny = scn.GetDSCard("destiny");
        scn.MoveCardsToHand(bhbm, throne, rebellion, destiny);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int reserve = scn.gameState().getReserveDeck(DS).size();
        assertTrue(scn.DSCardPlayAvailable(bhbm));
        deployObjective(scn, bhbm);
        assertTrue(bhbm.getZone().isInPlay());
        assertTrue("setup complete", bhbm.isObjectiveDeploymentComplete());
        assertTrue(throne.getZone().isInPlay());
        assertTrue(rebellion.getZone().isInPlay());
        assertTrue(destiny.getZone().isInPlay());
        assertEquals("nothing taken from Reserve Deck", reserve, scn.gameState().getReserveDeck(DS).size());
    }

    @Test
    public void ARequiredCardAlreadyOnTheTableCounts() {
        VirtualTableScenario scn = scenario(true);
        PhysicalCardImpl bhbm = scn.GetDSCard("bhbm");
        PhysicalCardImpl throne = scn.GetDSCard("throne");
        PhysicalCardImpl rebellion = scn.GetDSCard("rebellion");
        PhysicalCardImpl destiny = scn.GetDSCard("destiny");
        scn.MoveLocationToTable(throne);
        scn.MoveCardsToHand(bhbm, rebellion, destiny);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(bhbm));
        deployObjective(scn, bhbm);
        assertTrue("setup complete", bhbm.isObjectiveDeploymentComplete());
        assertTrue(rebellion.getZone().isInPlay());
        assertTrue(destiny.getZone().isInPlay());
    }

    @Test
    public void ARequiredCardOnlyInReserveDeckBlocksIt() {
        VirtualTableScenario scn = scenario(true);
        PhysicalCardImpl bhbm = scn.GetDSCard("bhbm");
        scn.MoveCardsToHand(bhbm, scn.GetDSCard("throne"), scn.GetDSCard("rebellion"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse("Your Destiny is not in hand or on the table", scn.DSCardPlayAvailable(bhbm));
    }

    @Test
    public void OnlyDuringYourDeployPhase() {
        VirtualTableScenario scn = scenario(true);
        PhysicalCardImpl bhbm = scn.GetDSCard("bhbm");
        scn.MoveCardsToHand(bhbm, scn.GetDSCard("throne"), scn.GetDSCard("rebellion"), scn.GetDSCard("destiny"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(bhbm));
    }

    @Test
    public void WithoutTheOptionAnObjectiveStaysInHand() {
        VirtualTableScenario scn = scenario(false);
        PhysicalCardImpl bhbm = scn.GetDSCard("bhbm");
        scn.MoveCardsToHand(bhbm, scn.GetDSCard("throne"), scn.GetDSCard("rebellion"), scn.GetDSCard("destiny"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(bhbm));
    }

    @Test
    public void RalltiirOperationsDeploysItsSystemFromHand() {
        VirtualTableScenario scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>() {{
                    put("ops", "7_300");        // Ralltiir Operations
                    put("ralltiir", "2_148");   // Ralltiir (system)
                }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                "anything_goes");
        scn.gameState().setObjectiveOptional(true);
        scn.gameState().setObjectivesFromHand(true);
        scn.StartGame();
        PhysicalCardImpl ops = scn.GetDSCard("ops");
        PhysicalCardImpl ralltiir = scn.GetDSCard("ralltiir");
        scn.MoveCardsToHand(ops, ralltiir);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(ops));
        deployObjective(scn, ops);
        assertTrue("setup complete", ops.isObjectiveDeploymentComplete());
        assertTrue(ralltiir.getZone().isInPlay());
    }

    @Test
    public void OnByDefaultInAnythingGoesOnly() {
        var library = VirtualTableScenario._formatLibrary;
        var option = GameOptions.get(GameOptions.OBJECTIVES_FROM_HAND);
        assertEquals("true", option.getDefault(library.getFormat("anything_goes")));
        assertEquals("false", option.getDefault(library.getFormat("open")));
    }
}
