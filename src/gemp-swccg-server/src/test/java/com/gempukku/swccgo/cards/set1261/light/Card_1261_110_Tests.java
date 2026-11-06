package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1261_110_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("garrison", "1261_110"); put("hoth", "3_62"); put("perimeter", "3_56");
            put("luke", "1_19"); put("rebel", "1_28");
        }}, new HashMap<>() {{ put("vader", "1_168"); }});
    }

    @Test
    public void SnowspeederGarrisonStatsMatchFinalMaster() {
        var scn = scenario(); var b = scn.GetLSCard("garrison").getBlueprint();
        assertEquals("Snowspeeder Garrison", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(2, b.getDestiny(), scn.epsilon); assertEquals(9, b.getDeployCost(), scn.epsilon);
        assertEquals(9, b.getPower(), scn.epsilon); assertEquals(4, b.getManeuver(), scn.epsilon);
        assertEquals(5, b.getLandspeed(), scn.epsilon); assertEquals(12, b.getForfeit(), scn.epsilon);
        assertEquals(0, b.getPilotCapacity()); assertEquals(0, b.getPassengerCapacity());
        assertEquals("Enclosed.", b.getLore()); assertEquals(CardSubtype.COMBAT, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.VEHICLE)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.VEHICLE, Icon.PILOT, Icon.PILOT, Icon.PILOT, Icon.HOTH, Icon.LEGACY_BLOCK_6)));
        assertEquals(3, b.getIconCount(Icon.PILOT));
        scn.BlueprintModelTypeCheck(b, new ArrayList<>(Arrays.asList(ModelType.T_47)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.ENCLOSED, Keyword.SNOWSPEEDER)));
    }

    private void start(VirtualTableScenario scn) {
        scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("hoth")); scn.MoveLocationToTable(scn.GetLSCard("perimeter"));
    }

    @Test
    public void SnowspeederGarrisonPermanentPilotsProvideSixAbility() {
        var scn = scenario(); start(scn);
        scn.MoveCardsToLocation(scn.GetLSCard("hoth"), scn.GetLSCard("garrison"));
        assertEquals(6, scn.GetBattleDestinyAbility(scn.GetLSCard("garrison")));
    }

    @Test
    public void SnowspeederGarrisonDefenseAndImmunityFollowCurrentPlanet() {
        var scn = scenario(); start(scn); var g = scn.GetLSCard("garrison");
        scn.MoveCardsToLocation(scn.GetLSCard("hoth"), g);
        assertEquals(6, scn.GetDefense(g));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), g), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), g);
        assertEquals(4, scn.GetDefense(g));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), g), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetLSCard("hoth"), g); assertEquals(6, scn.GetDefense(g));
    }

    private void deploy(boolean hoth, int expectedCost) {
        var scn = scenario(); start(scn); var g = scn.GetLSCard("garrison");
        var site = hoth ? scn.GetLSCard("hoth") : scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke")); scn.MoveCardsToLSHand(g);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(10);
        int before = scn.GetLSForcePile().size();
        scn.LSDeployCard(g); scn.LSChooseCard(site); scn.PassAllResponses();
        assertEquals(site, g.getAtLocation()); assertEquals(before - expectedCost, scn.GetLSForcePile().size());
    }

    @Test public void SnowspeederGarrisonDeploysForSixToHoth() { deploy(true, 6); }
    @Test public void SnowspeederGarrisonCostsNineAwayFromHoth() { deploy(false, 9); }

    private VirtualTableScenario react(boolean hoth, int force) {
        var scn = scenario(); start(scn); var from = hoth ? scn.GetLSCard("perimeter") : scn.GetLSStartingLocation();
        var to = hoth ? scn.GetLSCard("hoth") : scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(from, scn.GetLSCard("garrison"));
        scn.MoveCardsToLocation(to, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSForcePile().toArray(com.gempukku.swccgo.game.PhysicalCardImpl[]::new));
        scn.LSActivateForceCheat(force); scn.DSInitiateBattle(to);
        return scn;
    }

    @Test
    public void SnowspeederGarrisonReactsToBattleForTwoForceOnHoth() {
        var scn = react(true, 2); var g = scn.GetLSCard("garrison"); int before = scn.GetLSForcePile().size();
        assertTrue(scn.LSCardActionAvailable(g, "react")); scn.LSUseCardAction(g, "react");
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("hoth"))) scn.LSChooseCard(scn.GetLSCard("hoth"));
        scn.PassAllResponses(); assertEquals(scn.GetLSCard("hoth"), g.getAtLocation());
        assertEquals(before - 2, scn.GetLSForcePile().size());
    }

    @Test
    public void SnowspeederGarrisonCannotReactWithoutExtraForce() {
        var scn = react(true, 1); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("garrison"), "react"));
    }

    @Test
    public void SnowspeederGarrisonCannotReactFromAnotherPlanet() {
        var scn = react(false, 4); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("garrison"), "react"));
    }

    @Test
    public void SnowspeederGarrisonReactsToForceDrainForExtraForce() {
        var scn = scenario(); start(scn); var g = scn.GetLSCard("garrison"); var target = scn.GetLSCard("hoth");
        scn.MoveCardsToLocation(scn.GetLSCard("perimeter"), g);
        scn.MoveCardsToLocation(target, scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.LSActivateForceCheat(3);
        int before = scn.GetLSForcePile().size();
        assertTrue(scn.DSForceDrainAvailable(target)); scn.DSForceDrainAt(target);
        assertTrue(scn.LSCardActionAvailable(g, "react")); scn.LSUseCardAction(g, "react");
        if (scn.LSHasCardChoiceAvailable(target)) scn.LSChooseCard(target);
        scn.PassAllResponses(); assertEquals(target, g.getAtLocation());
        assertEquals(before - 2, scn.GetLSForcePile().size());
    }

    private void attrition(boolean hoth, int destiny, boolean immune) {
        var scn = scenario(); start(scn); var g = scn.GetLSCard("garrison");
        var site = hoth ? scn.GetLSCard("hoth") : scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, g, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(destiny - 1);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses(); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertEquals(!immune, scn.LSHasCardChoiceAvailable(g));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke")));
    }

    @Test public void SnowspeederGarrisonDoesNotPayAttritionThreeOnHoth() { attrition(true, 3, true); }
    @Test public void SnowspeederGarrisonMayBeForfeitedAtAttritionFourOnHoth() { attrition(true, 4, false); }
    @Test public void SnowspeederGarrisonPaysAttritionThreeAwayFromHoth() { attrition(false, 3, false); }
}
