package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class StartingCreditsSaveTests {
    private GameSaveCodec codec() {
        return new GameSaveCodec(VirtualTableScenario._cardLibrary,VirtualTableScenario._formatLibrary,SaveBuild.current());
    }
    private GameSaveHeader header() {
        var h=new GameSaveHeader();h.gameId="credits-save";h.formatCode="premiere_reflections_gold";h.startingCredits=5;h.seed=4242;
        var ls=BotGame.lsDeck();ls.addCard("15_20");ls.addCard("15_22");
        var ds=BotGame.dsDeck();ds.addCard("15_170");
        h.seats.add(GameSaveHeader.seat(BotGame.LS,BotGame.LS,"ozzel",ls));
        h.seats.add(GameSaveHeader.seat(BotGame.DS,BotGame.DS,"ozzel",ds));
        return h;
    }
    private SaveRoot create(GameSaveHeader h) {
        var decks=new HashMap<String,SwccgDeck>();var clocks=new HashMap<String,Integer>();
        for(var seat:h.seats) {decks.put(seat.playerId,seat.toDeck());clocks.put(seat.playerId,0);}
        var feedback=new DefaultUserFeedback();
        var game=new DefaultSwccgGame(VirtualTableScenario._formatLibrary.getFormat(h.formatCode),decks,feedback,VirtualTableScenario._cardLibrary,clocks,false);
        feedback.setGame(game);game.getGameState().setRandomSeed(h.seed);game.getGameState().setStartingCredits(h.startingCredits);game.startGame();
        return new SaveRoot(game,feedback,BotGame.bots("ozzel","ozzel"));
    }
    private void reachCreditChoice(SaveRoot root,SaveRecorder recorder) {
        var game=BotGame.of(root);
        for(int i=0;i<100;i++) {
            String player=game.pendingPlayer();assertNotNull(player);
            var decision=root.feedback().getAwaitingDecision(player);
            if(decision.getDecisionParameters().containsKey("startingCredits")) return;
            var step=game.botStep(root.bots());
            if(recorder!=null)recorder.answered(root,step.player(),step.answer());
        }
        fail("Setup did not reach credit choice");
    }
    private void pick(SaveRoot root,String player,SaveRecorder recorder) {
        var decision=root.feedback().getAwaitingDecision(player);var ids=decision.getDecisionParameters().get("cardId");
        String answer=String.join(",",ids);
        DecisionApplier.apply(root.game(),root.feedback(),player,answer);
        if(recorder!=null)recorder.answered(root,player,answer);
    }
    @Test public void StartingCreditPendingChoicesAndSelectedHandSurviveFreshSnapshot() {
        var root=create(header());reachCreditChoice(root,null);
        var restored=codec().read(codec().write(root));
        assertEquals(5,restored.game().getGameState().getStartingCredits());
        for(String player:List.of(BotGame.LS,BotGame.DS))
            assertArrayEquals(root.feedback().getAwaitingDecision(player).getDecisionParameters().get("blueprintId"),
                    restored.feedback().getAwaitingDecision(player).getDecisionParameters().get("blueprintId"));
        pick(restored,BotGame.LS,null);
        var afterPick=codec().read(codec().write(restored));
        assertEquals(2,afterPick.game().getGameState().getHand(BotGame.LS).size());
        assertNull(afterPick.feedback().getAwaitingDecision(BotGame.LS));
        pick(afterPick,BotGame.DS,null);
        assertEquals(8,afterPick.game().getGameState().getHand(BotGame.LS).size());
        assertEquals(8,afterPick.game().getGameState().getHand(BotGame.DS).size());
        assertTrue(afterPick.game().getGameState().getHand(BotGame.LS).stream().anyMatch(c->c.getBlueprintId(true).equals("15_20")));
    }
    @Test public void StartingCreditSelectionReplaysFromPersistedBudgetAndFullMoveLog() throws Exception {
        var h=header();var root=create(h);var recorder=new SaveRecorder(h,codec());reachCreditChoice(root,recorder);
        pick(root,BotGame.LS,recorder);pick(root,BotGame.DS,recorder);
        var save=recorder.toSave(root,0);
        var persisted=new GameSave(GameSaveHeader.fromJson(save.getHeader().toJson()),save.getLog(),null,0,null);
        var resumed=new GameResumer(codec(),this::create).resume(persisted);
        assertEquals(GameResumer.Method.MOVE_LOG,resumed.method());
        assertEquals(5,resumed.root().game().getGameState().getStartingCredits());
        assertEquals(GameStateHash.of(root.game()),GameStateHash.of(resumed.root().game()));
    }
    @Test public void StartingCreditsLegacyAnythingGoesHeaderRestoresZeroWithoutNewChoice() {
        var h=header();h.formatCode="anything_goes";
        var old=GameSaveHeader.fromJson(h.toJson().replace("\"startingCredits\":5,",""));
        assertEquals(0,old.startingCredits);
        var root=create(old);var game=BotGame.of(root);
        for(int i=0;i<100 && game.turns()==0;i++) {
            var decision=root.feedback().getAwaitingDecision(game.pendingPlayer());
            assertFalse(decision.getDecisionParameters().containsKey("startingCredits"));
            game.botStep(root.bots());
        }
        assertTrue(game.turns()>0);assertEquals(0,root.game().getGameState().getStartingCredits());
        assertEquals(8,root.game().getGameState().getHand(BotGame.LS).size());
    }
    @Test public void StartingCreditsMediatorLetsBotDeclineWithoutCallingUnawareSelectionPolicy() {
        var h=header();var root=create(h);reachCreditChoice(root,null);
        String gameId="credits-ai";
        var ai=org.mockito.Mockito.mock(com.gempukku.swccgo.ai.SwccgAiController.class);
        var participants=h.seats.stream().map(seat->new com.gempukku.swccgo.game.SwccgGameParticipant(seat.playerId,seat.toDeck()))
                .toArray(com.gempukku.swccgo.game.SwccgGameParticipant[]::new);
        var mediator=new com.gempukku.swccgo.game.SwccgGameMediator(gameId,null,participants,root,3600,true,true,true,true,300,false);
        com.gempukku.swccgo.ai.AiRegistry.register(gameId,BotGame.DS,ai);
        try {
            mediator.resumeGame();
            assertNull(root.feedback().getAwaitingDecision(BotGame.DS));
            assertNotNull(root.feedback().getAwaitingDecision(BotGame.LS));
            org.mockito.Mockito.verify(ai,org.mockito.Mockito.never()).decide(org.mockito.ArgumentMatchers.anyString(),org.mockito.ArgumentMatchers.any(),org.mockito.ArgumentMatchers.any());
            pick(root,BotGame.LS,null);
            assertEquals(8,root.game().getGameState().getHand(BotGame.DS).size());
        } finally { com.gempukku.swccgo.ai.AiRegistry.unregisterGame(gameId); }
    }
}
