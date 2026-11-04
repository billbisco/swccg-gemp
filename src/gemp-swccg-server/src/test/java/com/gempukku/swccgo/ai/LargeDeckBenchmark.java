package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.state.GameCommunicationChannel;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import org.junit.Assume;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * swccg-guild/work#136: how GEMP copes with very large Anything Goes decks.
 * <p>
 * Plays bot-vs-bot games headless, with the server's per-player communication channels attached (as SwccgGameMediator
 * does) and the engine run to the next decision after each answer, and records the engine time of every decision,
 * the cost of a revert snapshot as the board grows, the cards in play and memory. It doesn't run in the normal suite;
 * run it with
 * <pre>mvn -o test -pl gemp-swccg-server -Dtest=LargeDeckBenchmark -Dgemp.bench=true
 *     [-Dgemp.bench.sizes=60,100,200] [-Dgemp.bench.seeds=1,2] [-Dgemp.bench.ai=ADVANCED]
 *     [-Dgemp.bench.minutes=10] [-Dgemp.bench.out=target/large-deck-bench]</pre>
 * Decks are the 1996 World Champion ANH pair, repeated to the size asked for (every card in the same proportion).
 */
public class LargeDeckBenchmark {
    private static final int SNAPSHOT_EVERY = 250;

    static String scaled(String contents, int size) {
        return scaled(contents, size, null);
    }

    /**
     * The deck repeated to {@code size} cards, or (with a {@code filler} blueprint) the deck once and then that card
     * to fill it: a non-unique character such as Stormtrooper, so the bots can keep putting more cards on the table.
     */
    static String scaled(String contents, int size, String filler) {
        String cards = contents.substring(0, contents.indexOf('|'));
        List<String> base = Arrays.asList(cards.split(","));
        List<String> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            out.add(filler != null && i >= base.size() ? filler : base.get(i % base.size()));
        return String.join(",", out) + "|";
    }

