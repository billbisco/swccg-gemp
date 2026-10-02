package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.ai.features.DecklistMultisets;
import com.gempukku.swccgo.ai.features.InformationSetGameStateListener;
import com.gempukku.swccgo.ai.features.InformationSetTracker;
import com.gempukku.swccgo.ai.models.AdvancedAi;
import com.gempukku.swccgo.ai.models.BeginnerAi;
import com.gempukku.swccgo.ai.models.ConfigurableHeuristicAi;
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
 * so the spike does not need a DB. Optional WC96 (P-ANH 1996) packs and
 * {@link HeadlessReplayWriter} xml.gz export are supported via {@link Config}.
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


    /** Librarian "P-ANH 1996 World Champion (Dark)" from sample_decks.sql */
    public static final String WC96_ANH_DARK =
            "2_85,1_172,1_177,2_100,2_107,2_107,1_164,1_164,1_165,1_167,1_173,1_173,1_174,1_168,1_168,1_168,"
                    + "1_179,1_179,2_98,2_98,1_227,1_227,1_234,1_234,1_234,1_234,102_8,1_248,1_267,1_267,1_267,"
                    + "1_267,1_267,1_267,1_267,2_143,1_287,2_146,1_288,2_147,1_289,1_296,1_299,1_299,1_300,1_300,"
                    + "2_151,2_151,2_152,1_301,1_306,2_155,2_155,2_155,2_155,2_155,2_155,2_155,1_324,1_324|";

    /** Librarian "P-ANH 1996 World Champion (Light)" from sample_decks.sql */
    public static final String WC96_ANH_LIGHT =
            "2_3,1_9,1_15,1_15,1_5,2_14,2_14,1_3,1_11,1_11,1_13,1_17,1_17,1_19,1_19,1_21,1_21,2_23,1_38,1_54,"
                    + "1_55,1_62,2_40,2_40,1_71,1_71,1_71,1_71,2_48,1_82,2_49,2_50,2_50,1_87,1_87,1_87,1_97,1_109,"
                    + "1_109,1_109,1_109,1_109,1_116,2_63,1_132,1_133,1_134,2_68,1_139,1_140,1_140,1_140,1_140,"
                    + "103_1,1_143,2_72,103_2,2_73,2_73,1_157|";

    public static final String DECK_OPEN40 = "open40";
    public static final String DECK_WC96 = "wc96";

    public static final String DS_PLAYER = "~OzzelBot";
    public static final String LS_PLAYER = "~AckbarBot";

    public enum AiSkill {
        BEGINNER,
        ADVANCED,
        RANDO,
        /** Gym-cli linear.v1 policy. Zeros pack when no weights path is set. */
        LINEAR
    }

    public static final class Config {
        public AiSkill darkAi = AiSkill.BEGINNER;
        public AiSkill lightAi = AiSkill.BEGINNER;
        /**
         * Format code from swccgFormats.json. Use {@code premiere_anh} for WC96 packs;
         * {@code open} for Open 40 beginner.
         */
        public String formatName = "open";
        /**
         * Built-in pack: {@link #DECK_OPEN40} or {@link #DECK_WC96}. Ignored when
         * {@link #darkDeckContents} / {@link #lightDeckContents} are set.
         */
        public String deckPack = DECK_OPEN40;
        public String darkDeckName = null;
        public String lightDeckName = null;
        /** Optional raw deck contents ({@code blueprintId,...|outside}); overrides pack. */
        public String darkDeckContents = null;
        public String lightDeckContents = null;
        public int maxDecisions = 25_000;
        public long maxMillis = 180_000L;
        public boolean verbose = false;
        public int progressEveryN = 250;
        /** Optional batch index (1-based) written into decision traces. */
        public Integer gameIndex = null;
        /**
         * When non-null, each AI decision is appended as one JSONL line.
         * Caller owns open/close (batch shares one writer across games).
         */
        public HeadlessDecisionTraceWriter traceWriter = null;
        /**
         * Trace richness. Default COMPACT. FEATURES embeds InformationSetV1 under {@code state}.
         * Overridden by the writer's level when the writer was opened from system properties.
         */
        public HeadlessDecisionTraceWriter.TraceLevel traceLevel = HeadlessDecisionTraceWriter.TraceLevel.COMPACT;
        /** Match-scoped Dark-seat tracker (own prior + destiny/reveals). Created if null when FEATURES. */
        public InformationSetTracker darkTracker = null;
        /** Match-scoped Light-seat tracker. Created if null when FEATURES. */
        public InformationSetTracker lightTracker = null;
        /**
         * When non-null, attach {@link HeadlessReplayWriter} and write xml.gz under this dir
         * after the game (no Hall / no GameHistoryService).
         */
        public Path replayDir = null;
        /** Convenience: when true and {@link #replayDir} is null, use {@code target/headless-replays}. */
        public boolean recordReplay = false;
        /**
         * Optional weights file for Dark. {@code heuristic.v1} when {@link #darkAi} is not
         * {@link AiSkill#LINEAR}; {@code linear.v1} when it is. Null LINEAR uses a zeros pack.
         */
        public Path darkWeightsPath = null;
        /** Optional weights file for Light (same semantics as darkWeightsPath). */
        public Path lightWeightsPath = null;
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
        /** PlayerId -> recording id when replay was written; empty otherwise. */
        public final Map<String, String> recordingIds;
        /** PlayerId -> xml.gz path when replay was written; empty otherwise. */
        public final Map<String, Path> replayFiles;
        public final Path replayMetaPath;
        public final String darkDeckName;
        public final String lightDeckName;
        public final String formatName;

        Result(String gameId, boolean finished, boolean cancelled, String winner, String winReasonSummary,
               int decisionCount, int invalidAnswerCount, int darkTurnNumber, int lightTurnNumber,
               Phase lastPhase, int darkLifeForce, int lightLifeForce, long elapsedMillis,
               String stopper, List<String> failureNotes, Map<String, Integer> decisionsByPlayer,
               Map<String, String> recordingIds, Map<String, Path> replayFiles, Path replayMetaPath,
               String darkDeckName, String lightDeckName, String formatName) {
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
            this.recordingIds = recordingIds != null ? recordingIds : new LinkedHashMap<>();
            this.replayFiles = replayFiles != null ? replayFiles : new LinkedHashMap<>();
            this.replayMetaPath = replayMetaPath;
            this.darkDeckName = darkDeckName;
            this.lightDeckName = lightDeckName;
            this.formatName = formatName;
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
                    + ", format=" + formatName
                    + ", darkDeck=" + darkDeckName
                    + ", lightDeck=" + lightDeckName
                    + ", replays=" + replayFiles.size()
                    + ", failures=" + failureNotes.size()
                    + '}';
        }
    }

    private HeadlessBotVsBotRunner() {
    }

    public static SwccgAiController createAi(AiSkill skill) {
        return createAi(skill, null);
    }

    /**
     * {@link AiSkill#LINEAR} loads {@code linear.v1} from {@code weightsPath}, or a zeros pack
     * when the path is null. Any other skill with a non-null path loads heuristic.v1
     * {@link ConfigurableHeuristicAi}. {@link AiSkill#BEGINNER} with a null path stays BeginnerAi.
     */
    public static SwccgAiController createAi(AiSkill skill, Path weightsPath) {
        AiSkill resolved = skill == null ? AiSkill.BEGINNER : skill;
        if (resolved == AiSkill.LINEAR) {
            try {
                return weightsPath != null ? com.gempukku.swccgo.ai.models.LinearPolicyAi.load(weightsPath)
                        : com.gempukku.swccgo.ai.models.LinearPolicyAi.zeros();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load linear.v1 weights from " + weightsPath, e);
            }
        }
        if (weightsPath != null) {
            try {
                return new ConfigurableHeuristicAi(weightsPath);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load heuristic weights from " + weightsPath, e);
            }
        }
        switch (resolved) {
            case ADVANCED:
                return new AdvancedAi();
            case RANDO:
                return new RandoCalAi();
            case LINEAR:
                return com.gempukku.swccgo.ai.models.LinearPolicyAi.zeros();
            case BEGINNER:
            default:
                return new BeginnerAi();
        }
    }

    public static Result playOneGame(Config config) {
        return playOneGame(config, null, null);
    }

    /**
     * Same as {@link #playOneGame(Config)} but optionally reuses card/format libraries
     * across a batch (pass non-null libraries from {@link HeadlessBotVsBotBatch}).
     */
    public static Result playOneGame(Config config,
                                     SwccgCardBlueprintLibrary sharedCardLibrary,
                                     SwccgoFormatLibrary sharedFormatLibrary) {
        Config cfg = config == null ? new Config() : config;
        String gameId = "headless-" + UUID.randomUUID();
        long started = System.currentTimeMillis();
        List<String> failures = new ArrayList<>();
        Map<String, Integer> byPlayer = new LinkedHashMap<>();
        byPlayer.put(DS_PLAYER, 0);
        byPlayer.put(LS_PLAYER, 0);

        SwccgCardBlueprintLibrary cardLibrary = sharedCardLibrary != null
                ? sharedCardLibrary : new SwccgCardBlueprintLibrary();
        SwccgoFormatLibrary formatLibrary = sharedFormatLibrary != null
                ? sharedFormatLibrary : new SwccgoFormatLibrary(cardLibrary);

        DeckPair deckPair = resolveDecks(cfg, cardLibrary);
        SwccgDeck darkDeck = deckPair.dark;
        SwccgDeck lightDeck = deckPair.light;

        if (darkDeck.getCards().isEmpty() || lightDeck.getCards().isEmpty()) {
            throw new IllegalStateException("Decks failed to load (empty card list). pack=" + cfg.deckPack
                    + " dark=" + darkDeck.getCards().size() + " light=" + lightDeck.getCards().size());
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

        HeadlessReplayWriter replayWriter = null;
        Path replayDir = cfg.replayDir;
        if (replayDir == null && cfg.recordReplay) {
            replayDir = java.nio.file.Paths.get("target", "headless-replays").toAbsolutePath().normalize();
        }
        if (replayDir != null) {
            Path gameReplayDir = replayDir;
            if (cfg.gameIndex != null) {
                gameReplayDir = replayDir.resolve("game-" + String.format(Locale.ROOT, "%04d", cfg.gameIndex));
            }
            replayWriter = new HeadlessReplayWriter(gameReplayDir);
            replayWriter.attach(game, Arrays.asList(DS_PLAYER, LS_PLAYER));
        }

        SwccgAiController darkAi = createAi(cfg.darkAi, cfg.darkWeightsPath);
        SwccgAiController lightAi = createAi(cfg.lightAi, cfg.lightWeightsPath);
        darkAi.setGame(game);
        lightAi.setGame(game);

        AiRegistry.register(gameId, DS_PLAYER, darkAi);
        AiRegistry.register(gameId, LS_PLAYER, lightAi);

        HeadlessDecisionTraceWriter.TraceLevel effectiveLevel = resolveTraceLevel(cfg);
        if (effectiveLevel == HeadlessDecisionTraceWriter.TraceLevel.FEATURES) {
            if (cfg.darkTracker == null) {
                cfg.darkTracker = new InformationSetTracker(DS_PLAYER);
            }
            if (cfg.lightTracker == null) {
                cfg.lightTracker = new InformationSetTracker(LS_PLAYER);
            }
            cfg.darkTracker.setOwnDeckPrior(DecklistMultisets.fromBlueprintIds(darkDeck.getCards()));
            cfg.lightTracker.setOwnDeckPrior(DecklistMultisets.fromBlueprintIds(lightDeck.getCards()));
            cfg.darkTracker.setOwnBlueprintDestinyHints(
                    DecklistMultisets.destinyHints(cfg.darkTracker.getOwnDeckPriorView(), cardLibrary));
            cfg.lightTracker.setOwnBlueprintDestinyHints(
                    DecklistMultisets.destinyHints(cfg.lightTracker.getOwnDeckPriorView(), cardLibrary));
            // Destiny + interrupt hooks into each seat's tracker (never seeds opponent decklist).
            game.addGameStateListener(DS_PLAYER, new InformationSetGameStateListener(cfg.darkTracker));
            game.addGameStateListener(LS_PLAYER, new InformationSetGameStateListener(cfg.lightTracker));
        }

        if (cfg.traceWriter != null) {
            try {
                cfg.traceWriter.writeGameHeader(
                        gameId,
                        cfg.gameIndex,
                        cfg.formatName,
                        deckPair.darkName,
                        deckPair.lightName,
                        cfg.darkAi != null ? cfg.darkAi.name() : "",
                        cfg.lightAi != null ? cfg.lightAi.name() : "");
            } catch (Exception ex) {
                System.err.println("[headless] trace header failed: " + ex.getClass().getSimpleName()
                        + ": " + ex.getMessage());
            }
        }

        int decisionCount = 0;
        int invalidCount = 0;
        String stopper = null;
        Map<String, String> recordingIds = new LinkedHashMap<>();
        Map<String, Path> replayFiles = new LinkedHashMap<>();
        Path replayMetaPath = null;

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

                        recordTrace(cfg, gameId, decisionCount, playerId, decision,
                                game.getGameState(), answer, true, null);

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
                        recordTrace(cfg, gameId, decisionCount + 1, playerId, decision,
                                game.getGameState(), answer, false, note);
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
            if (replayWriter != null) {
                try {
                    String winner = game.getWinner();
                    String loser = null;
                    if (winner != null) {
                        loser = DS_PLAYER.equals(winner) ? LS_PLAYER : DS_PLAYER;
                    }
                    Map<String, String> extra = new LinkedHashMap<>();
                    extra.put("gameId", gameId);
                    extra.put("format", cfg.formatName);
                    extra.put("darkDeck", deckPair.darkName);
                    extra.put("lightDeck", deckPair.lightName);
                    extra.put("darkAi", cfg.darkAi != null ? cfg.darkAi.name() : "");
                    extra.put("lightAi", cfg.lightAi != null ? cfg.lightAi.name() : "");
                    extra.put("stopper", stopper != null ? stopper : "");
                    if (cfg.gameIndex != null) {
                        extra.put("gameIndex", Integer.toString(cfg.gameIndex));
                    }
                    HeadlessReplayWriter.ReplayArtifacts arts = replayWriter.finish(
                            winner,
                            game.isFinished() ? ("winner=" + winner) : ("stopper=" + stopper),
                            loser,
                            loser != null ? ("lost to " + winner) : null,
                            extra);
                    recordingIds.putAll(arts.recordingIds);
                    replayFiles.putAll(arts.replayFiles);
                    replayMetaPath = arts.metaPath;
                    if (cfg.verbose) {
                        System.out.println("[headless] wrote replays under " + arts.replayDir
                                + " meta=" + arts.metaPath);
                        for (Map.Entry<String, Path> e : arts.replayFiles.entrySet()) {
                            System.out.println("[headless]   " + e.getKey() + " -> " + e.getValue()
                                    + " open: " + arts.replayUrlHint(e.getKey()));
                        }
                    }
                } catch (Exception ex) {
                    String note = "Replay write failed: " + ex.getClass().getSimpleName() + ": " + ex.getMessage();
                    failures.add(note);
                    System.err.println("[headless] " + note);
                }
            }
            if (cfg.traceWriter != null) {
                try {
                    GameState outcomeGs = game.getGameState();
                    int outcomeDarkLf = outcomeGs != null ? outcomeGs.getPlayerLifeForce(DS_PLAYER) : -1;
                    int outcomeLightLf = outcomeGs != null ? outcomeGs.getPlayerLifeForce(LS_PLAYER) : -1;
                    cfg.traceWriter.writeGameOutcome(
                            gameId,
                            cfg.gameIndex,
                            cfg.formatName,
                            game.isFinished(),
                            game.isCancelled(),
                            game.getWinner(),
                            stopper,
                            decisionCount,
                            outcomeDarkLf,
                            outcomeLightLf);
                } catch (Exception ex) {
                    System.err.println("[headless] trace outcome failed: " + ex.getClass().getSimpleName()
                            + ": " + ex.getMessage());
                }
            }
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
                byPlayer,
                recordingIds,
                replayFiles,
                replayMetaPath,
                deckPair.darkName,
                deckPair.lightName,
                cfg.formatName);
    }

    private static final class DeckPair {
        final SwccgDeck dark;
        final SwccgDeck light;
        final String darkName;
        final String lightName;

        DeckPair(SwccgDeck dark, SwccgDeck light, String darkName, String lightName) {
            this.dark = dark;
            this.light = light;
            this.darkName = darkName;
            this.lightName = lightName;
        }
    }

    static DeckPair resolveDecks(Config cfg, SwccgCardBlueprintLibrary cardLibrary) {
        String pack = cfg.deckPack != null ? cfg.deckPack.trim().toLowerCase(Locale.ROOT) : DECK_OPEN40;
        String darkContents;
        String lightContents;
        String darkName;
        String lightName;
        if (cfg.darkDeckContents != null && cfg.lightDeckContents != null) {
            darkContents = cfg.darkDeckContents;
            lightContents = cfg.lightDeckContents;
            darkName = cfg.darkDeckName != null ? cfg.darkDeckName : "Custom Dark";
            lightName = cfg.lightDeckName != null ? cfg.lightDeckName : "Custom Light";
        } else if (DECK_WC96.equals(pack) || "wc96-anh".equals(pack) || "p-anh-1996".equals(pack)) {
            darkContents = WC96_ANH_DARK;
            lightContents = WC96_ANH_LIGHT;
            darkName = cfg.darkDeckName != null ? cfg.darkDeckName : "P-ANH 1996 World Champion (Dark)";
            lightName = cfg.lightDeckName != null ? cfg.lightDeckName : "P-ANH 1996 World Champion (Light)";
        } else {
            darkContents = OPEN40_BEGINNER_DARK;
            lightContents = OPEN40_BEGINNER_LIGHT;
            darkName = cfg.darkDeckName != null ? cfg.darkDeckName : "Open 40 card - Beginner Dark";
            lightName = cfg.lightDeckName != null ? cfg.lightDeckName : "Open 40 card - Beginner Light";
        }
        SwccgDeck darkDeck = DeckSerialization.buildDeckFromContents(darkName, darkContents, cardLibrary);
        SwccgDeck lightDeck = DeckSerialization.buildDeckFromContents(lightName, lightContents, cardLibrary);
        return new DeckPair(darkDeck, lightDeck, darkName, lightName);
    }

    private static void recordTrace(Config cfg, String gameId, int decisionIndex, String playerId,
                                    AwaitingDecision decision, GameState gs, String answer,
                                    boolean accepted, String invalidReason) {
        if (cfg == null || cfg.traceWriter == null) {
            return;
        }
        String skill;
        if (DS_PLAYER.equals(playerId)) {
            skill = cfg.darkAi != null ? cfg.darkAi.name() : null;
        } else if (LS_PLAYER.equals(playerId)) {
            skill = cfg.lightAi != null ? cfg.lightAi.name() : null;
        } else {
            skill = null;
        }
        HeadlessDecisionTraceWriter.TraceLevel level = resolveTraceLevel(cfg);
        InformationSetTracker tracker = null;
        if (level == HeadlessDecisionTraceWriter.TraceLevel.FEATURES) {
            if (DS_PLAYER.equals(playerId)) {
                tracker = cfg.darkTracker;
            } else if (LS_PLAYER.equals(playerId)) {
                tracker = cfg.lightTracker;
            }
        }
        try {
            cfg.traceWriter.record(HeadlessDecisionTraceWriter.fromDecision(
                    gameId, cfg.gameIndex, decisionIndex, playerId, skill, cfg.formatName,
                    decision, gs, answer, accepted, invalidReason, level, tracker));
        } catch (Exception ex) {
            // Tracing must never abort a game; surface once via stderr.
            System.err.println("[headless] trace write failed: " + ex.getClass().getSimpleName()
                    + ": " + ex.getMessage());
        }
    }

    private static HeadlessDecisionTraceWriter.TraceLevel resolveTraceLevel(Config cfg) {
        if (cfg != null && cfg.traceWriter != null
                && cfg.traceWriter.getTraceLevel() == HeadlessDecisionTraceWriter.TraceLevel.FEATURES) {
            return HeadlessDecisionTraceWriter.TraceLevel.FEATURES;
        }
        if (cfg != null && cfg.traceLevel == HeadlessDecisionTraceWriter.TraceLevel.FEATURES) {
            return HeadlessDecisionTraceWriter.TraceLevel.FEATURES;
        }
        return HeadlessDecisionTraceWriter.levelFromSystemProperties();
    }

    private static Map<String, Integer> multiset(List<String> cards) {
        return DecklistMultisets.fromBlueprintIds(cards);
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
            } else if (arg.startsWith("--decks=")) {
                cfg.deckPack = arg.substring("--decks=".length());
                if (DECK_WC96.equalsIgnoreCase(cfg.deckPack) || "wc96-anh".equalsIgnoreCase(cfg.deckPack)) {
                    cfg.formatName = "premiere_anh";
                }
            } else if (arg.startsWith("--format=")) {
                cfg.formatName = arg.substring("--format=".length());
            } else if (arg.equals("--replay") || arg.equals("--replay=true")) {
                cfg.recordReplay = true;
            } else if (arg.startsWith("--replayDir=")) {
                cfg.recordReplay = true;
                cfg.replayDir = java.nio.file.Paths.get(arg.substring("--replayDir=".length()));
            }
        }
        Result result = playOneGame(cfg);
        System.out.println(result);
        if (!result.replayFiles.isEmpty()) {
            System.out.println("Replays:");
            for (Map.Entry<String, Path> e : result.replayFiles.entrySet()) {
                System.out.println("  " + e.getKey() + " -> " + e.getValue());
            }
            if (result.replayMetaPath != null) {
                System.out.println("  meta -> " + result.replayMetaPath);
            }
        }
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
