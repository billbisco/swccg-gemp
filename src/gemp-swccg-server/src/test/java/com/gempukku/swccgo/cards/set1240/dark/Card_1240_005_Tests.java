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
                        Map.entry("garindan", "1_177"), Map.entry("junk", "1_249"), Map.entry("tie", "1_303"))),
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
    public void GrandAdmiralThrawnLosesYourIndependentStarshipsWhenHeDeploys() {
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
        for (int i = 0; i < 4 && scn.DSDecisionAvailable("Choose card to be lost"); i++) {
            scn.DSChooseCard(scn.DSHasCardChoiceAvailable(hunter) ? hunter : punishing);
            passOptional(scn);
        }
        assertInZone(Zone.LOST_PILE, hunter, punishing);
        assertAtLocation(system, pulsar, ship);
        assertInHand(ig);
    }

    @Test
    public void GrandAdmiralThrawnMakesYourIndependentStarshipDeployedLaterLostButNotOthers() {
        // Mist Hunter (no permanent pilot) cannot deploy unpiloted here, so it arrives by cheat while Thrawn is on
        // table; the next table change (a TIE Advanced x1 deploying) makes it lost while the TIE stays.
        var scn = scenario();
        var system = scn.GetDSCard("system");
        var ship = scn.GetDSCard("devastator");
        var hunter = scn.GetDSCard("hunter");
        var tie = scn.GetDSCard("tie");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship);
        attachPilot(scn, scn.GetDSCard("thrawn"), ship);
        scn.MoveCardsToDSHand(tie);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(12);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.MoveCardsToLocation(system, hunter);

        scn.DSDeployCard(tie);
        if (scn.DSHasCardChoiceAvailable(system)) scn.DSChooseCard(system);
        passOptional(scn);
        for (int i = 0; i < 4 && scn.DSDecisionAvailable("Choose card to be lost"); i++) {
            scn.DSChooseCard(hunter);
            passOptional(scn);
        }
        assertInZone(Zone.LOST_PILE, hunter);
        assertAtLocation(system, tie, ship);
    }

    @Test
    public void GrandAdmiralThrawnPaysForRealDreadnaughtAtHisOwnLowParsecSystemOutsideDeployPhase() {
        var scn = atHoth();
        var thrawn = scn.GetDSCard("thrawn");
        var dread = scn.GetDSCard("deployDread");
        var hoth = scn.GetLSCard("hoth");
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(8);
        scn.MoveCardsToTopOfDSReserveDeck(dread, scn.GetDSCard("capital"));
        scn.SkipToPhase(Phase.DRAW);
        int force = scn.GetDSForcePileCount();

        assertEquals(5, hoth.getBlueprint().getParsec());
        assertTrue(scn.DSCardActionAvailable(thrawn, DEPLOY));
        scn.DSUseCardAction(thrawn, DEPLOY);
        assertTrue(selectable(scn, dread));
        assertFalse(selectable(scn, scn.GetDSCard("capital")));
        scn.DSChooseCard(dread);
        if (scn.DSHasCardChoiceAvailable(hoth)) {
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("otherSystem")));
            scn.DSChooseCard(hoth);
        }
        passOptional(scn);
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
        passOptional(scn);
        assertTrue(selectable(scn, target));
        assertTrue(selectable(scn, other));
        assertFalse(selectable(scn, junk));
        scn.DSChooseCard(target);
        passOptional(scn);
        // Both Dreadnaughts share a blueprint, so either copy may be the one retrieved.
        assertEquals(1, (target.getZone() != Zone.LOST_PILE ? 1 : 0) + (other.getZone() != Zone.LOST_PILE ? 1 : 0));
        assertInZone(Zone.LOST_PILE, junk);
    }

    @Test
    public void GrandAdmiralThrawnDeployOrRetrieveIsOneUsePerTurn() {
        var scn = atHoth();
        var thrawn = scn.GetDSCard("thrawn");
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("retrieveDread"), scn.GetDSCard("otherDread"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(thrawn, DEPLOY) || scn.DSCardActionAvailable(thrawn, RETRIEVE));
        scn.DSUseCardAction(thrawn, RETRIEVE);
        passOptional(scn);
        scn.DSChooseCard(scn.GetDSCard("retrieveDread"));
        passOptional(scn);
        assertTrue(scn.GetDSCard("retrieveDread").getZone() != Zone.LOST_PILE
                || scn.GetDSCard("otherDread").getZone() != Zone.LOST_PILE);
        if (scn.LSDecisionAvailable("Control action or Pass")) scn.LSPass();
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
        passOptional(scn);
        assertTrue(scn.DSDecisionAvailable("Opponent's hand"));
        assertFalse(scn.LSDecisionAvailable("Opponent's hand"));
        scn.DSDecided("");
        passOptional(scn);
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

    /** True if the Dark Side may actually pick the card (pile browses also list unselectable cards). */
    private static boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] ids = scn.GetADParam(scn.DS, "cardId");
        String[] blueprints = scn.GetADParam(scn.DS, "blueprintId");
        String[] flags = scn.GetADParam(scn.DS, "selectable");
        if (flags == null) return scn.DSHasCardChoiceAvailable(card);
        for (int i = 0; i < flags.length; i++) {
            boolean temp = ids == null || ids[i].startsWith("temp");
            boolean same = temp ? (blueprints != null && blueprints[i].equals(card.getBlueprintId(true)))
                    : ids[i].equals(String.valueOf(card.getCardId()));
            if (same) return Boolean.parseBoolean(flags[i]);
        }
        return false;
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
        passOptional(scn);
    }

    private static void attachPilot(VirtualTableScenario scn, PhysicalCardImpl pilot, PhysicalCardImpl ship) {
        scn.RemoveCardZone(pilot);
        scn.gameState().attachCardInPilotCapacitySlot(pilot, ship);
    }

    /** Passes every pending optional-response window (PassAllResponses stops after 20), then any remaining responses. */
    private static void passOptional(VirtualTableScenario scn) {
        for (int i = 0; i < 100; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }
}
