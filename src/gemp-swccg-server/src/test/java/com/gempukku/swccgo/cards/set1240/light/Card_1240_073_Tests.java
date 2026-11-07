package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Card_1240_073_Tests {
    private static final String UPLOAD = "Take card into hand from Reserve Deck";
    private static final String DOUBLE = "Double X for Kessel Run or Rycar's Run";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.ofEntries(
                Map.entry("rayc", "1240_73"), Map.entry("kesselRun", "1_52"),
                Map.entry("rycarRun", "4_36"), Map.entry("bigOne", "4_82"),
                Map.entry("kessel", "1_126"), Map.entry("hoth", "3_55"),
                Map.entry("luminous", "9_77"), Map.entry("ywing", "1_147"),
                Map.entry("han", "1_11"), Map.entry("falcon", "1_143"),
                Map.entry("yacht", "10_20"), Map.entry("corvette", "1_140"),
                Map.entry("redOne", "1_144"))), new HashMap<>(), 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void RaycRyjerdHasPrintedStatsIconsAndKeywords() {
        var scn = scenario();
        var card = scn.GetLSCard("rayc").getBlueprint();
        assertEquals("Rayc Ryjerd", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(CardType.CHARACTER, card.getCardType());
        assertEquals(CardSubtype.ALIEN, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.ALIEN, Icon.PILOT, Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>(List.of(Keyword.SMUGGLER)));
    }

    @Test
    public void RaycRyjerdAddsThreeToHisShipOnlyWhenPiloting() {
        verifyPowerAboard("Pilot", 3);
        verifyPowerAboard("Passenger", 0);
    }

    private void verifyPowerAboard(String role, int addedPower) {
        var scn = scenario();
        var rayc = scn.GetLSCard("rayc");
        var hoth = scn.GetLSCard("hoth");
        var ship = scn.GetLSCard("luminous");
        var other = scn.GetLSCard("ywing");
        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, ship, other);
        scn.MoveCardsToLSHand(rayc);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int base = scn.GetPower(ship);
        int otherPower = scn.GetPower(other);
        scn.LSDeployCard(rayc);
        scn.LSChooseCard(ship);
        scn.LSChoose(role);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(ship, rayc));
        assertEquals(base + addedPower, scn.GetPower(ship));
        assertEquals(otherPower, scn.GetPower(other));
    }

    @Test
    public void RaycRyjerdMatchesFreightersAndYachtsOnly() {
        var scn = scenario();
        var rayc = scn.GetLSCard("rayc");
        scn.StartGame();
        for (String name : List.of("falcon", "yacht")) {
            var ship = scn.GetLSCard(name);
            assertTrue(name, Filters.matchingPilot(ship).accepts(scn.game(), rayc));
            assertTrue(name, Filters.matchingStarship(rayc).accepts(scn.game(), ship));
        }
        for (String name : List.of("corvette", "redOne")) {
            assertFalse(name, Filters.matchingPilot(scn.GetLSCard(name)).accepts(scn.game(), rayc));
        }
    }

    @Test
    public void RaycRyjerdUploadsAnyPrintedTargetButOnlyTwoInTheGame() {
        verifyUploads("bigOne", "kesselRun");
        verifyUploads("rycarRun", "bigOne");
    }

    @Test
    public void RaycRyjerdDoesNotOfferAnUploadFromAnEmptyReserveDeck() {
        var scn = scenario();
        var rayc = scn.GetLSCard("rayc");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), rayc);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(rayc, UPLOAD));
        while (scn.GetLSReserveDeckCount() != 0) {
            scn.MoveCardsToTopOfLSUsedPile(scn.GetTopOfLSReserveDeck());
        }
        // Refresh the native action window after the fixture empties Reserve Deck.
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(rayc, UPLOAD));
    }

    private void verifyUploads(String first, String second) {
        var scn = scenario();
        var rayc = scn.GetLSCard("rayc");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), rayc);
        for (String key : List.of("bigOne", "kesselRun", "rycarRun", "corvette")) {
            var card = scn.GetLSCard(key);
            scn.MoveOutOfPlay(card);
            scn.MoveCardsToTopOfLSReserveDeck(card);
        }
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(rayc, DOUBLE));
        takeRun(scn, rayc, first, List.of("bigOne", "kesselRun", "rycarRun"));
        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.CONTROL);
        var remaining = new ArrayList<>(List.of("bigOne", "kesselRun", "rycarRun"));
        remaining.remove(first);
        takeRun(scn, rayc, second, remaining);
        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(rayc, UPLOAD));
        remaining.remove(second);
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard(remaining.getFirst()));
    }

    private void takeRun(VirtualTableScenario scn, PhysicalCardImpl rayc, String key, List<String> eligible) {
        int force = scn.GetLSForcePileCount();
        var wanted = scn.GetLSCard(key);
        var oldOrder = new ArrayList<>(scn.gameState().getReserveDeck(scn.LS));
        oldOrder.remove(wanted);
        scn.gameState().setRandomSeed(124073);
        assertTrue(scn.LSCardActionAvailable(rayc, UPLOAD));
        scn.LSUseCardAction(rayc, UPLOAD);
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        for (String target : eligible) {
            assertTrue(target, scn.LSHasCardChoiceAvailable(scn.GetLSCard(target)));
        }
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("corvette")));
        scn.LSChooseCard(wanted);
        scn.PassAllResponses();
        assertInHand(wanted);
        assertEquals(force, scn.GetLSForcePileCount());
        assertNotEquals(oldOrder, new ArrayList<>(scn.gameState().getReserveDeck(scn.LS)));
    }

    @Test
    public void RaycRyjerdDoublesTheChosenKesselRunWithoutDoublingRycarRun() {
        completeBothRuns("kesselRun", 6, 2);
    }

    @Test
    public void RaycRyjerdDoublesTheChosenRycarRunThroughRelocationWithoutDoublingKesselRun() {
        completeBothRuns("rycarRun", 3, 4);
    }

    @Test
    public void RaycRyjerdMayLeaveBothRunsAtTheirNormalValue() {
        completeBothRuns(null, 3, 2);
    }

    private void completeBothRuns(String chosen, int kesselRetrieval, int rycarRetrieval) {
        var scn = scenario();
        var rayc = scn.GetLSCard("rayc");
        var kesselRun = scn.GetLSCard("kesselRun");
        var rycarRun = scn.GetLSCard("rycarRun");
        var bigOne = scn.GetLSCard("bigOne");
        var kessel = scn.GetLSCard("kessel");
        var hoth = scn.GetLSCard("hoth");
        var luminous = scn.GetLSCard("luminous");
        var ywing = scn.GetLSCard("ywing");
        var han = scn.GetLSCard("han");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), rayc);
        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(hoth, luminous);
        scn.MoveCardsToLocation(kessel, ywing);
        scn.MoveCardsToLSHand(han, bigOne, kesselRun, rycarRun);
        for (int i = 0; i < 12; i++) {
            scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        }

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(han);
        scn.LSChooseCard(luminous);
        scn.LSChoose("Pilot");
        finishLightAction(scn);
        scn.LSDeployCard(bigOne);
        finishLightAction(scn);
        scn.LSDeployCard(kesselRun);
        scn.LSChooseCard(kessel);
        scn.LSChooseCard(han);
        finishLightAction(scn);
        scn.LSDeployCard(rycarRun);
        scn.LSChooseCard(bigOne);
        scn.LSChooseCard(ywing);
        finishLightAction(scn);
        assertTrue(scn.IsAttachedTo(kessel, kesselRun));
        assertTrue(scn.IsAttachedTo(bigOne, rycarRun));
        assertTrue(scn.LSCardActionAvailable(rayc, DOUBLE));
        if (chosen != null) {
            scn.LSUseCardAction(rayc, DOUBLE);
            scn.PassAllResponses();
            assertTrue(scn.LSHasCardChoiceAvailable(kesselRun));
            assertTrue(scn.LSHasCardChoiceAvailable(rycarRun));
            assertFalse(scn.LSHasCardChoiceAvailable(bigOne));
            scn.LSChooseCard(scn.GetLSCard(chosen));
            finishLightAction(scn);
            assertFalse(scn.LSCardActionAvailable(rayc, DOUBLE));
        }

        scn.PrepareDSDestiny(1);
        scn.SkipToPhase(Phase.MOVE);
        scn.LSUseCardAction(luminous, "hyperspeed");
        scn.LSChooseCard(kessel);
        finishLightAction(scn);
        assertTrue(scn.CardsAtLocation(kessel, luminous));
        scn.LSUseCardAction(ywing, "sector");
        scn.LSChooseCard(bigOne);
        finishLightAction(scn);
        assertTrue(scn.IsAttachedTo(kessel, rycarRun));

        scn.SkipToDSTurn();
        scn.PrepareDSDestiny(1);
        scn.SkipToLSTurn(Phase.MOVE);
        if (chosen != null) assertFalse(scn.LSCardActionAvailable(rayc, DOUBLE));
        int lost = scn.GetLSLostPileCount();
        scn.LSUseCardAction(luminous, "hyperspeed");
        scn.LSChooseCard(hoth);
        finishLightAction(scn);
        assertTrue(scn.CardsAtLocation(hoth, luminous));
        assertEquals(lost + 1 - kesselRetrieval, scn.GetLSLostPileCount());

        lost = scn.GetLSLostPileCount();
        scn.LSUseCardAction(ywing, "sector");
        scn.LSChooseCard(kessel);
        finishLightAction(scn);
        assertTrue(scn.CardsAtLocation(kessel, ywing));
        assertEquals(lost + 1 - rycarRetrieval, scn.GetLSLostPileCount());
        if ("kesselRun".equals(chosen)) verifyLaterKesselRun(scn);
    }

    private void verifyLaterKesselRun(VirtualTableScenario scn) {
        var rayc = scn.GetLSCard("rayc");
        var kesselRun = scn.GetLSCard("kesselRun");
        var kessel = scn.GetLSCard("kessel");
        var hoth = scn.GetLSCard("hoth");
        var luminous = scn.GetLSCard("luminous");
        assertFalse(Filters.onTable.accepts(scn.game(), kesselRun));
        scn.MoveCardsToLSHand(kesselRun);
        for (int i = 0; i < 5; i++) {
            scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        }
        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(kesselRun);
        scn.LSChooseCard(kessel);
        scn.LSChooseCard(scn.GetLSCard("han"));
        finishLightAction(scn);
        assertTrue(scn.IsAttachedTo(kessel, kesselRun));
        assertFalse(scn.LSCardActionAvailable(rayc, DOUBLE));
        scn.PrepareDSDestiny(1);
        scn.SkipToPhase(Phase.MOVE);
        scn.LSUseCardAction(luminous, "hyperspeed");
        scn.LSChooseCard(kessel);
        finishLightAction(scn);
        assertTrue(scn.CardsAtLocation(kessel, luminous));
        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.MOVE);
        int lost = scn.GetLSLostPileCount();
        scn.LSUseCardAction(luminous, "hyperspeed");
        scn.LSChooseCard(hoth);
        finishLightAction(scn);
        assertTrue(scn.CardsAtLocation(hoth, luminous));
        assertEquals(lost + 1 - 3, scn.GetLSLostPileCount());
    }

    private void finishLightAction(VirtualTableScenario scn) {
        scn.PassAllResponses();
        if (scn.DSAnyActionsAvailable()) scn.DSPass();
    }
}
