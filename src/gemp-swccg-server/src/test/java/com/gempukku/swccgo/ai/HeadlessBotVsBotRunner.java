package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.ai.models.AdvancedAi;
import com.gempukku.swccgo.ai.models.BeginnerAi;
import com.gempukku.swccgo.ai.models.rando.RandoCalAi;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.db.DeckSerialization;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Headless bot-vs-bot driver: two in-process {@link SwccgAiController}s play one game
 * with no Hall/HTTP/UI. Mirrors {@code SwccgGameMediator.maybeLetAiPlay} but without
 * {@code MAX_AI_CHAIN = 50}, which is the Hall path's hard stop when both seats are AI.
 *
 * <p>Librarian "Open 40 card - Beginner" decks from {@code sample_decks.sql} are embedded
 * so the spike does not need a DB.
 */
public final class HeadlessBotVsBotRunner {

    /** Same contents as Librarian deck "Open 40 card - Beginner Dark" in sample_decks.sql */
    public static final String OPEN40_BEGINNER_DARK =
            "1_164,3_82,1_173,200_77,1_168,203_27,202_10,10_40,200_107,8_126,200_112,7_246,1_241,209_47,209_47,"
                    + "200_121,1_249,9_137,9_137,7_262,7_262,201_36,6_160,211_19,7_282,208_50,2_146,210_39,216_9,"
                    + "209_50,106_13,106_13,201_41,1_304,1_304,1_304,1_304,7_309,211_24,215_23|";

    /** Same contents as Librarian deck "Open 40 card - Beginner Light" in sample_decks.sql */
    public static final String OPEN40_BEGINNER_LIGHT =
            "217_26,217_31,10_3,214_18,207_5,217_36,7_24,7_24,211_57,204_11,216_42,7_50,7_50,216_48,216_48,216_48,"
                    + "213_44,12_39,200_46,1_90,200_54,12_62,12_62,6_72,210_24,1_105,203_17,203_17,207_14,4_67,"
                    + "216_45,216_45,204_23,12_76,215_13,215_14,216_32,216_33,201_19,203_21|";

    public static final String DS_PLAYER = "~OzzelBot";
    public static final String LS_PLAYER = "~AckbarBot";

    public enum AiSkill {
        BEGINNER,
        ADVANCED,
        RANDO
    }

    public static final class Config {
        public AiSkill darkAi = AiSkill.BEGINNER;
        public AiSkill lightAi = AiSkill.BEGINNER;
        public String formatName = "open";
        public int maxDecisions = 25_000;
        public long maxMillis = 180_000L;
        public boolean verbose = false;
        public int progressEveryN = 250;
    }

    public static final class Result {
        public final String gameId;
        public final boolean finished;
        public final boolean cancelled;
        public final String winner;
        public final String winReasonSummary;
        public final int decisionCount;
        public final int invalidAnswerCount;
        public final int darkTurnNumber;
        public final int lightTurnNumber;
        public final Phase lastPhase;
        public final int darkLifeForce;
        public final int lightLifeForce;
        public final long elapsedMillis;
        public final String stopper;
        public final List<String> failureNotes;
        public final Map<String, Integer> decisionsByPlayer;

        Result(String gameId, boolean finished, boolean cancelled, String winner, String winReasonSummary,
               int decisionCount, int invalidAnswerCount, int darkTurnNumber, int lightTurnNumber,
               Phase lastPhase, int darkLifeForce, int lightLifeForce, long elapsedMillis,
               String stopper, List<String> failureNotes, Map<String, Integer> decisionsByPlayer) {
            this.gameId = gameId;
            this.finished = finished;
            this.cancelled = cancelled;
            this.winner = winner;
            this.winReasonSummary = winReasonSummary;
            this.decisionCount = decisionCount;
            this.invalidAnswerCount = invalidAnswerCount;
            this.darkTurnNumber = darkTurnNumber;
            this.lightTurnNumber = lightTurnNumber;
            this.lastPhase = lastPhase;
            this.darkLifeForce = darkLifeForce;
            this.lightLifeForce = lightLifeForce;
            this.elapsedMillis = elapsedMillis;
            this.stopper = stopper;
            this.failureNotes = failureNotes;
            this.decisionsByPlayer = decisionsByPlayer;
        }

        @Override
        public String toString() {
            return "HeadlessBotVsBotResult{"
                    + "finished=" + finished
                    + ", cancelled=" + cancelled
                    + ", winner=" + winner
                    + ", decisions=" + decisionCount
                    + ", invalid=" + invalidAnswerCount
                    + ", dsTurn=" + darkTurnNumber
                    + ", lsTurn=" + lightTurnNumber
                    + ", phase=" + lastPhase
                    + ", dsLF=" + darkLifeForce
                    + ", lsLF=" + lightLifeForce
                    + ", elapsedMs=" + elapsedMillis
                    + ", stopper=" + stopper
                    + ", byPlayer=" + decisionsByPlayer
                    + ", failures=" + failureNotes.size()
                    + '}';
        }
    }

