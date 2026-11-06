package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class Card_1240_112_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("infinity", "1240_112");
            put("boshek", "1_4");
            put("pilot", "2_23");
            put("corvette", "1_140");
            put("kessel", "1_126");
            put("site", "1_133");
        }}, new HashMap<>() {{
            put("deathStar", "2_143");
            put("devastator", "1_301");
            put("pilot", "1_164");
        }}, 40, 40, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void InfinityPrintedStatsIconsKeywordsAndBlankLoreAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetLSCard("infinity").getBlueprint();
        assertEquals("Infinity", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals("", bp.getLore());
        assertEquals(Side.LIGHT, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(3, bp.getPower(), scn.epsilon);
        assertEquals(3, bp.getManeuver(), scn.epsilon);
        assertNull(bp.getArmor());
        assertEquals(4, bp.getHyperspeed(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(2, bp.getPilotCapacity());
        assertEquals(0, bp.getPassengerCapacity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.STARSHIP)));
        scn.BlueprintModelTypeCheck(bp, new ArrayList<>(List.of(ModelType.MODIFIED_LIGHT_FREIGHTER)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.STARSHIP, Icon.INDEPENDENT,
                Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_4)));
    }

    @Test
    public void InfinityDeploysToSystemWithSimultaneousPilotForOrdinaryCombinedCost() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSHand(scn.GetLSCard("infinity"), scn.GetLSCard("boshek"));
        scn.EnsureLSForcePile(7);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(scn.GetLSCard("infinity"));
        assertTrue(scn.LSDecisionAvailable("simultaneously deploy aboard"));
        scn.LSChooseCard(scn.GetLSCard("boshek"));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("infinity"));
        assertTrue(scn.IsAboardAsPilot(scn.GetLSCard("infinity"), scn.GetLSCard("boshek")));
        assertEquals(0, scn.GetLSForcePileCount());
    }

    private VirtualTableScenario Ready(int boShekPlacement) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        var ship = scn.GetLSCard("infinity");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship);
        if (boShekPlacement == 1) scn.BoardAsPilot(ship, scn.GetLSCard("boshek"));
        else {
            scn.BoardAsPilot(ship, scn.GetLSCard("pilot"));
            if (boShekPlacement == 2) {
                scn.MoveLocationToTable(scn.GetLSCard("site"));
                scn.MoveCardsToLocation(scn.GetLSCard("site"), scn.GetLSCard("boshek"));
            }
            if (boShekPlacement == 3) {
                scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("corvette"));
                scn.BoardAsPilot(scn.GetLSCard("corvette"), scn.GetLSCard("boshek"));
            }
        }
        return scn;
    }

    private void CheckBonuses(VirtualTableScenario scn, boolean active) {
        var ship = scn.GetLSCard("infinity");
        assertEquals(active ? 5 : 3, scn.GetManeuver(ship));
        assertEquals(active ? 6 : 4, scn.GetHyperspeed(ship));
        assertEquals(active ? 4 : 0,
                scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }

    @Test
    public void InfinityGainsManeuverHyperspeedAndImmunityWhileBoShekPilots() {
        CheckBonuses(Ready(1), true);
    }

    @Test
    public void InfinityDoesNotGainBonusesFromAnotherPilot() {
        CheckBonuses(Ready(0), false);
    }

    @Test
    public void InfinityDoesNotGainBonusesFromBoShekAtAnotherLocation() {
        CheckBonuses(Ready(2), false);
    }

    @Test
    public void InfinityDoesNotGainBonusesFromBoShekPilotingAnotherShipHere() {
        CheckBonuses(Ready(3), false);
    }

    @Test
    public void InfinityLosesAndRegainsBonusesWhenBoShekLeavesAndReturns() {
        var scn = Ready(1);
        scn.BoardAsPilot(scn.GetLSCard("infinity"), scn.GetLSCard("pilot"));
        scn.MoveCardsToLSHand(scn.GetLSCard("boshek"));
        CheckBonuses(scn, false);
        scn.BoardAsPilot(scn.GetLSCard("infinity"), scn.GetLSCard("boshek"));
        CheckBonuses(scn, true);
    }

    @Test
    public void InfinityCanMoveSixParsecsWithBoShekForNormalMovementCost() {
        var scn = Ready(1);
        var destination = scn.GetLSCard("kessel");
        scn.MoveLocationToTable(destination);
        scn.EnsureLSForcePile(10);
        scn.SkipToPhase(Phase.MOVE);
        int force = scn.GetLSForcePileCount();
        assertEquals(6, scn.GetHyperspeed(scn.GetLSCard("infinity")));
        scn.LSMoveCard(scn.GetLSCard("infinity"), destination);
        scn.PassAllResponses();
        assertAtLocation(destination, scn.GetLSCard("infinity"));
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void InfinityCannotMoveSixParsecsWithoutBoShek() {
        var scn = Ready(0);
        scn.MoveLocationToTable(scn.GetLSCard("kessel"));
        scn.EnsureLSForcePile(10);
        scn.SkipToPhase(Phase.MOVE);
        scn.LSUseCardAction(scn.GetLSCard("infinity"), "Move using hyperspeed");
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetLSCard("kessel")));
    }

    @Test
    public void InfinityMayAddOneToActualBattlegroundSystemForceDrain() {
        var scn = DrainReady(1, false);
        int lost = scn.GetDSLostPileCount();
        scn.LSUseCardAction(scn.GetLSCard("infinity"), "Add 1 to Force drain");
        scn.PassAllResponses();
        assertEquals(2, scn.GetForceDrainTotal());
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(lost + 2, scn.GetDSLostPileCount());
    }

    @Test
    public void InfinityMayDeclineItsBattlegroundSystemDrainBonus() {
        var scn = DrainReady(1, false);
        int lost = scn.GetDSLostPileCount();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("infinity"), "Add 1 to Force drain"));
        scn.PassAllResponses();
        assertEquals(1, scn.GetForceDrainTotal());
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(lost + 1, scn.GetDSLostPileCount());
    }

    @Test
    public void InfinityDoesNotOfferDrainBonusWithBoShekAtAnotherLocation() {
        var scn = DrainReady(2, false);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("infinity"), "Add 1 to Force drain"));
        scn.PassAllResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void InfinityDoesNotOfferDrainBonusAtNonBattlegroundSystem() {
        var scn = DrainReady(1, true);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("infinity"), "Add 1 to Force drain"));
        scn.PassAllResponses();
        assertEquals(3, scn.GetForceDrainTotal());
    }

    @Test
    public void InfinityDoesNotModifyAForceDrainAtAnotherBattlegroundSystem() {
        var scn = Ready(1);
        var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, scn.GetLSCard("corvette"));
        scn.SkipToPhase(Phase.CONTROL);
        scn.LSForceDrainAt(system);
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("infinity"), "Add 1 to Force drain"));
        scn.PassAllResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void InfinitySurvivesThreeAttritionWithBoShekPiloting() {
        var scn = BattleReady(3);
        assertEquals(3, scn.GetUnpaidLSAttrition());
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.SkipToPhase(Phase.MOVE);
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("infinity"));
        assertTrue(scn.IsAboardAsPilot(scn.GetLSCard("infinity"), scn.GetLSCard("boshek")));
    }

    @Test
    public void InfinityMustStillSatisfyExactlyFourAttritionWithBoShekPiloting() {
        var scn = BattleReady(4);
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        assertEquals(4, scn.GetUnpaidLSAttrition());
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("infinity")));
    }

    private VirtualTableScenario BattleReady(int destiny) {
        var scn = Ready(1);
        var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, scn.GetDSCard("devastator"));
        scn.BoardAsPilot(scn.GetDSCard("devastator"), scn.GetDSCard("pilot"));
        scn.EnsureLSForcePile(5);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(destiny);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(destiny, scn.GetDSTotalDestiny());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        return scn;
    }

    private VirtualTableScenario DrainReady(int placement, boolean nonBattleground) {
        var scn = Ready(placement);
        var system = scn.GetLSStartingLocation();
        if (nonBattleground) {
            system = scn.GetDSCard("deathStar");
            scn.MoveLocationToTable(system);
            scn.MoveCardsToLocation(system, scn.GetLSCard("infinity"));
        }
        scn.SkipToPhase(Phase.CONTROL);
        scn.LSForceDrainAt(system);
        scn.DSPass();
        return scn;
    }
}
