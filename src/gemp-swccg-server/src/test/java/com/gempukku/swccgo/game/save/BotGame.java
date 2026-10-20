package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.ai.models.AdvancedAi;
import com.gempukku.swccgo.ai.models.BeginnerAi;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A headless P-ANH precon game (Retro League Prelude decks) driven by bots or by logged answers.
 */
class BotGame {
    static final String LS = "lsPlayer";
    static final String DS = "dsPlayer";

    final DefaultSwccgGame game;
    final DefaultUserFeedback feedback;

    /** One answered decision and the position hash right after it. */
    record Step(String player, String answer, String hash) {
    }

    BotGame(long seed) {
        Map<String, SwccgDeck> decks = new HashMap<>();
        decks.put(LS, deck("/save/panh-precon-ls.txt", "LS precon"));
        decks.put(DS, deck("/save/panh-precon-ds.txt", "DS precon"));
        feedback = new DefaultUserFeedback();
        Map<String, Integer> clocks = new HashMap<>();
        clocks.put(LS, 0);
        clocks.put(DS, 0);
        game = new DefaultSwccgGame(VirtualTableScenario._formatLibrary.getFormat("premiere_anh"), decks, feedback, VirtualTableScenario._cardLibrary, clocks, false);
        feedback.setGame(game);
        game.getGameState().setRandomSeed(seed);
        game.startGame();
    }

    static Map<String, SwccgAiController> bots(String ls, String ds) {
        Map<String, SwccgAiController> bots = new HashMap<>();
        bots.put(LS, bot(ls));
        bots.put(DS, bot(ds));
        return bots;
    }

    static SwccgAiController bot(String name) {
        return name.equals("yoda") ? new AdvancedAi() : new BeginnerAi();
    }

    String pendingPlayer() {
        TreeSet<String> pending = new TreeSet<>(feedback.getUsersPendingDecision());
        return pending.isEmpty() ? null : pending.first();
    }

    int turns() {
        return game.getGameState().getPlayersLatestTurnNumber(LS) + game.getGameState().getPlayersLatestTurnNumber(DS);
    }

    boolean canContinue() {
        return !game.isFinished() && pendingPlayer() != null;
    }

    /** The pending player's bot answers one decision. */
    Step botStep(Map<String, SwccgAiController> bots) {
        String player = pendingPlayer();
        SwccgAiController bot = bots.get(player);
        bot.setGame(game);
        String answer = bot.decide(player, feedback.getAwaitingDecision(player), game.getGameState());
        return answer(player, answer);
    }

    /** Applies one answer, the same way the server does. */
    Step answer(String player, String answer) {
        AwaitingDecision decision = feedback.getAwaitingDecision(player);
        feedback.participantDecided(player);
        try {
            decision.decisionMade(answer);
        } catch (Exception e) {
            feedback.sendAwaitingDecision(player, decision);
        }
        game.carryOutPendingActionsUntilDecisionNeeded();
        return new Step(player, answer, GameStateHash.of(game));
    }

    private static SwccgDeck deck(String resource, String name) {
        try (InputStream in = BotGame.class.getResourceAsStream(resource)) {
            SwccgDeck deck = new SwccgDeck(name);
            Matcher m = Pattern.compile("blueprintId=\"([^\"]+)\"").matcher(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            while (m.find()) {
                deck.addCard(m.group(1));
            }
            return deck;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
