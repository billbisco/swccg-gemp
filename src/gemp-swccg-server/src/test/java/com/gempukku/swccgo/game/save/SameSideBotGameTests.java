package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.ai.SwccgAiController;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#177: bots play same-side games (Dark vs Dark, Light vs Light) to the end, from either seat.
 */
public class SameSideBotGameTests {
    private static final int MAX_STEPS = 20000;

    private static String play(BotGame g, Map<String, SwccgAiController> bots) {
        int steps = 0;
        while (g.canContinue() && steps < MAX_STEPS) {
            g.botStep(bots);
            steps++;
        }
        return "steps=" + steps + " turns=" + g.turns() + " finished=" + g.game.isFinished() + " winner=" + g.game.getWinner();
    }

    private static void playsToTheEnd(long seed, boolean dark, String ls, String ds) {
        BotGame g = dark ? new BotGame(seed, BotGame.dsDeck(), BotGame.dsDeck()) : new BotGame(seed, BotGame.lsDeck(), BotGame.lsDeck());
        assertTrue(g.game.getGameState().isSameSideGame());
        String result = play(g, BotGame.bots(ls, ds));
        assertTrue(result, g.game.isFinished());
        assertTrue(result, g.turns() > 4);
    }

    @Test
    public void BeginnerBotsPlayDarkVsDarkToTheEnd() {
        for (long seed : new long[]{7, 1000})
            playsToTheEnd(seed, true, "rando", "rando");
    }

    @Test
    public void BeginnerBotsPlayLightVsLightToTheEnd() {
        for (long seed : new long[]{7, 1000})
            playsToTheEnd(seed, false, "rando", "rando");
    }

    @Test
    public void AdvancedBotsPlaySameSideToTheEnd() {
        playsToTheEnd(42, true, "yoda", "yoda");
        playsToTheEnd(42, false, "yoda", "yoda");
    }
}
