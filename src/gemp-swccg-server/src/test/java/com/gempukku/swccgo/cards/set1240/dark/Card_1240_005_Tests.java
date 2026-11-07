package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_005_Tests {
    private static final String DEPLOY = "Deploy a Dreadnaught here from Reserve Deck";
    private static final String RETRIEVE = "Retrieve a Dreadnaught";
    private static final String PEEK = "Peek at opponent's hand";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of(
                "hoth", "3_55", "alien", "1_4", "luke", "1_19", "pulsar", "10_20", "privateCard", "1_109")),
                new HashMap<>(Map.ofEntries(
                        Map.entry("thrawn", "1240_5"), Map.entry("devastator", "1_301"),
                        Map.entry("deployDread", "106_13"), Map.entry("retrieveDread", "106_13"),
                        Map.entry("otherDread", "106_13"), Map.entry("hunter", "4_170"),
                        Map.entry("punishing", "4_171"), Map.entry("ig", "4_169"),
                        Map.entry("capital", "1_302"), Map.entry("system", "1_289"),
                        Map.entry("otherSystem", "1_288"), Map.entry("related", "1_293"),
                        Map.entry("garindan", "1_177"), Map.entry("junk", "1_249"))),
                15, 15, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.DeclineLSStartingInterrupt, StartingSetup.DeclineDSStartingInterrupt,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void GrandAdmiralThrawnHasPrintedStatsIconsKeywordsAndPersona() {
        var scn = scenario();
        var card = scn.GetDSCard("thrawn").getBlueprint();
        assertEquals("Grand Admiral Thrawn", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        assertTrue(card.getPersonas().contains(Persona.THRAWN));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.IMPERIAL)));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.IMPERIAL, Icon.PILOT, Icon.WARRIOR,
                Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>(List.of(Keyword.LEADER)));
    }

    @Test
    public void GrandAdmiralThrawnAddsThreeOnlyAsPilot() {
        verifyPilot("Pilot", 3);
        verifyPilot("Passenger", 0);
    }

    private void verifyPilot(String role, int expectedGain) {
        var scn = scenario();
        var ship = scn.GetDSCard("devastator");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), ship);
        scn.MoveCardsToDSHand(scn.GetDSCard("thrawn"));
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.EnsureDSForcePile(6);
        scn.SkipToPhase(Phase.DEPLOY);
        int power = scn.GetPower(ship);
        deployThrawnAboard(scn, ship, role);
        assertTrue(scn.IsAttachedTo(ship, scn.GetDSCard("thrawn")));
        assertEquals(power + expectedGain, scn.GetPower(ship));
    }

    @Test
    public void GrandAdmiralThrawnLosesOwnExistingAndNewlyDeployedIndependentShips() {
        var scn = scenario();
        var system = scn.GetDSCard("system");
        var ship = scn.GetDSCard("devastator");
        var hunter = scn.GetDSCard("hunter");
        var punishing = scn.GetDSCard("punishing");
        var ig = scn.GetDSCard("ig");
        var pulsar = scn.GetLSCard("pulsar");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship, hunter, punishing, pulsar);
        scn.MoveCardsToDSHand(scn.GetDSCard("thrawn"), ig);
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.DEPLOY);
        assertAtLocation(system, hunter, punishing, pulsar);
        deployThrawnAboard(scn, ship, "Pilot");
        assertInZone(Zone.LOST_PILE, hunter, punishing);
        assertAtLocation(system, pulsar, ship);
        assertInHand(ig);

        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        scn.DSDeployCard(ig);
        scn.DSChooseCard(system);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, ig);
        assertAtLocation(system, pulsar, ship);
    }

    @Test
    public void GrandAdmiralThrawnPaysForRealDreadnaughtAtHisOwnLowParsecSystemOutsideDeployPhase() {
        var scn = atHoth();
        var thrawn = scn.GetDSCard("thrawn");
        var dread = scn.GetDSCard("deployDread");
        var hoth = scn.GetLSCard("hoth");
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.EnsureDSForcePile(8);
        scn.MoveCardsToTopOfDSReserveDeck(dread, scn.GetDSCard("capital"));
        scn.SkipToPhase(Phase.DRAW);
        int force = scn.GetDSForcePileCount();

        assertEquals(5, hoth.getBlueprint().getParsec());
        assertTrue(scn.DSCardActionAvailable(thrawn, DEPLOY));
        scn.DSUseCardAction(thrawn, DEPLOY);
        assertTrue(scn.DSHasCardChoiceAvailable(dread));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("capital")));
        scn.DSChooseCard(dread);
        if (scn.DSHasCardChoiceAvailable(hoth)) {
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("otherSystem")));
            scn.DSChooseCard(hoth);
        }
        scn.PassAllResponses();
        assertAtLocation(hoth, dread);
        assertEquals(force - 5, scn.GetDSForcePileCount());
        assertTrue(scn.gameState().getLastMessages().stream().anyMatch(m -> m.contains("shuffles Reserve Deck")));
    }

    @Test
    public void GrandAdmiralThrawnRetrievesOnlyChosenDreadnaughtFromLostPile() {
        var scn = atHoth();
        var thrawn = scn.GetDSCard("thrawn");
        var target = scn.GetDSCard("retrieveDread");
        var other = scn.GetDSCard("otherDread");
        var junk = scn.GetDSCard("junk");
        scn.MoveCardsToTopOfDSLostPile(target, other, junk);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(thrawn, RETRIEVE));
        scn.DSUseCardAction(thrawn, RETRIEVE);
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(target));
        assertTrue(scn.DSHasCardChoiceAvailable(other));
        assertFalse(scn.DSHasCardChoiceAvailable(junk));
        scn.DSChooseCard(target);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, target);
        assertInZone(Zone.LOST_PILE, other, junk);
    }

    @Test
    public void GrandAdmiralThrawnDeployOrRetrieveIsOneUsePerTurn() {
        var scn = atHoth();
        var thrawn = scn.GetDSCard("thrawn");
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("retrieveDread"), scn.GetDSCard("otherDread"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(thrawn, DEPLOY) || scn.DSCardActionAvailable(thrawn, RETRIEVE));
        scn.DSUseCardAction(thrawn, RETRIEVE);
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSCard("retrieveDread"));
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, scn.GetDSCard("retrieveDread"));
        if (scn.LSAnyActionsAvailable()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(thrawn, RETRIEVE));
        assertFalse(scn.DSCardActionAvailable(thrawn, DEPLOY));
    }

    @Test
    public void GrandAdmiralThrawnNeedsHisTurnAndSystemForEitherDreadnaughtAction() {
        var otherTurn = atHoth();
        var thrawn = otherTurn.GetDSCard("thrawn");
        otherTurn.MoveCardsToTopOfDSLostPile(otherTurn.GetDSCard("retrieveDread"));
        otherTurn.MoveCardsToTopOfDSReserveDeck(otherTurn.GetDSCard("deployDread"));
        otherTurn.SkipToLSTurn(Phase.CONTROL);
        otherTurn.LSPass();
        assertTrue(otherTurn.AwaitingDSControlPhaseActions());
        assertFalse(otherTurn.DSCardActionAvailable(thrawn, DEPLOY));
        assertFalse(otherTurn.DSCardActionAvailable(thrawn, RETRIEVE));

        var atSite = scenario();
        var groundThrawn = atSite.GetDSCard("thrawn");
        atSite.StartGame();
        atSite.MoveCardsToLocation(atSite.GetDSStartingLocation(), groundThrawn);
        atSite.MoveCardsToTopOfDSLostPile(atSite.GetDSCard("retrieveDread"));
        atSite.MoveCardsToTopOfDSReserveDeck(atSite.GetDSCard("deployDread"));
        atSite.SkipToDSTurn(Phase.CONTROL);
        assertFalse(atSite.DSCardActionAvailable(groundThrawn, DEPLOY));
        assertFalse(atSite.DSCardActionAvailable(groundThrawn, RETRIEVE));
    }

    @Test
    public void GrandAdmiralThrawnPeeksPrivatelyOncePerTurnAtSameOrRelatedLocation() {
        verifyPeek(false);
        verifyPeek(true);
    }

    private void verifyPeek(boolean related) {
        var scn = scenario();
        var thrawn = scn.GetDSCard("thrawn");
        var system = scn.GetDSCard("system");
        var site = scn.GetDSCard("related");
        var hidden = scn.GetLSCard("privateCard");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveLocationToTable(site);
        if (related) {
            var ship = scn.GetDSCard("devastator");
            scn.MoveCardsToLocation(system, ship);
            attachPilot(scn, thrawn, ship);
        } else {
            scn.MoveCardsToLocation(site, thrawn);
        }
        scn.MoveCardsToLocation(site, scn.GetLSCard("alien"));
        scn.MoveCardsToLSHand(hidden);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(thrawn, PEEK));
        scn.DSUseCardAction(thrawn, PEEK);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Opponent's hand"));
        assertFalse(scn.LSDecisionAvailable("Opponent's hand"));
        scn.DSDecided("");
        scn.PassAllResponses();
        assertInHand(hidden);

        if (scn.AwaitingLSControlPhaseActions()) {
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardActionAvailable(thrawn, PEEK));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(thrawn, PEEK));
    }

    @Test
    public void GrandAdmiralThrawnDoesNotPeekForHisOwnAlienOrRemoteOpponentAlien() {
        var scn = scenario();
        var thrawn = scn.GetDSCard("thrawn");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), thrawn, scn.GetDSCard("garindan"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("alien"));
        scn.MoveCardsToLSHand(scn.GetLSCard("privateCard"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(thrawn, PEEK));
    }

    private VirtualTableScenario atHoth() {
        var scn = scenario();
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("hoth"));
        scn.MoveLocationToTable(scn.GetDSCard("otherSystem"));
        var ship = scn.GetDSCard("devastator");
        scn.MoveCardsToLocation(scn.GetLSCard("hoth"), ship);
        attachPilot(scn, scn.GetDSCard("thrawn"), ship);
        return scn;
    }

    private static void deployThrawnAboard(VirtualTableScenario scn, PhysicalCardImpl ship, String role) {
        scn.DSDeployCard(scn.GetDSCard("thrawn"));
        scn.DSChooseCard(ship);
        scn.DSChoose(role);
        scn.PassAllResponses();
    }

    private static void attachPilot(VirtualTableScenario scn, PhysicalCardImpl pilot, PhysicalCardImpl ship) {
        scn.RemoveCardZone(pilot);
        scn.gameState().attachCardInPilotCapacitySlot(pilot, ship);
    }
}
