package com.gempukku.swccgo.ai;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Batch self-play: N games via {@link HeadlessBotVsBotBatch}, CSV to disk.
 *
 * <pre>
 * cd src && mvn -pl gemp-swccg-server -am \
 *   -Dtest=HeadlessBotVsBotBatchTest \
 *   -Dheadless.games=5 \
 *   -Dheadless.dark=BEGINNER -Dheadless.light=BEGINNER \
 *   test
 * </pre>
 *
 * Optional: {@code -Dheadless.alsoAdvanced=true} runs a second small batch Beginner vs Advanced.
 * Optional: {@code -Dheadless.alsoRando=true} runs Beginner vs RandoCalAi.
 */
public class HeadlessBotVsBotBatchTest {

    @Test
    public void batchSelfPlay_writesCsv() throws Exception {
        int games = Integer.getInteger("headless.games", 5);
        HeadlessBotVsBotRunner.AiSkill dark = parseAi(
                System.getProperty("headless.dark", "BEGINNER"));
        HeadlessBotVsBotRunner.AiSkill light = parseAi(
                System.getProperty("headless.light", "BEGINNER"));
        Path csv = Paths.get(System.getProperty("headless.csv",
                "target/headless-bot-vs-bot-batch.csv"));

        HeadlessBotVsBotBatch.BatchConfig cfg = new HeadlessBotVsBotBatch.BatchConfig();
        cfg.games = games;
        cfg.darkAi = dark;
        cfg.lightAi = light;
        cfg.outputCsv = csv;
        cfg.verbose = Boolean.parseBoolean(System.getProperty("headless.verbose", "false"));
        cfg.maxDecisions = Integer.getInteger("headless.maxDecisions", 25_000);
        cfg.maxMillis = Long.getLong("headless.maxMillis", 180_000L);
        cfg.writeTraces = Boolean.parseBoolean(System.getProperty("headless.traces", "false"));
        cfg.tracesPath = Paths.get(System.getProperty("headless.traces.path",
                HeadlessDecisionTraceWriter.DEFAULT_PATH));
        cfg.deckPack = System.getProperty("headless.decks", HeadlessBotVsBotRunner.DECK_OPEN40);
        if (HeadlessBotVsBotRunner.DECK_WC96.equalsIgnoreCase(cfg.deckPack)
                || "wc96-anh".equalsIgnoreCase(cfg.deckPack)) {
            cfg.deckPack = HeadlessBotVsBotRunner.DECK_WC96;
        }
        cfg.formatName = System.getProperty("headless.format",
                HeadlessBotVsBotRunner.DECK_WC96.equals(cfg.deckPack) ? "premiere_anh" : "open");
        cfg.recordReplay = Boolean.parseBoolean(System.getProperty("headless.replay", "false"));
        String replayDirProp = System.getProperty("headless.replay.dir");
        if (replayDirProp != null && !replayDirProp.isEmpty()) {
            cfg.recordReplay = true;
            cfg.replayDir = Paths.get(replayDirProp);
        } else if (cfg.recordReplay) {
            cfg.replayDir = Paths.get("target", "headless-replays");
        }
        String darkW = System.getProperty("headless.dark.weights");
        if (darkW != null && !darkW.isEmpty()) {
            cfg.darkWeightsPath = Paths.get(darkW);
        }
        String lightW = System.getProperty("headless.light.weights");
        if (lightW != null && !lightW.isEmpty()) {
            cfg.lightWeightsPath = Paths.get(lightW);
        }

        HeadlessBotVsBotBatch.BatchResult batch = HeadlessBotVsBotBatch.runBatch(cfg);
        System.out.println("=== Batch " + dark + " vs " + light + " ===");
        System.out.println(batch);

        assertTrue("CSV should exist: " + batch.csvPath, Files.isRegularFile(batch.csvPath));
        String csvText = new String(Files.readAllBytes(batch.csvPath), java.nio.charset.StandardCharsets.UTF_8);
        System.out.println("--- CSV ---");
        System.out.println(csvText);
        assertTrue("CSV should start with header",
                csvText.startsWith(HeadlessBotVsBotBatch.CSV_HEADER));
        assertEquals("row count should match games", games, batch.rows.size());

        if (cfg.writeTraces) {
            assertTrue("traces JSONL should exist: " + batch.tracesPath,
                    Files.isRegularFile(batch.tracesPath));
            assertTrue("traces should be non-empty", batch.traceLines > 0);
            java.util.List<String> lines = Files.readAllLines(batch.tracesPath,
                    java.nio.charset.StandardCharsets.UTF_8);
            assertEquals("file lines should match writer count", batch.traceLines, lines.size());
            com.google.gson.Gson gson = new com.google.gson.Gson();
            int sample = Math.min(3, lines.size());
            System.out.println("--- Sample JSONL (" + sample + ") ---");
            for (int i = 0; i < sample; i++) {
                String line = lines.get(i);
                System.out.println(line.length() > 400 ? line.substring(0, 400) + "..." : line);
                assertTrue("line should be JSON object", line.startsWith("{"));
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> obj = gson.fromJson(line, java.util.Map.class);
                assertTrue("parsed object non-null", obj != null && !obj.isEmpty());
                assertTrue("should include decisionType", obj.containsKey("decisionType"));
                assertTrue("should include chosen", obj.containsKey("chosen"));
            }
            // Rough sanity: ~one line per accepted decision across finished games
            int totalDecisions = 0;
            for (HeadlessBotVsBotBatch.GameRow row : batch.rows) {
                if (row.result != null) {
                    totalDecisions += row.result.decisionCount;
                }
            }
            System.out.println("traceLines=" + batch.traceLines + " acceptedDecisions=" + totalDecisions);
            assertTrue("trace lines should be at least accepted decisions",
                    batch.traceLines >= totalDecisions);
        }

        if (cfg.recordReplay) {
            assertTrue("replayDir should be set", batch.replayDir != null);
            assertTrue("at least one game should write replay files", batch.replayGames > 0);
            // Spot-check first finished game's xml.gz exists and is non-trivial
            boolean foundGz = false;
            for (HeadlessBotVsBotBatch.GameRow row : batch.rows) {
                if (row.result == null || row.result.replayFiles == null) {
                    continue;
                }
                for (Path p : row.result.replayFiles.values()) {
                    assertTrue("replay file exists: " + p, Files.isRegularFile(p));
                    assertTrue("replay file non-empty: " + p, Files.size(p) > 100);
                    foundGz = true;
                    System.out.println("Replay sample: " + p + " bytes=" + Files.size(p));
                    if (row.result.replayMetaPath != null) {
                        System.out.println("Replay meta: " + row.result.replayMetaPath);
                    }
                }
            }
            assertTrue("expected at least one xml.gz", foundGz);
        }

        // Soft: prefer all finished; fail with clear summary if any did not.
        if (batch.errorCount > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Batch had ").append(batch.errorCount).append("/").append(games)
                    .append(" unfinished/error games. csv=").append(batch.csvPath).append('\n');
            for (HeadlessBotVsBotBatch.GameRow row : batch.rows) {
                if (row.error != null && !row.error.isEmpty()) {
                    sb.append("  game ").append(row.gameIndex).append(": ").append(row.error).append('\n');
                }
            }
            fail(sb.toString());
        }
        assertEquals(games, batch.finishedCount);
    }

