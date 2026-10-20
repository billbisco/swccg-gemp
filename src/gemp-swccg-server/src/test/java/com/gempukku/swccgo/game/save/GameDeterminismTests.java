package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameDeterminismTests {
    private static final int TURNS = 8;

    private static List<BotGame.Step> playWithBots(BotGame g, Map<String, SwccgAiController> bots, int turns) {
        List<BotGame.Step> steps = new ArrayList<>();
        while (g.canContinue() && g.turns() <= turns) {
            steps.add(g.botStep(bots));
        }
        return steps;
    }

    private static List<BotGame.Step> replay(BotGame g, List<BotGame.Step> log) {
        List<BotGame.Step> steps = new ArrayList<>();
        for (BotGame.Step logged : log) {
            assertEquals(logged.player(), g.pendingPlayer());
            steps.add(g.answer(logged.player(), logged.answer()));
        }
        return steps;
    }

    /** Allocates a random number of objects and asks for their identity hashes, so identity hashes differ from the recorded run. */
    private static void perturbIdentityHashes() {
        int n = 50_000 + new Random().nextInt(50_000);
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += System.identityHashCode(new Object());
        }
        assertNotEquals(Long.MIN_VALUE, sum);
    }

    @Test
    public void SeededGameReplaysIdenticallyFromLoggedAnswers() {
        //test1: a seeded bot game replayed from its logged answers matches its position hash at every step
        List<BotGame.Step> recorded = playWithBots(new BotGame(42), BotGame.bots("ozzel", "yoda"), TURNS);
        assertTrue(recorded.size() > 200);

        perturbIdentityHashes();
        List<BotGame.Step> replayed = replay(new BotGame(42), recorded);

        assertEquals(recorded, replayed); //test1
    }

    @Test
    public void SeededGameReplaysIdenticallyWithAnotherGameInterleaved() {
        //test1: two games played step by step in turn, then the first replayed alone, matches at every step
        //test2: the other game, replayed alone, also matches
        BotGame a = new BotGame(7);
        BotGame b = new BotGame(1001);
        Map<String, SwccgAiController> botsA = BotGame.bots("yoda", "ozzel");
        Map<String, SwccgAiController> botsB = BotGame.bots("ozzel", "ozzel");
        List<BotGame.Step> logA = new ArrayList<>();
        List<BotGame.Step> logB = new ArrayList<>();
        while ((a.canContinue() && a.turns() <= TURNS) || (b.canContinue() && b.turns() <= TURNS)) {
            if (a.canContinue() && a.turns() <= TURNS) {
                logA.add(a.botStep(botsA));
            }
            if (b.canContinue() && b.turns() <= TURNS) {
                logB.add(b.botStep(botsB));
            }
        }

        assertEquals(logA, replay(new BotGame(7), logA)); //test1
        assertEquals(logB, replay(new BotGame(1001), logB)); //test2
    }

    @Test
    public void SeededGameShufflesTheSameWayEachTime() {
        //test1: same seed, same opening Reserve Deck order
        //test2: a different seed gives a different order
        assertEquals(GameStateHash.of(new BotGame(5).game), GameStateHash.of(new BotGame(5).game)); //test1
        assertNotEquals(GameStateHash.of(new BotGame(5).game), GameStateHash.of(new BotGame(6).game)); //test2
    }

    @Test
    public void RandomCardPicksUseTheGameRandom() {
        //test1: two games with the same seed pick the same random cards from the same pile
        //test2: a pick in another game in between does not change that
        BotGame a = new BotGame(11);
        BotGame b = new BotGame(11);
        BotGame other = new BotGame(12);
        List<PhysicalCard> pickA = GameUtils.getRandomCards(a.game, a.game.getGameState().getReserveDeck(BotGame.LS), 5);
        GameUtils.getRandomCards(other.game, other.game.getGameState().getReserveDeck(BotGame.LS), 5);
        List<PhysicalCard> pickB = GameUtils.getRandomCards(b.game, b.game.getGameState().getReserveDeck(BotGame.LS), 5);

        assertEquals(ids(pickA), ids(pickB)); //test1, test2
    }

    @Test
    public void PhysicalCardHashCodeIsThePermanentCardId() {
        //test1: hash-ordered card collections iterate in the same order in every run
        BotGame g = new BotGame(3);
        for (PhysicalCard card : g.game.getGameState().getAllPermanentCards()) {
            assertEquals(card.getPermanentCardId(), card.hashCode()); //test1
        }
    }

    private static List<Integer> ids(List<PhysicalCard> cards) {
        List<Integer> ids = new ArrayList<>();
        for (PhysicalCard c : cards) {
            ids.add(c.getPermanentCardId());
        }
        return ids;
    }
}
