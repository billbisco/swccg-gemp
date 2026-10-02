package com.gempukku.swccgo.ai;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

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

    private static HeadlessBotVsBotRunner.AiSkill parseAi(String name) {
        return HeadlessBotVsBotBatch.parseAi(name);
    }
}