    @Test
    public void batchBeginnerVsAdvanced_optional() throws Exception {
        if (!Boolean.parseBoolean(System.getProperty("headless.alsoAdvanced", "false"))) {
            System.out.println("Skipping Beginner vs Advanced batch (pass -Dheadless.alsoAdvanced=true)");
            return;
        }
        runOptionalMatchup(HeadlessBotVsBotRunner.AiSkill.BEGINNER, HeadlessBotVsBotRunner.AiSkill.ADVANCED,
                "target/headless-bot-vs-bot-batch-advanced.csv");
    }

    @Test
    public void batchBeginnerVsRando_optional() throws Exception {
        if (!Boolean.parseBoolean(System.getProperty("headless.alsoRando", "false"))) {
            System.out.println("Skipping Beginner vs Rando batch (pass -Dheadless.alsoRando=true)");
            return;
        }
        runOptionalMatchup(HeadlessBotVsBotRunner.AiSkill.BEGINNER, HeadlessBotVsBotRunner.AiSkill.RANDO,
                "target/headless-bot-vs-bot-batch-rando.csv");
    }

    private static void runOptionalMatchup(HeadlessBotVsBotRunner.AiSkill dark,
                                           HeadlessBotVsBotRunner.AiSkill light,
                                           String defaultCsv) throws Exception {
        int games = Integer.getInteger("headless.games", 3);
        HeadlessBotVsBotBatch.BatchConfig cfg = new HeadlessBotVsBotBatch.BatchConfig();
        cfg.games = games;
        cfg.darkAi = dark;
        cfg.lightAi = light;
        cfg.outputCsv = Paths.get(System.getProperty("headless.csv." + light.name().toLowerCase(Locale.ROOT),
                defaultCsv));
        cfg.verbose = Boolean.parseBoolean(System.getProperty("headless.verbose", "false"));
        cfg.maxDecisions = Integer.getInteger("headless.maxDecisions", 25_000);
        cfg.maxMillis = Long.getLong("headless.maxMillis", 180_000L);
        cfg.writeTraces = Boolean.parseBoolean(System.getProperty("headless.traces", "false"));
        if (cfg.writeTraces) {
            cfg.tracesPath = Paths.get(System.getProperty("headless.traces.path",
                    "target/headless-decision-traces-" + light.name().toLowerCase(Locale.ROOT) + ".jsonl"));
        }

        HeadlessBotVsBotBatch.BatchResult batch = HeadlessBotVsBotBatch.runBatch(cfg);
        System.out.println("=== Optional batch " + dark + " vs " + light + " ===");
        System.out.println(batch);
        assertTrue(Files.isRegularFile(batch.csvPath));
        assertEquals("optional matchup should finish all games", games, batch.finishedCount);
    }

