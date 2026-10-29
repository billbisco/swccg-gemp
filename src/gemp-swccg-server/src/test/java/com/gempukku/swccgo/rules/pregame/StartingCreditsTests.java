package com.gempukku.swccgo.rules.pregame;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.*;
import com.gempukku.swccgo.logic.decisions.*;
import com.gempukku.swccgo.logic.modifiers.NumCardsDrawnInStartingHandModifier;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class StartingCreditsTests {
    private VirtualTableScenario scenario(String format, int budget) {
        var ls = new HashMap<String,String>();
        ls.put("gift", "15_20"); ls.put("gift2", "15_20"); ls.put("two", "15_22");
        var ds = new HashMap<String,String>(); ds.put("dark", "15_170");
        var s = new VirtualTableScenario(ls, ds, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, format);
        s.gameState().setStartingCredits(budget);
        return s;
    }
    private String choice(AwaitingDecision d, String blueprint, int occurrence) {
        var ids = d.getDecisionParameters().get("blueprintId");
        for (int i=0;i<ids.length;i++) if (ids[i].equals(blueprint) && occurrence-- == 0) return "temp"+i;
        throw new AssertionError("Expected eligible blueprint " + blueprint);
    }
    private AwaitingDecision ls(VirtualTableScenario s) { return s.userFeedback().getAwaitingDecision(s.LS); }
    private void declineDark(VirtualTableScenario s) { s.DSDecided(""); }
    @Test public void StartingCreditsFiveSelectsBothPlayersPrivatelyAndFillsNormalHands() {
        var s=scenario("premiere_reflections_gold",5);
        var owner=new GameCommunicationChannel(s.LS,1); var opponent=new GameCommunicationChannel(s.DS,2);
        var spectator=new GameCommunicationChannel("spectator",3);
        s.game().addGameStateListener(s.LS,owner); s.game().addGameStateListener(s.DS,opponent);
        s.game().addGameStateListener("spectator",spectator);
        s.StartGame(false);
        var d=ls(s);
        assertNotNull(d.getDecisionParameters().get("startingCredits"));
        assertEquals(3,d.getDecisionParameters().get("blueprintId").length);
        assertTrue(owner.consumeGameEvents().stream().anyMatch(e -> e.getAwaitingDecision()==d));
        assertFalse(opponent.consumeGameEvents().stream().anyMatch(e -> e.getAwaitingDecision()==d));
        assertFalse(spectator.consumeGameEvents().stream().anyMatch(e -> e.getAwaitingDecision()==d));
        var reconnectChoice = new GameCommunicationChannel(s.LS,5);
        var reconnectSpectator = new GameCommunicationChannel("spectator-reconnect",6);
        s.game().addGameStateListener(s.LS,reconnectChoice);
        s.game().addGameStateListener("spectator-reconnect",reconnectSpectator);
        assertTrue(reconnectChoice.consumeGameEvents().stream().anyMatch(e -> e.getAwaitingDecision()==d));
        assertFalse(reconnectSpectator.consumeGameEvents().stream().anyMatch(e -> e.getAwaitingDecision()!=null));
        s.LSDecided(choice(d,"15_20",0)+","+choice(d,"15_22",0));
        assertEquals(2,s.gameState().getHand(s.LS).size());
        assertFalse(opponent.consumeGameEvents().stream().anyMatch(e -> "15_20".equals(e.getBlueprintId()) || "15_22".equals(e.getBlueprintId())));
        assertFalse(spectator.consumeGameEvents().stream().anyMatch(e -> "15_20".equals(e.getBlueprintId()) || "15_22".equals(e.getBlueprintId())));
        var reconnect=new GameCommunicationChannel(s.LS,4);
        s.game().addGameStateListener(s.LS,reconnect);
        assertTrue(reconnect.consumeGameEvents().stream().anyMatch(e -> "15_22".equals(e.getBlueprintId())));
        var dark=s.userFeedback().getAwaitingDecision(s.DS);
        s.DSDecided(choice(dark,"15_170",0));
        assertEquals(8,s.gameState().getHand(s.LS).size()); assertEquals(8,s.gameState().getHand(s.DS).size());
        assertEquals(Zone.HAND,s.GetLSCard("two").getZone()); assertEquals(Zone.HAND,s.GetDSCard("dark").getZone());
        assertFalse(s.userFeedback().getAwaitingDecision(s.DS).getDecisionParameters().containsKey("startingCredits"));
    }
    @Test public void StartingCreditsFourRejectsOverBudgetDuplicatesAndMalformedChoicesWithoutMovingCards() throws Exception {
        var s=scenario("premiere_reflections_gold",4);s.StartGame(false);var d=ls(s);
        String gift=choice(d,"15_20",0),two=choice(d,"15_22",0);
        for(String invalid:List.of(gift+","+two,gift+","+gift,"temp99","bad0","temp-1","temp0,"))
            assertThrows(DecisionResultInvalidException.class,()->d.decisionMade(invalid));
        assertEquals(0,s.gameState().getHand(s.LS).size());
        s.LSDecided(gift);declineDark(s);
        assertEquals(8,s.gameState().getHand(s.LS).size());
    }
    @Test public void StartingCreditsAllowsDistinctCopiesAndDecliningUnusedBudget() {
        var s=scenario("anything_goes",6);s.StartGame(false);var d=ls(s);
        s.LSDecided(choice(d,"15_20",0)+","+choice(d,"15_20",1));declineDark(s);
        assertEquals(Zone.HAND,s.GetLSCard("gift").getZone()); assertEquals(Zone.HAND,s.GetLSCard("gift2").getZone());
        var decline=scenario("anything_goes",5);decline.StartGame(false);decline.LSDecided("");declineDark(decline);
        assertEquals(8,decline.gameState().getHand(decline.LS).size());
    }
    @Test public void StartingCreditsRespectsModifiedOpeningHandSizeAndExistingHandSlots() throws Exception {
        var s=scenario("premiere_reflections_gold",5);
        s.game().getModifiersEnvironment().addUntilEndOfTurnModifier(new NumCardsDrawnInStartingHandModifier(s.GetLSCard("gift"),2,s.LS));
        s.MoveCardsToLSHand(s.GetLSFiller(1));
        s.StartGame(false);var d=ls(s);
        assertEquals("1",d.getDecisionParameters().get("max")[0]);
        assertThrows(DecisionResultInvalidException.class,()->d.decisionMade(choice(d,"15_20",0)+","+choice(d,"15_22",0)));
        s.LSDecided(choice(d,"15_22",0));declineDark(s);
        assertEquals(2,s.gameState().getHand(s.LS).size());
    }
    @Test public void StartingCreditsZeroAndNormalFormatsKeepOrdinaryRandomHands() {
        for(String format:List.of("premiere_reflections_gold","open")) {
            var s=scenario(format,0);s.StartGame(false);
            assertEquals(8,s.gameState().getHand(s.LS).size()); assertEquals(8,s.gameState().getHand(s.DS).size());
            assertFalse(s.userFeedback().getAwaitingDecision(s.DS).getDecisionParameters().containsKey("startingCredits"));
        }
    }
    @Test public void StartingCreditsOnlyOffersCardsStillInOwnReserveDeck() {
        var s=scenario("premiere_reflections_gold",5);
        s.MoveOutOfPlay(s.GetLSCard("gift"));s.MoveCardsToLSHand(s.GetLSCard("gift2"));
        s.StartGame(false);var d=ls(s);
        assertArrayEquals(new String[]{"15_22"},d.getDecisionParameters().get("blueprintId"));
        s.LSDecided("temp0");declineDark(s);
        assertEquals(8,s.gameState().getHand(s.LS).size());
    }
    @Test public void StartingCreditsSkipsChoiceWithoutEligibleCardsAndHandlesShortReserveDeck() {
        var none=scenario("premiere_reflections_gold",1);
        none.StartGame(false);
        assertEquals(8,none.gameState().getHand(none.LS).size());
        assertFalse(none.userFeedback().getAwaitingDecision(none.DS).getDecisionParameters().containsKey("startingCredits"));
        var shortDeck=scenario("premiere_reflections_gold",5);
        var keep=Set.of(shortDeck.GetLSCard("gift"),shortDeck.GetLSFiller(1),shortDeck.GetLSFiller(2),shortDeck.GetLSStartingLocation());
        for(var card:List.copyOf(shortDeck.gameState().getReserveDeck(shortDeck.LS)))
            if(!keep.contains(card)) shortDeck.MoveOutOfPlay((PhysicalCardImpl)card);
        shortDeck.StartGame(false);
        shortDeck.LSDecided("temp0");declineDark(shortDeck);
        assertEquals(Zone.HAND,shortDeck.GetLSCard("gift").getZone());
        // Preserve the existing opening-draw rule that leaves one card in Reserve Deck.
        assertEquals(2,shortDeck.gameState().getHand(shortDeck.LS).size());
        assertEquals(1,shortDeck.gameState().getReserveDeck(shortDeck.LS).size());
    }
}
