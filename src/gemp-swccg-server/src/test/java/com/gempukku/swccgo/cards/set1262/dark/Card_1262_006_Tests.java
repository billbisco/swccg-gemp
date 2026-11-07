package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_006_Tests {
    private static final String LANDING = "Deploy a Presence droid or Neimoidian aboard the Landing Craft";
    private static final String PAIR = "Reveal a transport or droid pilot and deploy the pair";

    private VirtualTableScenario scenario(boolean ordersOnTable) {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("obi", "1_21", "alter", "1_71")),
                new HashMap<>(Map.ofEntries(Map.entry("orders", "1262_6"), Map.entry("craft", "7_307"),
                        Map.entry("otherCraft", "7_307"), Map.entry("scout", "1_305"), Map.entry("tank", "14_121"),
                        Map.entry("pilot", "14_74"), Map.entry("nextPilot", "14_83"),
                        Map.entry("presence", "14_69"),
                        Map.entry("neimoidian", "14_81"), Map.entry("trooper", "1_194"),
                        Map.entry("system", "1_289"), Map.entry("bay", "1_291"))),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveLocationToTable(scn.GetDSCard("bay"));
        if (ordersOnTable) scn.MoveCardsToSideOfTable(scn.GetDSCard("orders"));
        else scn.MoveCardsToDSHand(scn.GetDSCard("orders"));
        return scn;
    }

    @Test public void DeploymentOrdersPrintedMetadataMatches() {
        var scn = scenario(false);
        var b = scn.GetDSCard("orders").getBlueprint();
        assertEquals("Deployment Orders", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.isLegacy());
        assertFalse(b.hasVirtualSuffix());
        assertEquals("The amount of time it takes for a sub-command to transfer to an officer battle droid, and then to the rest of his regiment, is .396 of a second.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.THEED_PALACE,
                Icon.EPISODE_I, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void DeploymentOrdersActuallyDeploysAndIsImmuneToAlter() {
        var scn = scenario(false);
        var orders = scn.GetDSCard("orders");
        var alter = scn.GetLSCard("alter");
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(orders);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, orders);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("obi"));
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(alter));
    }

    @Test public void DeploymentOrdersActuallyDeploysEitherPrintedTypeAboardTheCraftThatLanded() {
        landAndDeploy("craft", "presence", true);
        landAndDeploy("craft", "neimoidian", true);
    }

    @Test public void DeploymentOrdersDoesNotRespondToANonLandingCraftActuallyLanding() {
        landAndDeploy("scout", "presence", false);
    }

    private void landAndDeploy(String ship, String cardKey, boolean expected) {
        var scn = scenario(true);
        var orders = scn.GetDSCard("orders");
        var craft = scn.GetDSCard(ship);
        var otherCraft = scn.GetDSCard("otherCraft");
        var bay = scn.GetDSCard("bay");
        var passenger = scn.GetDSCard(cardKey);
        scn.MoveCardsToLocation(scn.GetDSCard("system"), craft);
        scn.MoveCardsToLocation(bay, otherCraft);
        scn.SkipToPhase(Phase.CONTROL);
        scn.EnsureDSForcePile(10);
        scn.MoveCardsToTopOfDSReserveDeck(passenger, scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(orders, LANDING));
        int forceBefore = scn.GetDSForcePileCount();
        var remaining = new ArrayList<>(scn.GetDSReserveDeck());
        if (expected) assertTrue(remaining.remove(passenger));
        scn.gameState().getRandom().setSeed(4096);
        scn.DSUseCardAction(craft, "Land");
        boolean offered = false;
        for (int i = 0; i < 80 && !scn.AwaitingDSMovePhaseActions(); i++) {
            if (scn.DSHasCardChoiceAvailable(bay)) scn.DSChooseCard(bay);
            else if (scn.DSHasCardChoiceAvailable(passenger)) {
                scn.DSChooseCard(passenger);
            } else if (scn.DSDecisionAvailable("Choose capacity slot")) scn.DSChoose("Passenger");
            else if (scn.DSHasCardChoiceAvailable(craft)) {
                assertFalse(scn.DSHasCardChoiceAvailable(otherCraft));
                scn.DSChooseCard(craft);
            } else if (scn.DSGetDecision() instanceof CardActionSelectionDecision
                    && scn.DSCardActionAvailable(orders, LANDING)) {
                offered = true;
                assertTrue(expected);
                scn.DSUseCardAction(orders, LANDING);
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected landing/deploy decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertEquals(expected, offered);
        assertSame(bay, craft.getAtLocation());
        if (expected) {
            assertSame(craft, passenger.getAttachedTo());
            assertEquals(forceBefore - 1 - (int) (float) passenger.getBlueprint().getDeployCost(),
                    scn.GetDSForcePileCount());
            assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
            assertNotEquals(remaining, scn.GetDSReserveDeck());
        } else {
            assertInZone(Zone.RESERVE_DECK, passenger);
            assertEquals(remaining, scn.GetDSReserveDeck());
        }
    }

    @Test public void DeploymentOrdersRevealsVehicleAndTakesPilotForRealSimultaneousDeployment() {
        pair("tank", "pilot", "tank", "pilot", "bay");
    }

    @Test public void DeploymentOrdersRevealsPilotAndTakesLandingCraftForRealSimultaneousDeployment() {
        pair("pilot", "craft", "craft", "pilot", "system");
    }

    private void pair(String reveal, String partner, String vehicleKey, String pilotKey, String destinationKey) {
        var scn = scenario(true);
        var orders = scn.GetDSCard("orders");
        var revealed = scn.GetDSCard(reveal);
        var searched = scn.GetDSCard(partner);
        var vehicle = scn.GetDSCard(vehicleKey);
        var pilot = scn.GetDSCard(pilotKey);
        var destination = scn.GetDSCard(destinationKey);
        var secondCraft = scn.GetDSCard("otherCraft");
        var secondPilot = scn.GetDSCard("nextPilot");
        scn.MoveCardsToDSHand(revealed, secondCraft, scn.GetDSCard("presence"), scn.GetDSCard("scout"));
        scn.SkipToPhase(Phase.CONTROL);
        scn.EnsureDSForcePile(16);
        scn.MoveCardsToTopOfDSReserveDeck(searched, secondPilot, scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.DEPLOY);
        var remaining = new ArrayList<>(scn.GetDSReserveDeck());
        remaining.remove(searched);
        scn.gameState().getRandom().setSeed(4096);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(orders, PAIR));
        scn.DSUseCardAction(orders, PAIR);
        boolean revealedToLight = false;
        for (int i = 0; i < 90 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.LSDecisionAvailable("Revealed card")) {
                revealedToLight = true;
                scn.LSPass();
            } else if (scn.DSHasCardChoiceAvailable(revealed)) {
                assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("scout")));
                assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("presence")));
                scn.DSChooseCard(revealed);
            } else if (scn.DSHasCardChoiceAvailable(searched)) scn.DSChooseCard(searched);
            else if (scn.DSHasCardChoiceAvailable(destination)) scn.DSChooseCard(destination);
            else if (scn.DSDecisionAvailable("Choose capacity slot")) scn.DSChoose("Pilot");
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected reveal/search/paired deployment decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(revealedToLight);
        assertSame(destination, vehicle.getAtLocation());
        assertSame(vehicle, pilot.getAttachedTo());
        assertTrue(scn.IsAboardAsPilot(vehicle, pilot));
        assertEquals(forceBefore - 1 - (int) (float) vehicle.getBlueprint().getDeployCost()
                - (int) (float) pilot.getBlueprint().getDeployCost(), scn.GetDSForcePileCount());
        assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
        assertNotEquals(remaining, scn.GetDSReserveDeck());

        // The same second pair was already in place when the new action choices were generated.
        assertInZone(Zone.HAND, secondCraft);
        assertInZone(Zone.RESERVE_DECK, secondPilot);
        assertFalse("The second pair cannot be offered in the same turn", scn.DSCardActionAvailable(orders, PAIR));

        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue("The same second pair is available next turn", scn.DSCardActionAvailable(orders, PAIR));
    }

    @Test public void DeploymentOrdersDoesNotRevealAnUnrelatedVehicleOrNonPilotDroid() {
        var scn = scenario(true);
        scn.MoveCardsToDSHand(scn.GetDSCard("scout"), scn.GetDSCard("presence"));
        scn.SkipToPhase(Phase.CONTROL);
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("orders"), PAIR));
    }
}