    /**
     * WC96 Dark vs Light with real GEMP xml.gz replay.
     * Enabled by default when {@code headless.wc96Replay=true} (also the primary acceptance path).
     */
    @Test
    public void wc96Anh_beginnerVsBeginner_writesReplay() throws Exception {
        if (!Boolean.parseBoolean(System.getProperty("headless.wc96Replay",
                System.getProperty("headless.decks", "").equalsIgnoreCase("wc96") ? "true" : "false"))) {
            // Allow explicit run via -Dheadless.wc96Replay=true or -Dheadless.decks=wc96 on main test.
            System.out.println("Skipping WC96 replay test (pass -Dheadless.wc96Replay=true)");
            return;
        }
        Path replayDir = Paths.get(System.getProperty("headless.replay.dir",
                "target/headless-replays-wc96")).toAbsolutePath().normalize();
        Path csv = Paths.get(System.getProperty("headless.csv",
                "target/headless-bot-vs-bot-wc96.csv"));

        HeadlessBotVsBotBatch.BatchConfig cfg = new HeadlessBotVsBotBatch.BatchConfig();
        cfg.games = Integer.getInteger("headless.games", 1);
        cfg.darkAi = parseAi(System.getProperty("headless.dark", "BEGINNER"));
        cfg.lightAi = parseAi(System.getProperty("headless.light", "BEGINNER"));
        cfg.deckPack = HeadlessBotVsBotRunner.DECK_WC96;
        cfg.formatName = System.getProperty("headless.format", "premiere_anh");
        cfg.outputCsv = csv;
        cfg.recordReplay = true;
        cfg.replayDir = replayDir;
        cfg.writeTraces = Boolean.parseBoolean(System.getProperty("headless.traces", "true"));
        cfg.tracesPath = Paths.get(System.getProperty("headless.traces.path",
                "target/headless-decision-traces-wc96.jsonl"));
        cfg.verbose = Boolean.parseBoolean(System.getProperty("headless.verbose", "true"));
        cfg.maxDecisions = Integer.getInteger("headless.maxDecisions", 25_000);
        cfg.maxMillis = Long.getLong("headless.maxMillis", 300_000L);

        HeadlessBotVsBotBatch.BatchResult batch = HeadlessBotVsBotBatch.runBatch(cfg);
        System.out.println("=== WC96 ANH Beginner vs Beginner + replay ===");
        System.out.println(batch);
        assertEquals("WC96 game(s) should finish", cfg.games, batch.finishedCount);
        assertTrue("replay games written", batch.replayGames >= 1);

        HeadlessBotVsBotBatch.GameRow row = batch.rows.get(0);
        assertTrue(row.result != null && row.result.finished);
        assertTrue(!row.result.replayFiles.isEmpty());
        for (Map.Entry<String, Path> e : row.result.replayFiles.entrySet()) {
            assertTrue(Files.isRegularFile(e.getValue()));
            long sz = Files.size(e.getValue());
            System.out.println("WC96 replay " + e.getKey() + " -> " + e.getValue() + " bytes=" + sz);
            assertTrue("xml.gz should be substantial", sz > 500);
            String id = row.result.recordingIds.get(e.getKey());
            System.out.println("  open locally: game.html?replayId=" + e.getKey() + "$" + id);
            System.out.println("  drop file under <application.root>/replays/" + e.getKey() + "/"
                    + id + ".xml.gz");
        }
        assertTrue(row.result.replayMetaPath != null && Files.isRegularFile(row.result.replayMetaPath));
        System.out.println("meta.json: " + row.result.replayMetaPath);
        System.out.println("winner=" + row.winner + " darkDeck=" + row.result.darkDeckName
                + " lightDeck=" + row.result.lightDeckName + " format=" + row.result.formatName);
    }

    private static HeadlessBotVsBotRunner.AiSkill parseAi(String name) {
        return HeadlessBotVsBotBatch.parseAi(name);
    }
}
