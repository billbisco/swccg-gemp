package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1250_035_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("pilot", "1250_35");
            put("ship", "1115_26");
            put("otherShip", "1_140");
            put("rebelPilot", "1_3");
            put("jedi", "1116_35");
            put("vehicle", "102_4");
            put("kessel", "1_126");
            put("tatooine", "1_127");
        }}, new HashMap<>() {{
            put("trooper", "1_194");
            put("hound", "7_301");
        }}, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ClonePilotImmunityBelowFiveAllowsIgnoringFourAttrition() {
        checkAttritionBoundary(false, 4, true);
    }

    @Test
    public void ClonePilotImmunityBelowFiveRequiresForfeitAtFiveAttrition() {
        checkAttritionBoundary(false, 5, false);
    }

    @Test
    public void ClonePilotImmunityBelowThreeAllowsIgnoringTwoAttrition() {
        checkAttritionBoundary(true, 2, true);
    }

    @Test
    public void ClonePilotImmunityBelowThreeRequiresForfeitAtThreeAttrition() {
        checkAttritionBoundary(true, 3, false);
    }

    private void checkAttritionBoundary(boolean otherPilotPresent, int attrition, boolean canIgnore) {
        var scn = scenario();
        var ship = scn.GetLSCard("ship");
        var system = scn.GetLSCard("tatooine");
        var pilot = scn.GetLSCard("pilot");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship, scn.GetDSCard("hound"));
        scn.BoardAsPilot(ship, pilot);
        if (otherPilotPresent) {
            scn.BoardAsPassenger(ship, scn.GetLSCard("rebelPilot"));
        }
        scn.LSStartBattleAndSkipToWeaponsSegment(system);
        scn.PrepareLSDestiny(7);
        scn.PrepareDSDestiny(attrition);
        scn.SkipToDamageSegment(true);
        assertEquals(0, scn.gameState().getBattleState().getBattleDamageRemaining(scn.game(), scn.LS), scn.epsilon);
        assertEquals(attrition, scn.gameState().getBattleState().getAttritionRemaining(scn.game(), scn.LS), scn.epsilon);
        assertTrue(scn.LSDecisionAvailable("Choose a card from battle to forfeit"));
        assertEquals(canIgnore ? 0 : 1, scn.LSGetChoiceMin());
        if (canIgnore) {
            scn.LSChooseCards();
            scn.PassAllResponses();
            assertTrue(scn.IsAboardAsPilot(ship, pilot));
        }
        else {
            scn.LSChooseCard(pilot);
            scn.PassAllResponses();
            assertEquals(Zone.LOST_PILE, pilot.getZone());
            assertEquals(0, scn.gameState().getBattleState().getAttritionRemaining(scn.game(), scn.LS), scn.epsilon);
        }
        assertEquals(Zone.AT_LOCATION, ship.getZone());
    }

    @Test
    public void ClonePilotStatsAndIconsMatchPrintedSource() {
        var scn = scenario();
        var card = scn.GetLSCard("pilot").getBlueprint();
        assertEquals("Clone Pilot", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.REPUBLIC); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC); add(Icon.EPISODE_I); add(Icon.PILOT); add(Icon.LEGACY_BLOCK_5);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.CLONE_TROOPER); }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
    }

    @Test
    public void ClonePilotDeploysAsPilotAndImprovesRepublicShipAtBattleground() {
        var scn = scenario();
        var pilot = scn.GetLSCard("pilot");
        var ship = scn.GetLSCard("ship");
        var system = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship);
        scn.MoveCardsToHand(pilot);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(pilot);
        scn.LSChooseCard(ship);
        scn.LSChooseOption("Pilot");
        scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPilot(ship, pilot));
        assertEquals(7, scn.GetPower(ship));
        assertEquals(5, scn.GetForfeit(pilot));
        assertEquals(2, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), system, scn.LS), scn.epsilon);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        scn.MoveOutOfPlay(pilot);
        assertEquals(5, scn.GetPower(ship));
        assertEquals(1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), system, scn.LS), scn.epsilon);
    }

    @Test
    public void ClonePilotDeploysAboardVehicleAndAddsPowerManeuverAndImmunity() {
        var scn = scenario();
        var pilot = scn.GetLSCard("pilot");
        var vehicle = scn.GetLSCard("vehicle");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vehicle);
        scn.MoveCardsToHand(pilot);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(pilot);
        scn.LSChooseCard(vehicle);
        scn.LSChooseOption("Pilot");
        scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPilot(vehicle, pilot));
        assertEquals(4, scn.GetPower(vehicle));
        assertEquals(6, scn.GetManeuver(vehicle));
        assertEquals(4, scn.GetForfeit(pilot));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vehicle), scn.epsilon);
        scn.MoveOutOfPlay(pilot);
        assertEquals(0, scn.GetPower(vehicle));
        assertEquals(0, scn.GetManeuver(vehicle));
        scn.BoardAsPilot(vehicle, scn.GetLSCard("rebelPilot"));
        assertEquals(4, scn.GetPower(vehicle));
        assertEquals(5, scn.GetManeuver(vehicle));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vehicle), scn.epsilon);
    }

    @Test
    public void ClonePilotIncludesPilotPassengersOnOtherShipsHereButAllowsJediPilots() {
        var scn = scenario();
        var ship = scn.GetLSCard("ship");
        var otherShip = scn.GetLSCard("otherShip");
        var system = scn.GetLSCard("tatooine");
        var kessel = scn.GetLSCard("kessel");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(system, ship);
        scn.MoveCardsToLocation(kessel, otherShip);
        scn.BoardAsPilot(ship, scn.GetLSCard("pilot"));
        scn.BoardAsPassenger(ship, scn.GetLSCard("jedi"));
        scn.BoardAsPassenger(otherShip, scn.GetLSCard("rebelPilot"));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        scn.MoveCardsToLocation(system, otherShip);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        scn.MoveOutOfPlay(otherShip);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }

    @Test
    public void ClonePilotAddsOneToActualForceDrainFromRepublicShipAtBattleground() {
        var scn = scenario();
        var ship = scn.GetLSCard("ship");
        var system = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship);
        scn.BoardAsPilot(ship, scn.GetLSCard("pilot"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSForceDrainAt(system);
        scn.DSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void ClonePilotOtherPilotPassengerLowersImmunityAndLeavingRestoresIt() {
        var scn = scenario();
        var pilot = scn.GetLSCard("pilot");
        var ship = scn.GetLSCard("ship");
        var otherPilot = scn.GetLSCard("rebelPilot");
        var system = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship);
        scn.BoardAsPilot(ship, pilot);
        scn.BoardAsPassenger(ship, otherPilot);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        scn.MoveOutOfPlay(otherPilot);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }
}