    private HeadlessBotVsBotRunner() {
    }

    public static SwccgAiController createAi(AiSkill skill) {
        switch (skill == null ? AiSkill.BEGINNER : skill) {
            case ADVANCED:
                return new AdvancedAi();
            case RANDO:
                return new RandoCalAi();
            case BEGINNER:
            default:
                return new BeginnerAi();
        }
    }

    public static Result playOneGame(Config config) {
        Config cfg = config == null ? new Config() : config;
        String gameId = "headless-" + UUID.randomUUID();
        long started = System.currentTimeMillis();
        List<String> failures = new ArrayList<>();
        Map<String, Integer> byPlayer = new LinkedHashMap<>();
        byPlayer.put(DS_PLAYER, 0);
        byPlayer.put(LS_PLAYER, 0);

        SwccgCardBlueprintLibrary cardLibrary = new SwccgCardBlueprintLibrary();
        SwccgoFormatLibrary formatLibrary = new SwccgoFormatLibrary(cardLibrary);

        SwccgDeck darkDeck = DeckSerialization.buildDeckFromContents(
                "Open 40 card - Beginner Dark", OPEN40_BEGINNER_DARK, cardLibrary);
        SwccgDeck lightDeck = DeckSerialization.buildDeckFromContents(
                "Open 40 card - Beginner Light", OPEN40_BEGINNER_LIGHT, cardLibrary);

        if (darkDeck.getCards().isEmpty() || lightDeck.getCards().isEmpty()) {
            throw new IllegalStateException("Open 40 beginner decks failed to load (empty card list). "
                    + "dark=" + darkDeck.getCards().size() + " light=" + lightDeck.getCards().size());
        }

        Map<String, SwccgDeck> decks = new HashMap<>();
        decks.put(DS_PLAYER, darkDeck);
        decks.put(LS_PLAYER, lightDeck);

        Map<String, Integer> clocks = new HashMap<>();
        clocks.put(DS_PLAYER, 0);
        clocks.put(LS_PLAYER, 0);

        DefaultUserFeedback userFeedback = new DefaultUserFeedback();
        DefaultSwccgGame game = new DefaultSwccgGame(
                formatLibrary.getFormat(cfg.formatName),
                decks,
                userFeedback,
                cardLibrary,
                clocks,
                false);
        userFeedback.setGame(game);

        SwccgAiController darkAi = createAi(cfg.darkAi);
        SwccgAiController lightAi = createAi(cfg.lightAi);
        darkAi.setGame(game);
        lightAi.setGame(game);

        AiRegistry.register(gameId, DS_PLAYER, darkAi);
        AiRegistry.register(gameId, LS_PLAYER, lightAi);

        int decisionCount = 0;
        int invalidCount = 0;
        String stopper = null;

        try {
            game.startGame();

            while (!game.isFinished() && !game.isCancelled()) {
                if (decisionCount >= cfg.maxDecisions) {
                    stopper = "maxDecisions=" + cfg.maxDecisions;
                    break;
                }
                long elapsed = System.currentTimeMillis() - started;
                if (elapsed >= cfg.maxMillis) {
                    stopper = "maxMillis=" + cfg.maxMillis;
                    break;
                }

                Set<String> pending = userFeedback.getUsersPendingDecision();
                if (pending == null || pending.isEmpty()) {
                    // No decision queued and game not finished — engine stall or both autoskip.
                    game.carryOutPendingActionsUntilDecisionNeeded();
                    pending = userFeedback.getUsersPendingDecision();
                    if (pending == null || pending.isEmpty()) {
                        if (!game.isFinished() && !game.isCancelled()) {
                            stopper = "noPendingDecision";
                            failures.add("Engine idle with no pending decision and game not finished");
                        }
                        break;
                    }
                }

                // Snapshot to avoid concurrent modification while resolving.
                List<String> players = new ArrayList<>(pending);
                boolean progressed = false;
                for (String playerId : players) {
                    if (game.isFinished() || game.isCancelled()) {
                        break;
                    }
                    AwaitingDecision decision = userFeedback.getAwaitingDecision(playerId);
                    if (decision == null) {
                        continue;
                    }

                    SwccgAiController ai = AiRegistry.get(gameId, playerId);
                    if (ai == null) {
                        stopper = "missingAi:" + playerId;
                        failures.add("No AI registered for pending player " + playerId);
                        break;
                    }

                    ai.setGame(game);
                    String answer;
                    try {
                        answer = ai.decide(playerId, decision, game.getGameState());
                    } catch (RuntimeException ex) {
                        invalidCount++;
                        String note = "AI decide threw for " + playerId + ": " + ex.getClass().getSimpleName()
                                + ": " + ex.getMessage();
                        failures.add(note);
                        if (cfg.verbose) {
                            System.out.println(note);
                        }
                        // Leave decision pending; abort to avoid infinite loop on broken AI.
                        stopper = "aiDecideException";
                        break;
                    }

                    userFeedback.participantDecided(playerId);
                    try {
                        decision.decisionMade(answer);
                        decisionCount++;
                        byPlayer.put(playerId, byPlayer.getOrDefault(playerId, 0) + 1);
                        progressed = true;

                        if (cfg.verbose && (decisionCount % Math.max(1, cfg.progressEveryN) == 0)) {
                            GameState gs = game.getGameState();
                            System.out.println(String.format(Locale.ROOT,
                                    "[headless] decisions=%d phase=%s dsTurn=%d lsTurn=%d dsLF=%d lsLF=%d last=%s answer=%s",
                                    decisionCount,
                                    gs != null ? gs.getCurrentPhase() : null,
                                    gs != null ? gs.getPlayersLatestTurnNumber(DS_PLAYER) : -1,
                                    gs != null ? gs.getPlayersLatestTurnNumber(LS_PLAYER) : -1,
                                    gs != null ? gs.getPlayerLifeForce(DS_PLAYER) : -1,
                                    gs != null ? gs.getPlayerLifeForce(LS_PLAYER) : -1,
                                    playerId,
                                    truncate(answer, 80)));
                        }
                    } catch (DecisionResultInvalidException invalid) {
                        invalidCount++;
                        // Same recovery as SwccgGameMediator.maybeLetAiPlay: re-queue decision.
                        userFeedback.sendAwaitingDecision(playerId, decision);
                        String note = "Invalid AI answer for " + playerId
                                + " decisionId=" + decision.getAwaitingDecisionId()
                                + " type=" + decision.getDecisionType()
                                + " answer=" + truncate(answer, 60);
                        failures.add(note);
                        if (cfg.verbose) {
                            System.out.println(note);
                        }
                        // Avoid spinning forever on the same invalid answer.
                        if (invalidCount >= 50) {
                            stopper = "tooManyInvalidAnswers";
                            break;
                        }
                    }

                    game.carryOutPendingActionsUntilDecisionNeeded();
                }

                if (stopper != null) {
                    break;
                }
                if (!progressed && !game.isFinished() && !game.isCancelled()) {
                    // Pending set existed but nothing applied (e.g. decision vanished).
                    stopper = "noProgress";
                    failures.add("Loop iteration made no progress with pending=" + players);
                    break;
                }
            }

            if (stopper == null) {
                if (game.isFinished()) {
                    stopper = "finished";
                } else if (game.isCancelled()) {
                    stopper = "cancelled";
                }
            }
        } finally {
            AiRegistry.unregisterGame(gameId);
        }

        GameState gs = game.getGameState();
        Phase phase = gs != null ? gs.getCurrentPhase() : null;
        int dsTurn = gs != null ? gs.getPlayersLatestTurnNumber(DS_PLAYER) : -1;
        int lsTurn = gs != null ? gs.getPlayersLatestTurnNumber(LS_PLAYER) : -1;
        int dsLf = gs != null ? gs.getPlayerLifeForce(DS_PLAYER) : -1;
        int lsLf = gs != null ? gs.getPlayerLifeForce(LS_PLAYER) : -1;

        return new Result(
                gameId,
                game.isFinished(),
                game.isCancelled(),
                game.getWinner(),
                game.isFinished() ? ("winner=" + game.getWinner()) : ("stopper=" + stopper),
                decisionCount,
                invalidCount,
                dsTurn,
                lsTurn,
                phase,
                dsLf,
                lsLf,
                System.currentTimeMillis() - started,
                stopper,
                failures,
                byPlayer);
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "null";
        }
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "...";
    }

    /**
     * Optional CLI entry: {@code mvn -pl gemp-swccg-server -DskipTests exec:...} is not wired;
     * prefer the JUnit spike. This main exists for ad-hoc classpath runs.
     */
    public static void main(String[] args) {
        Config cfg = new Config();
        cfg.verbose = true;
        for (String arg : args) {
            if (arg.startsWith("--dark=")) {
                cfg.darkAi = AiSkill.valueOf(arg.substring("--dark=".length()).toUpperCase(Locale.ROOT));
            } else if (arg.startsWith("--light=")) {
                cfg.lightAi = AiSkill.valueOf(arg.substring("--light=".length()).toUpperCase(Locale.ROOT));
            } else if (arg.equals("--quiet")) {
                cfg.verbose = false;
            } else if (arg.startsWith("--maxDecisions=")) {
                cfg.maxDecisions = Integer.parseInt(arg.substring("--maxDecisions=".length()));
            } else if (arg.startsWith("--maxMillis=")) {
                cfg.maxMillis = Long.parseLong(arg.substring("--maxMillis=".length()));
            }
        }
        Result result = playOneGame(cfg);
        System.out.println(result);
        if (!result.failureNotes.isEmpty()) {
            System.out.println("Failures:");
            for (String f : result.failureNotes) {
                System.out.println("  - " + f);
            }
        }
        if (!result.finished) {
            System.exit(2);
        }
    }
}
