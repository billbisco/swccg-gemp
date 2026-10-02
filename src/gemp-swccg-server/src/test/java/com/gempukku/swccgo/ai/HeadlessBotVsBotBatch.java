package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Batch self-play on top of {@link HeadlessBotVsBotRunner#playOneGame}: runs N games
 * and writes a CSV of results for bot measurement.
 *
 * <p>Does not duplicate the decision loop — every game goes through the shared runner.
 */
public final class HeadlessBotVsBotBatch {

    public static final String CSV_HEADER =
            "gameIndex,darkAi,lightAi,winner,darkDecisions,lightDecisions,darkTurns,lightTurns,elapsedMs,format,darkDeck,lightDeck,error";

    public static final class BatchConfig {
        public int games = 5;
        public HeadlessBotVsBotRunner.AiSkill darkAi = HeadlessBotVsBotRunner.AiSkill.BEGINNER;
        public HeadlessBotVsBotRunner.AiSkill lightAi = HeadlessBotVsBotRunner.AiSkill.BEGINNER;
        public String formatName = "open";
        public int maxDecisions = 25_000;
        public long maxMillis = 180_000L;
        public boolean verbose = false;
        public int progressEveryN = 250;
        /** Destination CSV path; parent dirs are created. Null = stdout only (no file). */
        public Path outputCsv = Paths.get("target", "headless-bot-vs-bot-batch.csv");
        /** Reuse card/format libraries across games (faster for N&gt;1). Default true. */
        public boolean reuseLibraries = true;
        /**
         * When true, write per-decision JSONL traces via {@link HeadlessDecisionTraceWriter}.
         * Default false so CSV-only batches stay unchanged.
         */
        public boolean writeTraces = false;
        /** JSONL path when {@link #writeTraces} is true. Default under target/. */
        public Path tracesPath = Paths.get(HeadlessDecisionTraceWriter.DEFAULT_PATH);
        /**
         * COMPACT or FEATURES. Default follows {@code -Dheadless.traceLevel} when unset here.
         */
        public HeadlessDecisionTraceWriter.TraceLevel traceLevel = null;
        /**
         * Deck pack: {@link HeadlessBotVsBotRunner#DECK_OPEN40} or
         * {@link HeadlessBotVsBotRunner#DECK_WC96}.
         */
        public String deckPack = HeadlessBotVsBotRunner.DECK_OPEN40;
        /**
         * When non-null, write GEMP xml.gz replays under this directory (one subdir per game).
         */
        public Path replayDir = null;
        public boolean recordReplay = false;
        /** Optional Dark heuristic.v1 weights.json (overrides builtin darkAi skill). */
        public Path darkWeightsPath = null;
        /** Optional Light heuristic.v1 weights.json (overrides builtin lightAi skill). */
        public Path lightWeightsPath = null;
    }

    public static final class GameRow {
        public final int gameIndex;
        public final HeadlessBotVsBotRunner.AiSkill darkAi;
        public final HeadlessBotVsBotRunner.AiSkill lightAi;
        public final String winner;
        public final int darkDecisions;
        public final int lightDecisions;
        public final int darkTurns;
        public final int lightTurns;
        public final long elapsedMs;
        public final String error;
        public final HeadlessBotVsBotRunner.Result result;

        GameRow(int gameIndex, HeadlessBotVsBotRunner.AiSkill darkAi, HeadlessBotVsBotRunner.AiSkill lightAi,
                String winner, int darkDecisions, int lightDecisions, int darkTurns, int lightTurns,
                long elapsedMs, String error, HeadlessBotVsBotRunner.Result result) {
            this.gameIndex = gameIndex;
            this.darkAi = darkAi;
            this.lightAi = lightAi;
            this.winner = winner;
            this.darkDecisions = darkDecisions;
            this.lightDecisions = lightDecisions;
            this.darkTurns = darkTurns;
            this.lightTurns = lightTurns;
            this.elapsedMs = elapsedMs;
            this.error = error;
            this.result = result;
        }

        public String toCsvLine() {
            String format = result != null && result.formatName != null ? result.formatName : "";
            String darkDeck = result != null && result.darkDeckName != null ? result.darkDeckName : "";
            String lightDeck = result != null && result.lightDeckName != null ? result.lightDeckName : "";
            return String.join(",",
                    Integer.toString(gameIndex),
                    csv(darkAi != null ? darkAi.name() : ""),
                    csv(lightAi != null ? lightAi.name() : ""),
                    csv(winner != null ? winner : ""),
                    Integer.toString(darkDecisions),
                    Integer.toString(lightDecisions),
                    Integer.toString(darkTurns),
                    Integer.toString(lightTurns),
                    Long.toString(elapsedMs),
                    csv(format),
                    csv(darkDeck),
                    csv(lightDeck),
                    csv(error != null ? error : ""));
        }
    }

    public static final class BatchResult {
        public final List<GameRow> rows;
        public final Path csvPath;
        public final Path tracesPath;
        public final long traceLines;
        public final int finishedCount;
        public final int errorCount;
        public final long totalElapsedMs;
        public final Path replayDir;
        public final int replayGames;

        BatchResult(List<GameRow> rows, Path csvPath, Path tracesPath, long traceLines,
                    int finishedCount, int errorCount, long totalElapsedMs,
                    Path replayDir, int replayGames) {
            this.rows = rows;
            this.csvPath = csvPath;
            this.tracesPath = tracesPath;
            this.traceLines = traceLines;
            this.finishedCount = finishedCount;
            this.errorCount = errorCount;
            this.totalElapsedMs = totalElapsedMs;
            this.replayDir = replayDir;
            this.replayGames = replayGames;
        }

        @Override
        public String toString() {
            return "BatchResult{games=" + rows.size()
                    + ", finished=" + finishedCount
                    + ", errors=" + errorCount
                    + ", totalElapsedMs=" + totalElapsedMs
                    + ", csv=" + csvPath
                    + ", traces=" + tracesPath
                    + ", traceLines=" + traceLines
                    + ", replayDir=" + replayDir
                    + ", replayGames=" + replayGames
                    + '}';
        }
    }

    private HeadlessBotVsBotBatch() {
    }

    public static BatchResult runBatch(BatchConfig config) throws IOException {
        BatchConfig cfg = config == null ? new BatchConfig() : config;
        if (cfg.games < 1) {
            throw new IllegalArgumentException("games must be >= 1, got " + cfg.games);
        }

        // WC96 packs are Premiere - A New Hope; never silently run them as Open.
        if (cfg.deckPack != null
                && (HeadlessBotVsBotRunner.DECK_WC96.equalsIgnoreCase(cfg.deckPack)
                    || "wc96-anh".equalsIgnoreCase(cfg.deckPack)
                    || "p-anh-1996".equalsIgnoreCase(cfg.deckPack))
                && (cfg.formatName == null || cfg.formatName.isEmpty() || "open".equals(cfg.formatName))) {
            cfg.formatName = "premiere_anh";
        }

        SwccgCardBlueprintLibrary sharedCards = null;
        SwccgoFormatLibrary sharedFormats = null;
        if (cfg.reuseLibraries) {
            sharedCards = new SwccgCardBlueprintLibrary();
            sharedFormats = new SwccgoFormatLibrary(sharedCards);
        }

        List<GameRow> rows = new ArrayList<>(cfg.games);
        int finished = 0;
        int errors = 0;
        long batchStarted = System.currentTimeMillis();

        HeadlessDecisionTraceWriter traceWriter = null;
        Path tracesPath = null;
        long traceLines = 0;
        if (cfg.writeTraces) {
            tracesPath = (cfg.tracesPath != null ? cfg.tracesPath
                    : Paths.get(HeadlessDecisionTraceWriter.DEFAULT_PATH)).toAbsolutePath().normalize();
            HeadlessDecisionTraceWriter.TraceLevel level = cfg.traceLevel != null
                    ? cfg.traceLevel
                    : HeadlessDecisionTraceWriter.levelFromSystemProperties();
            traceWriter = new HeadlessDecisionTraceWriter(tracesPath, level);
            System.out.println("[batch] writing decision traces: " + tracesPath + " level=" + level);
        }

        try {
            for (int i = 1; i <= cfg.games; i++) {
                HeadlessBotVsBotRunner.Config gameCfg = new HeadlessBotVsBotRunner.Config();
                gameCfg.darkAi = cfg.darkAi;
                gameCfg.lightAi = cfg.lightAi;
                gameCfg.formatName = cfg.formatName;
                gameCfg.deckPack = cfg.deckPack;
                gameCfg.maxDecisions = cfg.maxDecisions;
                gameCfg.maxMillis = cfg.maxMillis;
                gameCfg.verbose = cfg.verbose;
                gameCfg.progressEveryN = cfg.progressEveryN;
                gameCfg.gameIndex = i;
                gameCfg.traceWriter = traceWriter;
                if (traceWriter != null) {
                    gameCfg.traceLevel = traceWriter.getTraceLevel();
                } else if (cfg.traceLevel != null) {
                    gameCfg.traceLevel = cfg.traceLevel;
                }
                gameCfg.darkWeightsPath = cfg.darkWeightsPath;
                gameCfg.lightWeightsPath = cfg.lightWeightsPath;
                if (cfg.recordReplay || cfg.replayDir != null) {
                    gameCfg.recordReplay = true;
                    gameCfg.replayDir = cfg.replayDir != null
                            ? cfg.replayDir
                            : Paths.get("target", "headless-replays").toAbsolutePath().normalize();
                }

                GameRow row;
                try {
                    HeadlessBotVsBotRunner.Result result = HeadlessBotVsBotRunner.playOneGame(
                            gameCfg, sharedCards, sharedFormats);
                    row = toRow(i, cfg.darkAi, cfg.lightAi, result, null);
                    if (result.finished) {
                        finished++;
                    } else {
                        errors++;
                    }
                } catch (RuntimeException ex) {
                    errors++;
                    String msg = ex.getClass().getSimpleName() + ": " + Objects.toString(ex.getMessage(), "");
                    row = new GameRow(i, cfg.darkAi, cfg.lightAi, "", 0, 0, -1, -1, 0, truncate(msg, 200), null);
                    if (cfg.verbose) {
                        System.out.println("[batch] game " + i + " threw: " + msg);
                    }
                }

                rows.add(row);
                System.out.println(String.format(Locale.ROOT,
                        "[batch] game %d/%d finished=%s winner=%s darkDec=%d lightDec=%d elapsedMs=%d error=%s",
                        i, cfg.games,
                        row.result != null && row.result.finished,
                        row.winner,
                        row.darkDecisions,
                        row.lightDecisions,
                        row.elapsedMs,
                        row.error == null || row.error.isEmpty() ? "-" : truncate(row.error, 80)));
            }
        } finally {
            if (traceWriter != null) {
                traceLines = traceWriter.getLinesWritten();
                traceWriter.close();
                System.out.println("[batch] wrote traces: " + tracesPath + " lines=" + traceLines);
            }
        }

        Path csvPath = null;
        if (cfg.outputCsv != null) {
            csvPath = cfg.outputCsv.toAbsolutePath().normalize();
            writeCsv(csvPath, rows);
            System.out.println("[batch] wrote CSV: " + csvPath);
        }

        int replayGames = 0;
        Path replayDirOut = null;
        if (cfg.recordReplay || cfg.replayDir != null) {
            replayDirOut = cfg.replayDir != null
                    ? cfg.replayDir.toAbsolutePath().normalize()
                    : Paths.get("target", "headless-replays").toAbsolutePath().normalize();
            for (GameRow row : rows) {
                if (row.result != null && row.result.replayFiles != null && !row.result.replayFiles.isEmpty()) {
                    replayGames++;
                }
            }
            System.out.println("[batch] replays under " + replayDirOut + " gamesWithReplay=" + replayGames);
        }

        return new BatchResult(rows, csvPath, tracesPath, traceLines, finished, errors,
                System.currentTimeMillis() - batchStarted, replayDirOut, replayGames);
    }

    public static GameRow toRow(int gameIndex, HeadlessBotVsBotRunner.AiSkill darkAi,
                                HeadlessBotVsBotRunner.AiSkill lightAi,
                                HeadlessBotVsBotRunner.Result result, String thrownError) {
        int darkDec = result.decisionsByPlayer != null
                ? result.decisionsByPlayer.getOrDefault(HeadlessBotVsBotRunner.DS_PLAYER, 0) : 0;
        int lightDec = result.decisionsByPlayer != null
                ? result.decisionsByPlayer.getOrDefault(HeadlessBotVsBotRunner.LS_PLAYER, 0) : 0;

        String error = thrownError;
        if (error == null || error.isEmpty()) {
            if (!result.finished) {
                StringBuilder sb = new StringBuilder();
                sb.append(result.stopper != null ? result.stopper : "unfinished");
                if (result.invalidAnswerCount > 0) {
                    sb.append("; invalid=").append(result.invalidAnswerCount);
                }
                if (result.failureNotes != null && !result.failureNotes.isEmpty()) {
                    sb.append("; ").append(result.failureNotes.get(0));
                }
                error = truncate(sb.toString(), 200);
            } else {
                error = "";
            }
        }

        return new GameRow(
                gameIndex,
                darkAi,
                lightAi,
                result.winner != null ? result.winner : "",
                darkDec,
                lightDec,
                result.darkTurnNumber,
                result.lightTurnNumber,
                result.elapsedMillis,
                error,
                result);
    }

    public static void writeCsv(Path path, List<GameRow> rows) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write(CSV_HEADER);
            w.newLine();
            for (GameRow row : rows) {
                w.write(row.toCsvLine());
                w.newLine();
            }
        }
    }

    /** RFC-ish CSV cell: quote if needed; double internal quotes. */
    static String csv(String raw) {
        if (raw == null) {
            return "";
        }
        boolean needsQuote = raw.indexOf(',') >= 0 || raw.indexOf('"') >= 0
                || raw.indexOf('\n') >= 0 || raw.indexOf('\r') >= 0;
        if (!needsQuote) {
            return raw;
        }
        return '"' + raw.replace("\"", "\"\"") + '"';
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "...";
    }

    public static HeadlessBotVsBotRunner.AiSkill parseAi(String name) {
        return HeadlessBotVsBotRunner.AiSkill.valueOf(name.trim().toUpperCase(Locale.ROOT));
    }

    /**
     * CLI: --games=N --dark=BEGINNER --light=ADVANCED --csv=path [--verbose]
     * [--maxDecisions=N] [--maxMillis=N] [--format=premiere_anh|open]
     */
    public static void main(String[] args) throws IOException {
        BatchConfig cfg = new BatchConfig();
        for (String arg : args) {
            if (arg.startsWith("--games=")) {
                cfg.games = Integer.parseInt(arg.substring("--games=".length()));
            } else if (arg.startsWith("--dark=")) {
                cfg.darkAi = parseAi(arg.substring("--dark=".length()));
            } else if (arg.startsWith("--light=")) {
                cfg.lightAi = parseAi(arg.substring("--light=".length()));
            } else if (arg.startsWith("--dark-weights=")) {
                cfg.darkWeightsPath = Paths.get(arg.substring("--dark-weights=".length()));
            } else if (arg.startsWith("--light-weights=")) {
                cfg.lightWeightsPath = Paths.get(arg.substring("--light-weights=".length()));
            } else if (arg.startsWith("--csv=")) {
                cfg.outputCsv = Paths.get(arg.substring("--csv=".length()));
            } else if (arg.equals("--no-csv")) {
                cfg.outputCsv = null;
            } else if (arg.equals("--verbose")) {
                cfg.verbose = true;
            } else if (arg.equals("--quiet")) {
                cfg.verbose = false;
            } else if (arg.startsWith("--maxDecisions=")) {
                cfg.maxDecisions = Integer.parseInt(arg.substring("--maxDecisions=".length()));
            } else if (arg.startsWith("--maxMillis=")) {
                cfg.maxMillis = Long.parseLong(arg.substring("--maxMillis=".length()));
            } else if (arg.startsWith("--format=")) {
                cfg.formatName = arg.substring("--format=".length());
            } else if (arg.equals("--traces") || arg.equals("--traces=true")) {
                cfg.writeTraces = true;
            } else if (arg.equals("--traces=false") || arg.equals("--no-traces")) {
                cfg.writeTraces = false;
            } else if (arg.startsWith("--tracesPath=")) {
                cfg.writeTraces = true;
                cfg.tracesPath = Paths.get(arg.substring("--tracesPath=".length()));
            } else if (arg.startsWith("--decks=")) {
                cfg.deckPack = arg.substring("--decks=".length());
                if (HeadlessBotVsBotRunner.DECK_WC96.equalsIgnoreCase(cfg.deckPack)
                        || "wc96-anh".equalsIgnoreCase(cfg.deckPack)) {
                    cfg.deckPack = HeadlessBotVsBotRunner.DECK_WC96;
                    if ("open".equals(cfg.formatName)) {
                        cfg.formatName = "premiere_anh";
                    }
                }
            } else if (arg.equals("--replay") || arg.equals("--replay=true")) {
                cfg.recordReplay = true;
            } else if (arg.equals("--replay=false") || arg.equals("--no-replay")) {
                cfg.recordReplay = false;
                cfg.replayDir = null;
            } else if (arg.startsWith("--replayDir=")) {
                cfg.recordReplay = true;
                cfg.replayDir = Paths.get(arg.substring("--replayDir=".length()));
            } else if (arg.equals("--help") || arg.equals("-h")) {
                System.out.println("Usage: HeadlessBotVsBotBatch --games=5 --dark=BEGINNER --light=BEGINNER "
                        + "--csv=target/out.csv [--decks=open40|wc96] [--format=open|premiere_anh] "
                        + "[--replay] [--replayDir=target/headless-replays] "
                        + "[--traces] [--tracesPath=target/traces.jsonl] "
                        + "[--verbose] [--maxDecisions=25000] [--maxMillis=180000]");
                return;
            }
        }

        BatchResult result = runBatch(cfg);
        System.out.println(result);
        if (result.errorCount > 0) {
            System.exit(2);
        }
    }
}
