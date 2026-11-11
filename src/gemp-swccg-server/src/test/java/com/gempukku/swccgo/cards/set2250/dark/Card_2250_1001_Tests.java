package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

/** Batch R8-P: the card-list version 2250_1001 of 1250_1 keeps the behaviour the current card had before the Master List text (2013) became current; these are the former Card_1250_001_Tests tests run on 2250_1001. */
public class Card_2250_1001_Tests {
    private static final String BOOST = "Add 1 to this Force drain";

    private VirtualTableScenario scenario(boolean orderOnTable) {
        return scenario(orderOnTable, false);
    }

    private VirtualTableScenario scenario(boolean orderOnTable, boolean withObjective) {
        var light = new HashMap<>(Map.of("capital", "2_73"));
        if (!withObjective) light.put("rebel", "1_28");
        var dark = new HashMap<>(Map.ofEntries(Map.entry("order", "2250_1001"), Map.entry("system", "1_289"),
                        Map.entry("cantina", "1_290"), Map.entry("capital", "1_301"),
                        Map.entry("trooper", "1_194"), Map.entry("presence", "14_69"), Map.entry("bespin", "5_164"),
                        Map.entry("sector", "5_165"), Map.entry("car", "5_178"), Map.entry("vaderShip", "1_306"),
                        Map.entry("obsidian1", "7_305"), Map.entry("obsidian2", "7_306"),
                        Map.entry("shuttle", "7_309"), Map.entry("scout", "1_305"),
                        Map.entry("cantinaTrooper", "1_194")));
        if (withObjective) dark.put("market", "12_176");
        var scn = new VirtualTableScenario(light, dark, 28, 28, StartingSetup.DefaultLSGroundLocation,
                withObjective ? StartingSetup.CarbonChamberObjective : StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        if (withObjective) scn.MoveLocationToTable(scn.GetDSCard("market"));
        for (String location : List.of("system", "cantina", "bespin", "sector")) {
            scn.MoveLocationToTable(scn.GetDSCard(location));
        }
        if (orderOnTable) scn.MoveCardsToSideOfTable(scn.GetDSCard("order"));
        else scn.MoveCardsToDSHand(scn.GetDSCard("order"));
        return scn;
    }

    @Test public void NothingCanGetThroughOurShieldPrintedMetadataMatches() {
        var scn = scenario(false);
        var b = scn.GetDSCard("order").getBlueprint();
        assertEquals("Nothing Can Get Through Our Shield", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(6, b.getDestiny(), scn.epsilon);
        assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.ADMIRALS_ORDER)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.ADMIRALS_ORDER, Icon.THEED_PALACE,
                Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void NothingCanGetThroughOurShieldDeploysAndDiscountsAnActualDarkDeployment() {
        var scn = scenario(false);
        var site = scn.GetDSStartingLocation();
        var order = scn.GetDSCard("order");
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("capital"));
        scn.MoveCardsToDSHand(trooper);
        scn.SkipToPhase(Phase.DEPLOY);
        float original = deployCost(scn, trooper, site);
        scn.DSDeployCard(order);
        scn.PassAllResponses();
        if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertInZone(Zone.SIDE_OF_TABLE, order);
        assertEquals(original - 1, deployCost(scn, trooper, site), scn.epsilon);
        scn.EnsureDSForcePile((int) original - 1);
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSDeployCard(trooper);
        if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertSame(site, trooper.getAtLocation());
        assertEquals(forceBefore - (int) original + 1, scn.GetDSForcePileCount());
    }

    @Test public void NothingCanGetThroughOurShieldDiscountsEitherSideWithControlAndCapitalOnly() {
        var scn = scenario(true);
        var system = scn.GetDSCard("system");
        var site = scn.GetDSStartingLocation();
        var rebel = scn.GetLSCard("rebel");
        var trooper = scn.GetDSCard("trooper");
        var presence = scn.GetDSCard("presence");
        var ship = scn.GetDSCard("vaderShip");
        var capital = scn.GetDSCard("capital");
        var lightCapital = scn.GetLSCard("capital");
        float darkBase = deployCost(scn, trooper, site);
        float lightBase = deployCost(scn, rebel, site);
        float presenceBase = deployCost(scn, presence, site);
        float shipBase = deployCost(scn, ship, system);
        float unrelatedBase = deployCost(scn, trooper, scn.GetLSStartingLocation());
        scn.MoveCardsToLocation(system, capital);
        assertEquals(darkBase - 1, deployCost(scn, trooper, site), scn.epsilon);
        assertEquals(lightBase, deployCost(scn, rebel, site), scn.epsilon);
        assertEquals(presenceBase, deployCost(scn, presence, site), scn.epsilon);
        assertEquals(shipBase, deployCost(scn, ship, system), scn.epsilon);
        assertEquals(unrelatedBase, deployCost(scn, trooper, scn.GetLSStartingLocation()), scn.epsilon);
        scn.MoveCardsToLocation(system, lightCapital);
        assertEquals(darkBase, deployCost(scn, trooper, site), scn.epsilon);
        assertEquals(lightBase, deployCost(scn, rebel, site), scn.epsilon);
        scn.MoveCardsToDSHand(capital);
        assertEquals(lightBase - 1, deployCost(scn, rebel, site), scn.epsilon);
        assertEquals(darkBase, deployCost(scn, trooper, site), scn.epsilon);
        scn.MoveCardsToLSHand(lightCapital);
        scn.MoveCardsToLocation(system, scn.GetDSCard("obsidian1"), scn.GetDSCard("obsidian2"),
                scn.GetDSCard("shuttle"));
        assertEquals(darkBase, deployCost(scn, trooper, site), scn.epsilon);
    }

    @Test public void NothingCanGetThroughOurShieldDiscountsVehiclesAtRelatedSectors() {
        var scn = scenario(true);
        var car = scn.GetDSCard("car");
        var sector = scn.GetDSCard("sector");
        float base = deployCost(scn, car, sector);
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("capital"));
        assertEquals(base, deployCost(scn, car, sector), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetDSCard("bespin"), scn.GetDSCard("capital"));
        assertEquals(base - 1, deployCost(scn, car, sector), scn.epsilon);
    }

