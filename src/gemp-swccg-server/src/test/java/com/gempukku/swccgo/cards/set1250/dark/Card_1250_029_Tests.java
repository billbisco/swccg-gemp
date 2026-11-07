package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_029_Tests {
    private static final String DUEL = "Initiate epic duel";

    private VirtualTableScenario scenario(String character, boolean together, boolean epic) {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("obi", "1_21", "luke", "1_19", "prevent", "1_90",
                "loss1", "1_28", "loss2", "1_28", "loss3", "1_28")),
                new HashMap<>(Map.of("anger", "1250_29", "vader", "1_168", "epic", "5_129",
                        "loss1", "1_194", "loss2", "1_194", "loss3", "1_194")),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(together ? site : scn.GetLSStartingLocation(), scn.GetLSCard(character));
        scn.MoveCardsToDSHand(scn.GetDSCard("anger"));
        scn.MoveCardsToLSHand(scn.GetLSCard("prevent"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("loss1"), scn.GetDSCard("loss2"), scn.GetDSCard("loss3"));
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("loss1"), scn.GetLSCard("loss2"), scn.GetLSCard("loss3"));
        if (epic) scn.MoveCardsToSideOfTable(scn.GetDSCard("epic")); else scn.MoveCardsToDSHand(scn.GetDSCard("epic"));
        return scn;
    }

    @Test public void VadersAngerPrintedAttributesIconsAndKeywordsMatch() {
        var scn = scenario("obi", true, false);
        var b = scn.GetDSCard("anger").getBlueprint();
        assertEquals("Vader's Anger", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.LOST, b.getCardSubtype());
        assertEquals(5, b.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        assertEquals("Anger and aggression fuel the dark side of the Force.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.TATOOINE, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void VadersAngerCannotStartEpicDuelOutsideMovePhase() {
        var scn = scenario("obi", true, false);
        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("anger"), DUEL));
    }

    @Test public void VadersAngerNeedsARealJediPresentWithVader() {
        var nonJedi = scenario("luke", true, false);
        nonJedi.SkipToPhase(Phase.MOVE);
        assertFalse(nonJedi.DSCardPlayAvailable(nonJedi.GetDSCard("anger"), DUEL));
        var awayJedi = scenario("obi", false, false);
        awayJedi.SkipToPhase(Phase.MOVE);
        assertFalse(awayJedi.DSCardPlayAvailable(awayJedi.GetDSCard("anger"), DUEL));
    }

    private void duel(VirtualTableScenario scn, boolean lightTurn, boolean acceptRetrieval,
                      boolean expectRetrieval, int d1, int d2, int l1, int l2) {
        if (lightTurn) {
            scn.SkipToLSTurn(Phase.CONTROL);
            scn.EnsureLSForcePile(2);
            scn.SkipToPhase(Phase.MOVE);
            scn.LSPass();
        } else {
            scn.EnsureLSForcePile(2);
            scn.SkipToPhase(Phase.MOVE);
        }
        scn.PrepareDSDestiny(d2);
        scn.PrepareDSDestiny(d1);
        scn.PrepareLSDestiny(l2);
        scn.PrepareLSDestiny(l1);
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("anger"), DUEL));
        if (lightTurn) assertTrue(Filters.filterAllOnTable(scn.game(), Filters.Epic_Duel).isEmpty());
        scn.DSPlayCard(scn.GetDSCard("anger"), DUEL);
        boolean epicSeen = false;
        boolean offerSeen = false;
        for (int i = 0; i < 140; i++) {
            var live = scn.gameState().getDuelState();
            if (live != null) {
                assertTrue(live.isEpicDuel());
                epicSeen = true;
            }
            if (scn.AwaitingDSMovePhaseActions() || scn.AwaitingLSMovePhaseActions()) break;
            if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
            else if (scn.AwaitingDSForceLossPayment()) {
                if (lightTurn) assertInZone(Zone.USED_PILE, scn.GetDSCard("vader"));
                scn.DSPayRemainingForceLossFromReserveDeck();
            } else if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("obi"))) scn.DSChooseCard(scn.GetLSCard("obi"));
            else if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("vader"))) scn.DSChooseCard(scn.GetDSCard("vader"));
            else if (scn.DSDecisionAvailable("Retrieve 2 Force?")) {
                offerSeen = true;
                if (acceptRetrieval) scn.DSChooseYes(); else scn.DSChooseNo();
            } else if (scn.LSDecisionAvailable("Retrieve 2 Force?")) {
                offerSeen = true;
                if (acceptRetrieval) scn.LSChooseYes(); else scn.LSChooseNo();
            } else {
                var decision = scn.GetCurrentDecision();
                assertTrue("Unexpected duel decision: " + decision.getText(), decision instanceof CardActionSelectionDecision);
                if (scn.LSGetDecision() != null) {
                    assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("prevent")));
                    scn.LSPass();
                } else scn.DSPass();
            }
        }
        assertTrue(epicSeen);
        assertEquals(expectRetrieval, offerSeen);
        assertTrue("Duel did not return to Move actions: " + scn.GetCurrentDecision().getText(),
                scn.AwaitingDSMovePhaseActions() || scn.AwaitingLSMovePhaseActions());
        assertInZone(Zone.USED_PILE, scn.GetDSDestiny(d1));
        assertInZone(Zone.USED_PILE, scn.GetDSDestiny(d2));
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(l1));
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(l2));
        var interruptZone = GameUtils.getZoneFromZoneTop(scn.GetDSCard("anger").getZone());
        assertTrue(interruptZone == Zone.LOST_PILE || interruptZone == Zone.USED_PILE);
    }

    @Test public void VadersAngerDrawsTwoEachAndAddsPowerToDecideUsedPileLoser() {
        var scn = scenario("obi", true, false);
        // First draws and power alone tie; the second draw is what puts Vader ahead.
        duel(scn, false, true, true, 1, 4, 2, 3);
        assertInZone(Zone.USED_PILE, scn.GetLSCard("obi"));
        assertInZone(Zone.AT_LOCATION, scn.GetDSCard("vader"));
        assertEquals(5, scn.GetLSLostPileCount());
        assertEquals(2, scn.GetDSLostPileCount()); // three existing, Anger itself, then retrieve two
    }

    @Test public void VadersAngerWithEpicDuelOnTableSendsLoserToLostPileAndWinnerMayDecline() {
        var scn = scenario("obi", true, true);
        duel(scn, false, false, true, 1, 4, 2, 3);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("obi"));
        assertEquals(6, scn.GetLSLostPileCount());
        assertEquals(4, scn.GetDSLostPileCount());
    }

    @Test public void VadersAngerCanBePlayedOnOpponentsMoveTurnAndLightCanWinAndRetrieve() {
        var scn = scenario("obi", true, false);
        duel(scn, true, true, true, 0, 1, 5, 6);
        assertInZone(Zone.USED_PILE, scn.GetDSCard("vader"));
        assertInZone(Zone.AT_LOCATION, scn.GetLSCard("obi"));
        assertEquals(6, scn.GetDSLostPileCount());
        assertEquals(1, scn.GetLSLostPileCount());
    }

    @Test public void VadersAngerTiedTotalsLeaveBothParticipantsAndNoForceSwing() {
        var scn = scenario("obi", true, false);
        duel(scn, false, false, false, 2, 3, 4, 2);
        assertInZone(Zone.AT_LOCATION, scn.GetLSCard("obi"));
        assertInZone(Zone.AT_LOCATION, scn.GetDSCard("vader"));
        assertEquals(3, scn.GetLSLostPileCount());
        assertEquals(4, scn.GetDSLostPileCount());
    }
}
