package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_211_016_Follow_Tests {
    private record FollowScenario(VirtualTableScenario table, PhysicalCardImpl origin,
                                  PhysicalCardImpl destination, PhysicalCardImpl otherSite) {
    }

    private boolean canFollow(VirtualTableScenario scn, String text) {
        return scn.DSGetDecision() != null && scn.DSGetDecision().getDecisionParameters().containsKey("actionText")
                && scn.DSCardPlayAvailable(scn.GetDSCard("anger"), text);
    }

    private FollowScenario moveLuke(boolean trained, boolean maraStartsHere, boolean vaderStartsHere) {
        var scn = new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19"); put("mosEisley", "1_133"); put("farm", "1_132");
        }}, new HashMap<>() {{
            put("anger", "211_16"); put("trained", "1115_67");
            put("maul", "11_54"); put("mara", "110_10"); put("vader", "1_168");
        }}, 20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        var mosEisley = scn.GetLSCard("mosEisley");
        var farm = scn.GetLSCard("farm");
        scn.MoveLocationToTable(mosEisley);
        scn.MoveLocationToTable(farm);
        var sites = List.of(mosEisley, farm, scn.GetDSStartingLocation());
        var destination = sites.stream().filter(site -> sites.stream()
                .filter(other -> other != site && scn.IsAdjacentTo(site, other)).count() == 2).findFirst().orElseThrow();
        var edges = sites.stream().filter(site -> site != destination).toList();
        var origin = edges.get(0);
        var otherSite = edges.get(1);
        assertTrue(scn.IsAdjacentTo(origin, destination));
        assertTrue(scn.IsAdjacentTo(otherSite, destination));

        scn.MoveCardsToDSHand(scn.GetDSCard("anger"));
        if (trained) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("trained"));
        scn.MoveCardsToLocation(origin, scn.GetLSCard("luke"), scn.GetDSCard("maul"));
        scn.MoveCardsToLocation(maraStartsHere ? origin : otherSite, scn.GetDSCard("mara"));
        if (vaderStartsHere) scn.MoveCardsToLocation(origin, scn.GetDSCard("vader"));

        scn.SkipToLSTurn(Phase.MOVE);
        scn.EnsureLSForcePile(2);
        scn.LSMoveCard(scn.GetLSCard("luke"), destination);
        scn.PassForceUseResponses();
        for (int i = 0; i < 12 && !canFollow(scn, "Follow")
                && !scn.AwaitingLSMovePhaseActions(); i++) {
            if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("optional")) scn.DSPass();
            else if (scn.LSGetDecision() != null && scn.LSDecisionAvailable("optional")) scn.LSPass();
            else fail("Unexpected movement response: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.CardsAtLocation(destination, scn.GetLSCard("luke")));
        return new FollowScenario(scn, origin, destination, otherSite);
    }

    @Test
    public void TrainedLetsYouChooseWhichEligibleCharacterFollowsForFree() {
        var setup = moveLuke(true, true, false);
        var scn = setup.table();
        var anger = scn.GetDSCard("anger");
        assertTrue(canFollow(scn, "with Darth Maul"));
        assertTrue(canFollow(scn, "with Mara Jade"));
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(anger, "with Mara Jade");
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(setup.destination(), scn.GetDSCard("mara")));
        assertTrue(scn.CardsAtLocation(setup.origin(), scn.GetDSCard("maul")));
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void ACharacterAtAnotherSiteCannotFollowEvenIfItCanReachTheDestination() {
        var setup = moveLuke(true, false, false);
        var scn = setup.table();
        var anger = scn.GetDSCard("anger");
        assertTrue(canFollow(scn, "with Darth Maul"));
        assertFalse(canFollow(scn, "with Mara Jade"));
        scn.DSPlayCard(anger, "with Darth Maul");
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(setup.destination(), scn.GetDSCard("maul")));
        assertTrue(scn.CardsAtLocation(setup.otherSite(), scn.GetDSCard("mara")));
    }

    @Test
    public void UnmodifiedInterruptLetsOnlyVaderFollow() {
        var setup = moveLuke(false, true, true);
        var scn = setup.table();
        var anger = scn.GetDSCard("anger");
        assertTrue(canFollow(scn, "with Darth Vader"));
        assertFalse(canFollow(scn, "with Darth Maul"));
        assertFalse(canFollow(scn, "with Mara Jade"));
        scn.DSPlayCard(anger, "with Darth Vader");
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(setup.destination(), scn.GetDSCard("vader")));
    }
}
