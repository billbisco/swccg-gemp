package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("pilot", "1_27");
                    put("ywing", "1_147");
                }},
                new HashMap<>() {{
                    put("bewil", "1220_4");
                    put("aloo", "1230_18");
                    put("evax", "1240_12");
                    put("room", "1240_51");
                    put("interrupt", "1_239");
                    put("tatooine", "1_289");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void CaptainBewilStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bewil").getBlueprint();

        assertEquals("Captain Bewil", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getInfluence(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
            add(Keyword.CAPTAIN);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CaptainBewilHasLogisticsAndTakesInterruptWhenInfluenceHereExceedsThree() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var bewil = scn.GetDSCard("bewil");
        var aloo = scn.GetDSCard("aloo");
        var evax = scn.GetDSCard("evax");
        var room = scn.GetDSCard("room");
        var interrupt = scn.GetDSCard("interrupt");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, bewil);
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToTopOfDSForcePile(interrupt);

        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), bewil, Agenda.LOGISTICS));
        assertFalse(scn.DSCardActionAvailable(bewil, "Take Interrupt into hand from Force Pile"));

        scn.MoveCardsToLocation(room, aloo, evax);
        assertTrue(scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room) > 3);
        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.DSCardActionAvailable(bewil, "Take Interrupt into hand from Force Pile"));

        scn.DSUseCardAction(bewil, "Take Interrupt into hand from Force Pile");
        scn.DSChooseCard(interrupt);
        assertEquals(Zone.HAND, interrupt.getZone());
    }

    @Test
    public void CaptainBewilInterruptTakeIsNotOfferedDuringOpponentsTurn() {
        var scn = GetScenario();
        var bewil = scn.GetDSCard("bewil");
        var aloo = scn.GetDSCard("aloo");
        var evax = scn.GetDSCard("evax");
        var room = scn.GetDSCard("room");
        var interrupt = scn.GetDSCard("interrupt");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, bewil, aloo, evax, luke);
        scn.MoveCardsToTopOfDSForcePile(interrupt);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(room));
        scn.LSInitiateBattle(room);
        scn.LSPass();
        assertTrue(scn.DSGetDecision() != null);
        assertTrue(scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room) > 3);
        assertFalse(scn.DSCardActionAvailable(bewil, "Take Interrupt into hand from Force Pile"));
    }

    @Test
    public void CaptainBewilMakesOpponentLoseThreeForceFromUsedPileToReactAway() {
        var scn = GetReactScenario();
        var bewil = scn.GetDSCard("bewil");
        var room = scn.GetDSCard("room");
        var arcona = scn.GetLSCard("arcona");
        var cantina = scn.GetLSCard("cantina");
        var trooper = scn.GetDSCard("trooper");
        var adjacent = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(room, bewil);
        scn.MoveCardsToLocation(cantina, arcona, trooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DrainLSForcePileTo(1);
        for (var card : new java.util.ArrayList<>(scn.GetLSReserveDeck())) {
            scn.MoveCardToZone(scn.LS, (com.gempukku.swccgo.game.PhysicalCardImpl) card, Zone.USED_PILE);
        }
        assertEquals(1, scn.GetLSForcePileCount());
        assertEquals(0, scn.GetLSReserveDeckCount());
        assertTrue(scn.gameState().getUsedPile(scn.LS).size() >= 3);
        assertTrue(scn.AwaitingDSBattlePhaseActions());

        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(cantina);
        assertTrue(scn.LSCardActionAvailable(arcona));
        scn.LSUseCardAction(arcona);
        scn.LSChooseCard(adjacent);
        scn.PassAllResponses();
        assertTrue("Used-pile Force must pay the react cost. decision=" + scn.LSGetDecision(),
                scn.LSDecisionAvailable("Choose Force to lose"));
    }

    @Test
    public void CaptainBewilCancelsReactAwayWhenOpponentHasFewerThanThreeForce() {
        var scn = GetReactScenario();
        var bewil = scn.GetDSCard("bewil");
        var room = scn.GetDSCard("room");
        var arcona = scn.GetLSCard("arcona");
        var cantina = scn.GetLSCard("cantina");
        var trooper = scn.GetDSCard("trooper");
        var adjacent = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(room, bewil);
        scn.MoveCardsToLocation(cantina, arcona, trooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        // Arcona needs 1 Force in Force Pile to pay the react move. Dump every other
        // Life Force so the total is < 3 and Bewil cancels instead of charging 3.
        scn.DrainLSForcePileTo(1);
        java.util.List<com.gempukku.swccgo.game.PhysicalCardImpl> extras = new java.util.ArrayList<>();
        for (var card : scn.GetLSReserveDeck()) {
            extras.add((com.gempukku.swccgo.game.PhysicalCardImpl) card);
        }
        for (var card : scn.GetLSHand()) {
            extras.add((com.gempukku.swccgo.game.PhysicalCardImpl) card);
        }
        for (var card : scn.gameState().getUsedPile(scn.LS)) {
            extras.add((com.gempukku.swccgo.game.PhysicalCardImpl) card);
        }
        for (var card : extras) {
            scn.gameState().removeCardsFromZone(java.util.Collections.singletonList(card), true, true);
            scn.gameState().addCardToZone(card, Zone.OUT_OF_PLAY, scn.LS, true, true);
        }
        assertEquals(1, scn.GetLSForcePileCount());
        assertEquals(0, scn.GetLSReserveDeckCount());
        assertEquals(0, scn.gameState().getUsedPile(scn.LS).size());
        assertEquals(0, scn.GetLSHandCount());
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        scn.DSInitiateBattle(cantina);
        assertTrue(scn.LSCardActionAvailable(arcona));
        scn.LSUseCardAction(arcona);
        scn.LSChooseCard(adjacent);
        scn.PassAllResponses();
        assertEquals(cantina, arcona.getAtLocation());
        assertFalse(scn.LSDecisionAvailable("Choose Force to lose"));
    }

    private VirtualTableScenario GetReactScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("arcona", "2_001");
                    put("cantina", "1_128");
                }},
                new HashMap<>() {{
                    put("bewil", "1220_4");
                    put("room", "1240_51");
                    put("trooper", "1_194");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }
}
