package com.gempukku.swccgo.ai;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Spike: BeginnerAi vs BeginnerAi (Open 40 beginner decks) headless, no Hall/HTTP.
 *
 * Run:
 *   cd src && mvn -pl gemp-swccg-server -am -Dtest=HeadlessBotVsBotSpikeTest test
 *
 * Optional second matchup (Beginner vs Rando) is enabled via system property:
 *   -Dheadless.alsoRando=true
 */
public class HeadlessBotVsBotSpikeTest {

    @Test
    public void beginnerVsBeginner_playsToCompletionOrDocumentsStopper() throws Exception {
        HeadlessBotVsBotRunner.Config cfg = new HeadlessBotVsBotRunner.Config();
        cfg.darkAi = HeadlessBotVsBotRunner.AiSkill.BEGINNER;
        cfg.lightAi = HeadlessBotVsBotRunner.AiSkill.BEGINNER;
        cfg.verbose = Boolean.parseBoolean(System.getProperty("headless.verbose", "true"));
        cfg.maxDecisions = Integer.getInteger("headless.maxDecisions", 25_000);
        cfg.maxMillis = Long.getLong("headless.maxMillis", 180_000L);
        cfg.gameIndex = 1;

        HeadlessDecisionTraceWriter traces = null;
        if (HeadlessDecisionTraceWriter.enabledFromSystemProperties()) {
            traces = new HeadlessDecisionTraceWriter(HeadlessDecisionTraceWriter.pathFromSystemProperties());
            cfg.traceWriter = traces;
        }

        HeadlessBotVsBotRunner.Result result;
        try {
            result = HeadlessBotVsBotRunner.playOneGame(cfg);
        } finally {
            if (traces != null) {
                traces.close();
                System.out.println("[spike] traces: " + traces);
            }
        }
        System.out.println("=== Beginner vs Beginner ===");
        System.out.println(result);
        if (!result.failureNotes.isEmpty()) {
            System.out.println("Failure notes (" + result.failureNotes.size() + "):");
            int shown = 0;
            for (String note : result.failureNotes) {
                System.out.println("  - " + note);
                if (++shown >= 20) {
                    System.out.println("  ... truncated");
                    break;
                }
            }
        }

        assertNotNull("stopper should be set", result.stopper);
        // Success path: game finished with a winner.
        // Soft-fail path: document blocker clearly rather than hang forever.
        if (result.finished) {
            assertNotNull("finished game should have a winner", result.winner);
            assertTrue("decisionCount should be > 0", result.decisionCount > 0);
        } else {
            fail("Headless bot-vs-bot did not finish. stopper=" + result.stopper
                    + " decisions=" + result.decisionCount
                    + " invalid=" + result.invalidAnswerCount
                    + " dsTurn=" + result.darkTurnNumber
                    + " lsTurn=" + result.lightTurnNumber
                    + " phase=" + result.lastPhase
                    + " dsLF=" + result.darkLifeForce
                    + " lsLF=" + result.lightLifeForce
                    + " failures=" + result.failureNotes.size()
                    + " (see stdout / docs/headless-bot-vs-bot-NOTES.md)");
        }
    }

    @Test
    public void beginnerVsRando_optional() {
        if (!Boolean.parseBoolean(System.getProperty("headless.alsoRando", "false"))) {
            System.out.println("Skipping Beginner vs Rando (pass -Dheadless.alsoRando=true to enable)");
            return;
        }

        HeadlessBotVsBotRunner.Config cfg = new HeadlessBotVsBotRunner.Config();
        cfg.darkAi = HeadlessBotVsBotRunner.AiSkill.BEGINNER;
        cfg.lightAi = HeadlessBotVsBotRunner.AiSkill.RANDO;
        cfg.verbose = Boolean.parseBoolean(System.getProperty("headless.verbose", "true"));
        cfg.maxDecisions = Integer.getInteger("headless.maxDecisions", 25_000);
        cfg.maxMillis = Long.getLong("headless.maxMillis", 180_000L);

        HeadlessBotVsBotRunner.Result result = HeadlessBotVsBotRunner.playOneGame(cfg);
        System.out.println("=== Beginner vs Rando ===");
        System.out.println(result);
        assertTrue("optional rando matchup should finish", result.finished);
        assertNotNull(result.winner);
    }
}