    /**
     * #136 (Bill: "high Card Open decks with a lot of Defensive shields and Effects on both sides"): one big deck per
     * side made by merging real tournament decks (test resource bench/&lt;name&gt;-decks.json, e.g. the 2026 World
     * Championship GEMP files) in a seeded order until {@code size} cards; their cards outside the deck (Defensive
     * Shields and the like) are all kept, once each.
     */
    @SuppressWarnings("unchecked")
    static String merged(String resource, String side, int size, long seed) {
        try (java.io.InputStream in = LargeDeckBenchmark.class.getResourceAsStream("/bench/" + resource + "-decks.json")) {
            if (in == null)
                throw new IllegalStateException("no test resource bench/" + resource + "-decks.json");
            com.google.gson.JsonObject all = com.google.gson.JsonParser.parseReader(
                    new java.io.InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject(side);
            List<String> names = new ArrayList<>(all.keySet());
            Collections.sort(names);
            Collections.shuffle(names, new java.util.Random(seed * 31 + side.hashCode()));
            List<String> cards = new ArrayList<>();
            java.util.LinkedHashSet<String> outside = new java.util.LinkedHashSet<>();
            for (int round = 0; cards.size() < size; round++) {
                for (String name : names) {
                    com.google.gson.JsonObject deck = all.getAsJsonObject(name);
                    for (com.google.gson.JsonElement c : deck.getAsJsonArray("cards")) {
                        if (cards.size() < size)
                            cards.add(c.getAsString());
                    }
                    for (com.google.gson.JsonElement c : deck.getAsJsonArray("outside"))
                        outside.add(c.getAsString());
                    if (cards.size() >= size)
                        break;
                }
            }
            return String.join(",", cards) + "|" + String.join(",", outside);
        } catch (IOException exp) {
            throw new IllegalStateException(exp);
        }
    }

    static final class Row {
        int size; long seed; String ai; boolean finished; String stopper; String winner;
        int decisions; int turns; double engineSec;
        double medianMs, p99Ms, maxMs; int over1s, over5s;
        int maxInPlay; double snapshotMsLast, snapshotMsMax; int snapshotsTaken;
        long events; double heapMb; String failure = "";

        String csv() {
            return String.format(Locale.ROOT, "%d,%d,%s,%s,%s,%s,%d,%d,%.1f,%.2f,%.1f,%.1f,%d,%d,%d,%.2f,%.2f,%d,%d,%.0f,%s",
                    size, seed, ai, finished, stopper, winner, decisions, turns, engineSec, medianMs, p99Ms, maxMs,
                    over1s, over5s, maxInPlay, snapshotMsLast, snapshotMsMax, snapshotsTaken, events, heapMb,
                    failure.replace(',', ';').replace('\n', ' '));
        }
    }

    static final String HEADER = "size,seed,ai,finished,stopper,winner,decisions,turns,engine_s,median_ms,p99_ms,max_ms,"
            + "steps_over_1s,steps_over_5s,max_cards_in_play,snapshot_ms_last,snapshot_ms_max,snapshots_timed,events,heap_mb,failure";

    static Row play(int size, long seed, HeadlessBotVsBotRunner.AiSkill ai, long maxMillis,
                    SwccgCardBlueprintLibrary cards, SwccgoFormatLibrary formats) {
        System.setProperty("gemp.shuffleSeed", String.valueOf(seed));
        Row row = new Row();
        row.size = size;
        row.seed = seed;
        row.ai = ai.name();
        List<Long> steps = new ArrayList<>();
        List<GameCommunicationChannel> channels = new ArrayList<>();
        long[] events = {0};
        double[] snap = {0, 0};
        int[] snapCount = {0};
        int[] maxInPlay = {0};
        boolean[] pendingSnap = {false};

        HeadlessBotVsBotRunner.Config cfg = new HeadlessBotVsBotRunner.Config();
        cfg.formatName = "anything_goes";
        cfg.darkAi = ai;
        cfg.lightAi = ai;
        boolean troopers = Boolean.getBoolean("gemp.bench.troopers");
        String deckSource = System.getProperty("gemp.bench.decks");
        if (deckSource != null && !deckSource.isEmpty()) {
            cfg.darkDeckContents = merged(deckSource, "dark", size, seed);
            cfg.lightDeckContents = merged(deckSource, "light", size, seed);
        } else {
            cfg.darkDeckContents = scaled(HeadlessBotVsBotRunner.WC96_ANH_DARK, size, troopers ? "1_194" : null);   // Stormtrooper
            cfg.lightDeckContents = scaled(HeadlessBotVsBotRunner.WC96_ANH_LIGHT, size, troopers ? "1_28" : null);  // Rebel Trooper
        }
        cfg.darkDeckName = "WC96 Dark x" + size;
        cfg.lightDeckName = "WC96 Light x" + size;
        cfg.maxDecisions = Integer.getInteger("gemp.bench.maxDecisions", 400_000);
        // -Dheadless.traces=true -Dheadless.traces.path=FILE: the runner's JSONL trace (each decision and the answer given)
        if (HeadlessDecisionTraceWriter.enabledFromSystemProperties()) {
            try {
                cfg.traceWriter = new HeadlessDecisionTraceWriter(HeadlessDecisionTraceWriter.pathFromSystemProperties(),
                        HeadlessDecisionTraceWriter.levelFromSystemProperties());
            } catch (IOException exp) {
                throw new IllegalStateException(exp);
            }
        }
        // -Dgemp.bench.trace=FILE: one line per decision (phase, player, pile and board sizes) to diff two runs and
        // find the first decision where they part (an exact optimization must not change a single line)
        String tracePath = System.getProperty("gemp.bench.trace");
        java.io.PrintWriter trace;
        try {
            trace = tracePath == null ? null : new java.io.PrintWriter(Files.newBufferedWriter(Paths.get(tracePath)));
        } catch (IOException exp) {
            throw new IllegalStateException(exp);
        }
        cfg.maxMillis = maxMillis;
        cfg.stepNanos = steps::add;
        cfg.beforeStart = game -> {
            int n = 0;
            for (String player : Arrays.asList(HeadlessBotVsBotRunner.DS_PLAYER, HeadlessBotVsBotRunner.LS_PLAYER)) {
                GameCommunicationChannel channel = new GameCommunicationChannel(player, n++);
                game.addGameStateListener(player, channel);
                channels.add(channel);
            }
        };
        cfg.afterDecision = (DefaultSwccgGame game, Integer count) -> {
            boolean first = true;
            for (GameCommunicationChannel channel : channels) {
                java.util.List<com.gempukku.swccgo.game.state.GameEvent> consumed = channel.consumeGameEvents();
                events[0] += consumed.size();
                // the game log of one seat goes into the trace, so a split between two runs shows what happened
                if (first && trace != null)
                    for (com.gempukku.swccgo.game.state.GameEvent e : consumed)
                        if (e.getMessage() != null)
                            trace.println("  msg " + e.getMessage().replaceAll("<[^>]*>", ""));
                first = false;
            }
            GameState gs = game.getGameState();
            if (gs == null)
                return;
            maxInPlay[0] = Math.max(maxInPlay[0], gs.getCardsInPlay().size());
            if (trace != null) {
                StringBuilder line = new StringBuilder().append(count).append(' ').append(gs.getCurrentPhase())
                        .append(' ').append(gs.getCurrentPlayerId()).append(" play=").append(gs.getCardsInPlay().size());
                for (String p : Arrays.asList(HeadlessBotVsBotRunner.DS_PLAYER, HeadlessBotVsBotRunner.LS_PLAYER))
                    line.append(' ').append(gs.getHand(p).size()).append('/').append(gs.getReserveDeck(p).size())
                            .append('/').append(gs.getForcePile(p).size()).append('/').append(gs.getUsedPile(p).size())
                            .append('/').append(gs.getLostPile(p).size());
                trace.println(line);
                if (count % 500 == 0)
                    trace.flush();
            }
            // Time a revert snapshot of the current board every SNAPSHOT_EVERY decisions (or at the next point where the
            // engine allows one), without adding it to the game's snapshot list.
            if ((count % SNAPSHOT_EVERY == 0 || pendingSnap[0]) && !game.isFinished()) {
                try {
                    long t = System.nanoTime();
                    com.gempukku.swccgo.logic.timing.GameSnapshot.createGameSnapshot(-1, "benchmark", gs,
                            (com.gempukku.swccgo.logic.modifiers.querying.ModifiersLogic) field(game, "_modifiersLogic"),
                            game.getActionsEnvironment(),
                            (com.gempukku.swccgo.logic.timing.TurnProcedure) field(game, "_turnProcedure"));
                    double ms = (System.nanoTime() - t) / 1e6;
                    snap[0] = ms;
                    snap[1] = Math.max(snap[1], ms);
                    snapCount[0]++;
                    pendingSnap[0] = false;
                } catch (RuntimeException notNow) {
                    // the engine only snapshots at clean points (phase starts); try again on a later decision
                    pendingSnap[0] = true;
                }
            }
        };
        HeadlessBotVsBotRunner.Result result;
        BoardScalingBenchmark.Sampler sampler = Boolean.getBoolean("gemp.bench.sample")
                ? new BoardScalingBenchmark.Sampler(Thread.currentThread()) : null;
        try {
            result = HeadlessBotVsBotRunner.playOneGame(cfg, cards, formats);
            if (sampler != null)
                sampler.report(size);
        } catch (InterruptedException exp) {
            throw new IllegalStateException(exp);
        } catch (RuntimeException exp) {
            exp.printStackTrace(System.out);
            row.failure = exp.getClass().getSimpleName() + ": " + exp.getMessage();
            return row;
        } finally {
            if (trace != null)
                trace.close();
            if (cfg.traceWriter != null) {
                try {
                    cfg.traceWriter.close();
                } catch (Exception ignored) {
                }
            }
        }
        row.finished = result.finished;
        row.stopper = result.stopper == null ? "" : result.stopper;
        row.winner = (result.winner == null ? "" : result.winner) + " (" + (result.winReasonSummary == null ? ""
                : result.winReasonSummary.replace(',', ';')) + ") lf=" + result.darkLifeForce + "/" + result.lightLifeForce;
        row.decisions = result.decisionCount;
        row.turns = Math.max(result.darkTurnNumber, result.lightTurnNumber);
        if (!result.failureNotes.isEmpty())
            row.failure = result.failureNotes.size() + " notes: " + result.failureNotes.get(0);
        List<Long> sorted = new ArrayList<>(steps);
        Collections.sort(sorted);
        long total = 0;
        for (long s : sorted)
            total += s;
        row.engineSec = total / 1e9;
        if (!sorted.isEmpty()) {
            row.medianMs = sorted.get(sorted.size() / 2) / 1e6;
            row.p99Ms = sorted.get(Math.min(sorted.size() - 1, (int) (sorted.size() * 0.99))) / 1e6;
            row.maxMs = sorted.get(sorted.size() - 1) / 1e6;
        }
        for (long s : sorted) {
            if (s > 1_000_000_000L) row.over1s++;
            if (s > 5_000_000_000L) row.over5s++;
        }
        row.maxInPlay = maxInPlay[0];
        row.snapshotMsLast = snap[0];
        row.snapshotMsMax = snap[1];
        row.snapshotsTaken = snapCount[0];
        row.events = events[0];
        System.gc();
        Runtime rt = Runtime.getRuntime();
        row.heapMb = (rt.totalMemory() - rt.freeMemory()) / (1024.0 * 1024.0);
        return row;
    }

    private static Object field(Object target, String name) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(target);
        } catch (ReflectiveOperationException exp) {
            throw new IllegalStateException(exp);
        }
    }

    @Test
    public void LargeDeckBenchmarkRun() throws IOException {
        Assume.assumeTrue("set -Dgemp.bench=true to run", Boolean.getBoolean("gemp.bench"));
        int[] sizes = Arrays.stream(System.getProperty("gemp.bench.sizes", "60,100,200,300,500,1000").split(","))
                .mapToInt(s -> Integer.parseInt(s.trim())).toArray();
        long[] seeds = Arrays.stream(System.getProperty("gemp.bench.seeds", "1").split(","))
                .mapToLong(s -> Long.parseLong(s.trim())).toArray();
        HeadlessBotVsBotRunner.AiSkill ai = HeadlessBotVsBotRunner.AiSkill.valueOf(System.getProperty("gemp.bench.ai", "ADVANCED"));
        long maxMillis = Long.getLong("gemp.bench.minutes", 10L) * 60_000L;
        Path out = Paths.get(System.getProperty("gemp.bench.out", "target/large-deck-bench"));
        Files.createDirectories(out);
        Path csv = out.resolve("results.csv");
        if (!Files.exists(csv))
            Files.write(csv, (HEADER + "\n").getBytes(StandardCharsets.UTF_8));

        SwccgCardBlueprintLibrary cards = new SwccgCardBlueprintLibrary();
        SwccgoFormatLibrary formats = new SwccgoFormatLibrary(cards);
        for (int size : sizes) {
            for (long seed : seeds) {
                Row row = play(size, seed, ai, maxMillis, cards, formats);
                String line = row.csv();
                System.out.println("[bench136] " + line);
                Files.write(csv, (line + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            }
        }
    }
}
