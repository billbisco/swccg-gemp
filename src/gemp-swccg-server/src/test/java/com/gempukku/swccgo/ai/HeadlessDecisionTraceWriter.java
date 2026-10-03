package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.ai.features.InformationSetEncoder;
import com.gempukku.swccgo.ai.features.InformationSetTracker;
import com.gempukku.swccgo.ai.features.InformationSetV1;
import com.gempukku.swccgo.ai.models.LinearActionFeatures;
import com.gempukku.swccgo.ai.models.LinearPolicyAi;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Appends one compact JSON object per AI decision (JSONL) for later policy training / eval.
 * Keeps payloads small: titles/ids, capped option lists — not full game dumps.
 *
 * <p>System properties (also used by batch/spike tests):
 * <ul>
 *   <li>{@code headless.traces} — {@code true}/{@code false} (default false)</li>
 *   <li>{@code headless.traces.path} — output path (default {@code target/headless-decision-traces.jsonl})</li>
 *   <li>{@code headless.traceLevel} — {@code COMPACT} (default) or {@code FEATURES}
 *       (embeds InformationSetV1 bags + packed[128] under {@code state}, plus
 *       {@code state.bagHash}, the 16-d vector {@link LinearPolicyAi#bagHash} uses)</li>
 * </ul>
 */
public final class HeadlessDecisionTraceWriter implements Closeable {

    public static final String DEFAULT_PATH = "target/headless-decision-traces.jsonl";

    public enum TraceLevel {
        COMPACT,
        FEATURES
    }

    /** Param keys useful for training; UI-only fluff is omitted. */
    private static final Set<String> OPTION_KEYS = new java.util.LinkedHashSet<>(Arrays.asList(
            "actionId", "cardId", "blueprintId", "actionText", "results", "cardText",
            "min", "max", "defaultValue", "defaultIndex", "selectable", "preselected",
            "autoPassEligible", "noPass", "yourTurn"
    ));

    private static final int MAX_OPTIONS = 48;
    private static final int MAX_TEXT_LEN = 96;
    private static final int MAX_CHOSEN_LEN = 160;
    private static final int MAX_DECISION_TEXT_LEN = 120;

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final Path path;
    private final BufferedWriter writer;
    private final TraceLevel traceLevel;
    private long linesWritten;
    /** 0 = no cap. Further records are dropped so a batch cannot grow a full jsonl. */
    private int maxLines;

    public HeadlessDecisionTraceWriter(Path path) throws IOException {
        this(path, levelFromSystemProperties());
    }

    public HeadlessDecisionTraceWriter(Path path, TraceLevel traceLevel) throws IOException {
        this.path = path.toAbsolutePath().normalize();
        this.traceLevel = traceLevel != null ? traceLevel : TraceLevel.COMPACT;
        Path parent = this.path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        this.writer = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public TraceLevel getTraceLevel() {
        return traceLevel;
    }

    public Path getPath() {
        return path;
    }

    public long getLinesWritten() {
        return linesWritten;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = Math.max(0, maxLines);
    }

    private boolean atLineCap() {
        return maxLines > 0 && linesWritten >= maxLines;
    }

    /**
     * Record one decision attempt. Call after {@code ai.decide} and after
     * {@code decision.decisionMade} (or on invalid) so {@code accepted} is known.
     */
    public synchronized void record(TraceContext ctx) throws IOException {
        if (ctx == null || atLineCap()) {
            return;
        }
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("ts", ctx.timestampMs);
        row.put("decisionIndex", ctx.decisionIndex);
        row.put("gameId", ctx.gameId);
        if (ctx.gameIndex != null) {
            row.put("gameIndex", ctx.gameIndex);
        }
        row.put("playerId", ctx.playerId);
        row.put("side", ctx.side);
        row.put("aiSkill", ctx.aiSkill);
        if (ctx.format != null && !ctx.format.isEmpty()) {
            row.put("format", ctx.format);
        }
        row.put("turn", ctx.turn);
        row.put("phase", ctx.phase != null ? ctx.phase.name() : null);
        row.put("decisionType", ctx.decisionType);
        row.put("decisionId", ctx.decisionId);
        if (ctx.decisionText != null && !ctx.decisionText.isEmpty()) {
            row.put("decisionText", truncate(ctx.decisionText, MAX_DECISION_TEXT_LEN));
        }
        row.put("options", ctx.options != null ? ctx.options : Collections.emptyMap());
        row.put("optionCount", ctx.optionCount);
        row.put("chosen", truncate(ctx.chosen, MAX_CHOSEN_LEN));
        row.put("accepted", ctx.accepted);
        if (ctx.invalidReason != null) {
            row.put("invalidReason", truncate(ctx.invalidReason, 120));
        }
        row.put("darkLF", ctx.darkLF);
        row.put("lightLF", ctx.lightLF);
        row.put("darkHand", ctx.darkHand);
        row.put("lightHand", ctx.lightHand);

        if (traceLevel == TraceLevel.FEATURES || ctx.informationSet != null) {
            row.put("type", "step");
            row.put("schemaVersionFeatures", 1);
            row.put("traceLevel", "FEATURES");
            if (ctx.informationSet != null) {
                Map<String, Object> state = ctx.informationSet.toMap();
                state.put("bagHash", bagHashList(ctx.informationSet));
                row.put("state", state);
            }
        }

        writer.write(GSON.toJson(row));
        writer.newLine();
        linesWritten++;
        // Flush periodically so a crash mid-game still leaves usable traces.
        if (linesWritten % 50 == 0) {
            writer.flush();
        }
    }

    /**
     * One JSONL header line per game so learners see format/deck context even when
     * scanning only the first line of a game's decisions.
     */
    public synchronized void writeGameHeader(String gameId, Integer gameIndex, String format,
                                             String darkDeck, String lightDeck,
                                             String darkAi, String lightAi) throws IOException {
        if (atLineCap()) {
            return;
        }
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("type", "header");
        row.put("schemaVersion", 1);
        row.put("ts", System.currentTimeMillis());
        row.put("gameId", gameId);
        if (gameIndex != null) {
            row.put("gameIndex", gameIndex);
        }
        row.put("format", format != null ? format : "");
        row.put("darkDeck", darkDeck != null ? darkDeck : "");
        row.put("lightDeck", lightDeck != null ? lightDeck : "");
        row.put("darkAi", darkAi != null ? darkAi : "");
        row.put("lightAi", lightAi != null ? lightAi : "");
        writer.write(GSON.toJson(row));
        writer.newLine();
        linesWritten++;
        writer.flush();
    }

    /** One JSONL outcome line after the game ends (winner, format, final life force). */
    public synchronized void writeGameOutcome(String gameId, Integer gameIndex, String format,
                                              boolean finished, boolean cancelled, String winner,
                                              String stopper, int decisionCount,
                                              int darkLifeForce, int lightLifeForce) throws IOException {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("type", "outcome");
        row.put("schemaVersion", 1);
        row.put("ts", System.currentTimeMillis());
        row.put("gameId", gameId);
        if (gameIndex != null) {
            row.put("gameIndex", gameIndex);
        }
        row.put("format", format != null ? format : "");
        row.put("finished", finished);
        row.put("cancelled", cancelled);
        row.put("winner", winner);
        row.put("stopper", stopper != null ? stopper : "");
        row.put("decisionCount", decisionCount);
        row.put("darkLF", darkLifeForce);
        row.put("lightLF", lightLifeForce);
        row.put("darkLifeForce", darkLifeForce);
        row.put("lightLifeForce", lightLifeForce);
        writer.write(GSON.toJson(row));
        writer.newLine();
        linesWritten++;
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

    /**
     * Same 16-d summary {@link LinearPolicyAi} scores with when {@code bagHashDim}
     * is {@link LinearPolicyAi#DEFAULT_BAG_HASH_DIM}. Counts blueprintId (else title)
     * from own hand, public in-play, own public piles, opponent revealed, and seen
     * history, then divides by the max bucket.
     */
    static List<Float> bagHashList(InformationSetV1 set) {
        float[] hash = LinearPolicyAi.bagHash(set, LinearPolicyAi.DEFAULT_BAG_HASH_DIM);
        List<Float> out = new ArrayList<>(hash.length);
        for (float v : hash) {
            out.add(v);
        }
        return out;
    }

    /** Build options summary + optionCount from an awaiting decision. */
    public static CompactOptions compactOptions(AwaitingDecision decision) {
        CompactOptions out = new CompactOptions();
        if (decision == null || decision.getDecisionParameters() == null) {
            return out;
        }
        Map<String, String[]> params = decision.getDecisionParameters();
        Map<String, Object> compact = new LinkedHashMap<>();

        // Prefer aligned option arrays (ids + short labels).
        String[] actionIds = params.get("actionId");
        String[] cardIds = params.get("cardId");
        String[] blueprintIds = params.get("blueprintId");
        String[] actionTexts = params.get("actionText");
        String[] results = params.get("results");
        String[] cardTexts = params.get("cardText");

        int n = maxLen(actionIds, cardIds, blueprintIds, actionTexts, results, cardTexts);
        out.optionCount = n;

        if (n > 0) {
            List<Map<String, String>> options = new ArrayList<>(Math.min(n, MAX_OPTIONS));
            int limit = Math.min(n, MAX_OPTIONS);
            for (int i = 0; i < limit; i++) {
                Map<String, String> opt = new LinkedHashMap<>();
                putIfPresent(opt, "actionId", at(actionIds, i));
                putIfPresent(opt, "cardId", at(cardIds, i));
                putIfPresent(opt, "blueprintId", at(blueprintIds, i));
                String label = firstNonEmpty(at(actionTexts, i), at(results, i), at(cardTexts, i));
                if (label != null) {
                    opt.put("text", truncate(label, MAX_TEXT_LEN));
                }
                options.add(opt);
            }
            compact.put("items", options);
            if (n > MAX_OPTIONS) {
                compact.put("truncated", true);
            }
        }

        // Scalar / small array params useful for INTEGER / MULTIPLE_CHOICE bounds etc.
        for (String key : OPTION_KEYS) {
            if ("actionId".equals(key) || "cardId".equals(key) || "blueprintId".equals(key)
                    || "actionText".equals(key) || "results".equals(key) || "cardText".equals(key)) {
                continue; // already folded into items
            }
            String[] vals = params.get(key);
            if (vals == null || vals.length == 0) {
                continue;
            }
            if (vals.length == 1) {
                compact.put(key, truncate(vals[0], MAX_TEXT_LEN));
            } else if (vals.length <= 8) {
                List<String> shortList = new ArrayList<>(vals.length);
                for (String v : vals) {
                    shortList.add(truncate(v, 40));
                }
                compact.put(key, shortList);
            } else {
                compact.put(key + "Count", vals.length);
            }
        }

        out.map = compact;
        return out;
    }

    public static TraceContext fromDecision(String gameId, Integer gameIndex, int decisionIndex,
                                            String playerId, String aiSkill, String format,
                                            AwaitingDecision decision, GameState gs,
                                            String chosen, boolean accepted, String invalidReason) {
        return fromDecision(gameId, gameIndex, decisionIndex, playerId, aiSkill, format,
                decision, gs, chosen, accepted, invalidReason, TraceLevel.COMPACT, null);
    }

    public static TraceContext fromDecision(String gameId, Integer gameIndex, int decisionIndex,
                                            String playerId, String aiSkill, String format,
                                            AwaitingDecision decision, GameState gs,
                                            String chosen, boolean accepted, String invalidReason,
                                            TraceLevel level, InformationSetTracker tracker) {
        TraceContext ctx = new TraceContext();
        ctx.timestampMs = System.currentTimeMillis();
        ctx.decisionIndex = decisionIndex;
        ctx.gameId = gameId;
        ctx.gameIndex = gameIndex;
        ctx.playerId = playerId;
        ctx.side = sideOf(playerId);
        ctx.aiSkill = aiSkill;
        ctx.format = format;
        ctx.chosen = chosen;
        ctx.accepted = accepted;
        ctx.invalidReason = invalidReason;

        if (decision != null) {
            ctx.decisionId = decision.getAwaitingDecisionId();
            ctx.decisionType = decision.getDecisionType() != null ? decision.getDecisionType().name() : null;
            ctx.decisionText = decision.getText();
            CompactOptions opts = compactOptions(decision);
            ctx.options = opts.map;
            ctx.optionCount = opts.optionCount;
        }

        if (gs != null) {
            ctx.phase = gs.getCurrentPhase();
            ctx.darkLF = gs.getPlayerLifeForce(HeadlessBotVsBotRunner.DS_PLAYER);
            ctx.lightLF = gs.getPlayerLifeForce(HeadlessBotVsBotRunner.LS_PLAYER);
            ctx.darkHand = safeHandSize(gs, HeadlessBotVsBotRunner.DS_PLAYER);
            ctx.lightHand = safeHandSize(gs, HeadlessBotVsBotRunner.LS_PLAYER);
            if (HeadlessBotVsBotRunner.DS_PLAYER.equals(playerId)) {
                ctx.turn = gs.getPlayersLatestTurnNumber(HeadlessBotVsBotRunner.DS_PLAYER);
            } else if (HeadlessBotVsBotRunner.LS_PLAYER.equals(playerId)) {
                ctx.turn = gs.getPlayersLatestTurnNumber(HeadlessBotVsBotRunner.LS_PLAYER);
            } else {
                ctx.turn = -1;
            }
            if (level == TraceLevel.FEATURES) {
                try {
                    LinearActionFeatures.annotateItems(ctx.options, gs, playerId);
                } catch (RuntimeException ex) {
                    System.err.println("[headless] grounded features failed: " + ex.getClass().getSimpleName()
                            + ": " + ex.getMessage());
                }
                try {
                    ctx.informationSet = InformationSetEncoder.from(gs, playerId, decision, tracker, format);
                } catch (RuntimeException ex) {
                    System.err.println("[headless] FEATURES encode failed: " + ex.getClass().getSimpleName()
                            + ": " + ex.getMessage());
                }
            }
        }
        return ctx;
    }

    public static boolean enabledFromSystemProperties() {
        return Boolean.parseBoolean(System.getProperty("headless.traces", "false"));
    }

    public static Path pathFromSystemProperties() {
        return java.nio.file.Paths.get(System.getProperty("headless.traces.path", DEFAULT_PATH));
    }

    public static TraceLevel levelFromSystemProperties() {
        String raw = System.getProperty("headless.traceLevel", "COMPACT");
        if (raw == null || raw.isBlank()) {
            return TraceLevel.COMPACT;
        }
        try {
            return TraceLevel.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return TraceLevel.COMPACT;
        }
    }

    private static int safeHandSize(GameState gs, String playerId) {
        try {
            List<?> hand = gs.getHand(playerId);
            return hand != null ? hand.size() : -1;
        } catch (RuntimeException ex) {
            return -1;
        }
    }

    private static String sideOf(String playerId) {
        if (HeadlessBotVsBotRunner.DS_PLAYER.equals(playerId)) {
            return "DARK";
        }
        if (HeadlessBotVsBotRunner.LS_PLAYER.equals(playerId)) {
            return "LIGHT";
        }
        return "UNKNOWN";
    }

    private static int maxLen(String[]... arrays) {
        int m = 0;
        for (String[] a : arrays) {
            if (a != null && a.length > m) {
                m = a.length;
            }
        }
        return m;
    }

    private static String at(String[] arr, int i) {
        if (arr == null || i < 0 || i >= arr.length) {
            return null;
        }
        return arr[i];
    }

    private static void putIfPresent(Map<String, String> map, String key, String value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }

    private static String firstNonEmpty(String... values) {
        if (values == null) {
            return null;
        }
        for (String v : values) {
            if (v != null && !v.isEmpty()) {
                return v;
            }
        }
        return null;
    }

    static String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "...";
    }

    public static final class CompactOptions {
        public Map<String, Object> map = new LinkedHashMap<>();
        public int optionCount;
    }

    public static final class TraceContext {
        public long timestampMs;
        public int decisionIndex;
        public String gameId;
        public Integer gameIndex;
        public String playerId;
        public String side;
        public String aiSkill;
        /** GEMP format code, e.g. premiere_anh or open. */
        public String format;
        public int turn = -1;
        public Phase phase;
        public String decisionType;
        public int decisionId = -1;
        public String decisionText;
        public Map<String, Object> options;
        public int optionCount;
        public String chosen;
        public boolean accepted;
        public String invalidReason;
        public int darkLF = -1;
        public int lightLF = -1;
        public int darkHand = -1;
        public int lightHand = -1;
        /** Populated when traceLevel=FEATURES. */
        public InformationSetV1 informationSet;
    }

    /** Parse helper for docs / tests. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "HeadlessDecisionTraceWriter{path=%s, lines=%d}", path, linesWritten);
    }
}
