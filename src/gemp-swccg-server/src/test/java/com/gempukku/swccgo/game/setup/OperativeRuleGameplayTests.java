package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OperativeRuleGameplayTests {
    @Parameterized.Parameters(name = "operativeRule={0}")
    public static List<Object[]> values() {
        return List.of(new Object[]{true}, new Object[]{false});
    }

    private final boolean enabled;

    public OperativeRuleGameplayTests(boolean enabled) {
        this.enabled = enabled;
    }

    private VirtualTableScenario scenario(boolean matching) {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "operative", "7_26", "second", "7_26", "han", "1_11",
                "forest", "7_121", "city", "7_125", "system", matching ? "6_83" : "7_110")),
                new HashMap<>(Map.of("trooper", "1_167", "operative", "7_185", "second", "7_185")));
        scn.gameState().setOperativeRuleEnabled(enabled);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("system"));
        String system = matching ? Title.Kiffex : Title.Bothawui;
        for (String name : List.of("forest", "city")) {
            var card = scn.GetLSCard(name);
            var placements = scn.gameState().getLocationPlacement(scn.game(), card, system, null);
            assertFalse(placements.isEmpty());
            scn.MoveLocationToTable(card, placements.getFirst());
        }
        return scn;
    }

    @Test
    public void OperativeRuleControlsMatchingOperativeForceDrainEligibility() {
        //test1: alone on the matching planet, only the special rule prevents control/draining.
        var scn = scenario(true);
        var forest = scn.GetLSCard("forest");
        scn.MoveCardsToLocation(forest, scn.GetLSCard("operative"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(!enabled, scn.LSForceDrainAvailable(forest)); //test1
        if (!enabled) {
            int lost = scn.GetDSLostPileCount();
            scn.LSForceDrainAt(forest);
            scn.PassForceDrainStartResponses();
            int loss = scn.gameState().getForceDrainState().getForceTotal();
            for (int i = 0; i < loss; i++) {
                scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
                scn.PassForceDrainPendingResponses();
            }
            scn.PassForceDrainEndResponses();
            assertEquals(lost + loss, scn.GetDSLostPileCount()); //test1
        }
    }

    @Test
    public void OperativeRuleDoesNotRestrictOperativesAwayFromTheirMatchingPlanet() {
        //test1: printed matching-planet restrictions do not follow an operative elsewhere.
        var scn = scenario(false);
        var forest = scn.GetLSCard("forest");
        scn.MoveCardsToLocation(forest, scn.GetLSCard("operative"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(forest)); //test1
    }

    @Test
    public void OperativeRuleStillAllowsControlWithAnotherCapableCharacter() {
        //test1: a non-operative character supports control even with the rule on.
        var scn = scenario(true);
        var forest = scn.GetLSCard("forest");
        scn.MoveCardsToLocation(forest, scn.GetLSCard("operative"), scn.GetLSCard("han"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(forest)); //test1
    }

    @Test
    public void OperativeRuleControlsSecondMatchingDeploymentDestination() {
        //test1: a legal alternate site keeps deployment offered; only the occupied matching site is excluded.
        var scn = scenario(true);
        var city = scn.GetLSCard("city");
        var forest = scn.GetLSCard("forest");
        var second = scn.GetLSCard("second");
        scn.MoveCardsToLocation(city, scn.GetLSCard("operative"));
        scn.MoveCardsToLSHand(second);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertTrue(scn.LSDeployAvailable(second));
        scn.LSDeployCard(second);
        assertTrue(scn.LSHasCardChoiceAvailable(forest)); //test1
        assertEquals(!enabled, scn.LSHasCardChoiceAvailable(city)); //test1
        scn.LSChooseCard(enabled ? forest : city);
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, second.getZone()); //test1
        assertEquals((enabled ? forest : city).getCardId(), second.getAtLocation().getCardId()); //test1
    }

    @Test
    public void OperativeRuleControlsSecondMatchingMovement() {
        //test1: normal movement between adjacent matching sites is offered only with the restriction off.
        var scn = scenario(true);
        var city = scn.GetLSCard("city");
        var forest = scn.GetLSCard("forest");
        var second = scn.GetLSCard("second");
        scn.MoveCardsToLocation(city, scn.GetLSCard("operative"));
        scn.MoveCardsToLocation(forest, second);
        scn.SkipToLSTurn(Phase.MOVE);
        scn.EnsureLSForcePile(5);
        assertEquals(!enabled, scn.LSMoveAvailable(second)); //test1
        if (!enabled) {
            scn.LSMoveCard(second, city);
            scn.PassAllResponses();
            assertEquals(city.getCardId(), second.getAtLocation().getCardId()); //test1
            assertEquals(Zone.AT_LOCATION, scn.GetLSCard("operative").getZone()); //test1
        }
    }

    @Test
    public void OperativeRuleControlsAbilityAtTheBattleDestinyThreshold() {
        //test1: Han's 3 plus an operative's 1 reaches four only when that ability is allowed to count.
        var scn = scenario(true);
        var forest = scn.GetLSCard("forest");
        scn.MoveCardsToLocation(forest, scn.GetLSCard("operative"), scn.GetLSCard("han"), scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(forest);
        assertEquals(enabled ? 0 : 1, scn.GetLSBattleDestinyCount()); //test1
        scn.PrepareLSDestiny(3);
        scn.SkipToPowerSegment();
        assertEquals(!enabled, scn.LSDecisionAvailable("battle destiny?")); //test1
        if (!enabled) {
            scn.LSChooseYes();
            scn.PassDestinyDrawResponses();
        }
        assertEquals(enabled ? 0 : 3, scn.GetLSTotalDestiny()); //test1
    }
}
