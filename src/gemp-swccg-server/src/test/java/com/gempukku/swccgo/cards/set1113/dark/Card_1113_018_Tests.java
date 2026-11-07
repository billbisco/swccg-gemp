package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Card_1113_018_Tests {
    private static final String RETRIEVE = "Retrieve 2 Force";

    private VirtualTableScenario Started() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("r2", "2_14", "luke", "1_19")),
                new HashMap<>(Map.of("prison", "1113_18", "trooper", "1_194")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("prison"));
        return scn;
    }

    private VirtualTableScenario EscortToPrison(String captiveName) {
        var scn = Started();
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(scn.GetDSCard("prison"), trooper);
        scn.CaptureCardWith(trooper, scn.GetLSCard(captiveName));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSFiller(1), scn.GetDSFiller(2), scn.GetDSFiller(3), scn.GetDSFiller(4));
        scn.SkipToDSTurn(Phase.MOVE);
        assertTrue(scn.DSActionAvailable("Deliver captive to prison"));
        scn.DSChooseAction("Deliver captive to prison");
        return scn;
    }

    /** Passes responses until the Dungeon retrieve is offered (true) or the delivery has resolved (false). */
    private boolean ReachReward(VirtualTableScenario scn) {
        var prison = scn.GetDSCard("prison");
        for (int step = 0; step < 14; step++) {
            if (scn.DSDecisionAvailable("optional response") && scn.DSCardActionAvailable(prison, RETRIEVE)) return true;
            if (scn.LSDecisionAvailable("optional response")) scn.LSPass();
            else if (scn.DSDecisionAvailable("optional response")) scn.DSPass();
            else if (scn.AwaitingDSMovePhaseActions() || scn.AwaitingLSMovePhaseActions()) return false;
            else fail("Unexpected delivery decision: " + scn.GetCurrentDecision().getText());
        }
        fail("Delivery never resolved");
        return false;
    }

    @Test
    public void ImprisoningACaptiveWithoutABountyHereMayRetrieveTwoForce() {
        var scn = EscortToPrison("luke");
        assertTrue(ReachReward(scn));
        int lost = scn.GetDSLostPileCount();
        scn.DSUseCardAction(scn.GetDSCard("prison"), RETRIEVE);
        scn.PassAllResponses();
        assertEquals(lost - 2, scn.GetDSLostPileCount());
        assertTrue(scn.GetLSCard("luke").isImprisoned());
        assertEquals(scn.GetDSCard("prison"), scn.GetLSCard("luke").getAttachedTo());
    }
}