    @Test public void NothingCanGetThroughOurShieldMayBoostEachDrainWhileContestedAndCapsLoss() {
        var scn = scenario(true);
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("capital"), scn.GetLSCard("capital"));
        prepareDrainSites(scn);
        drain(scn, scn.GetDSStartingLocation(), true, true, 2);
        drain(scn, scn.GetDSCard("cantina"), true, true, 2);
    }

    @Test public void NothingCanGetThroughOurShieldNeedsThreeUniqueShipsWithoutCapitalAndCanDecline() {
        var scn = scenario(true);
        var system = scn.GetDSCard("system");
        scn.MoveCardsToLocation(system, scn.GetDSCard("obsidian1"), scn.GetDSCard("obsidian2"),
                scn.GetDSCard("scout"));
        prepareDrainSites(scn);
        drain(scn, scn.GetDSStartingLocation(), false, false, 1);
        scn.MoveCardsToLocation(system, scn.GetDSCard("shuttle"));
        drain(scn, scn.GetDSCard("cantina"), true, false, 2);
    }

    @Test public void NothingCanGetThroughOurShieldCannotBoostWithOwnObjectiveOnTable() {
        var scn = scenario(true, true);
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("capital"));
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetDSCard("testing"));
        var market = scn.GetDSCard("market");
        scn.MoveCardsToLocation(market, scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.CONTROL);
        drain(scn, market, false, false, 1);
    }

    private void prepareDrainSites(VirtualTableScenario scn) {
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetDSCard("cantina"), scn.GetDSCard("cantinaTrooper"));
        scn.SkipToPhase(Phase.CONTROL);
    }

    private void drain(VirtualTableScenario scn, PhysicalCardImpl site, boolean offered, boolean use, int loss) {
        int before = scn.GetLSLostPileCount();
        scn.DSForceDrainAt(site);
        boolean sawOffer = false;
        for (int i = 0; i < 45 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision
                    && scn.DSCardActionAvailable(scn.GetDSCard("order"), BOOST)) {
                sawOffer = true;
                if (use) scn.DSUseCardAction(scn.GetDSCard("order"), BOOST); else scn.DSPass();
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected drain decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(offered, sawOffer);
        assertEquals(loss, scn.GetLSLostPileCount() - before);
    }

    private float deployCost(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl location) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card,
                location, false, null, false, 0, null, true);
    }
}
