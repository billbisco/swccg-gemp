package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.ai.models.AdvancedAi;
import com.gempukku.swccgo.ai.models.ConfigurableHeuristicAi;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Seat names the headless batch already accepts. ADVANCED is Yoda / AdvancedAi
 * and must stay that even when the other seat has a heuristic weights file.
 */
public class HeadlessSeatSkillTest {

    @Test
    public void advancedStaysYodaAndHeuristicLoadsWeights() throws Exception {
        assertEquals(HeadlessBotVsBotRunner.AiSkill.ADVANCED,
                HeadlessBotVsBotBatch.parseAi("ADVANCED"));
        assertEquals(HeadlessBotVsBotRunner.AiSkill.HEURISTIC,
                HeadlessBotVsBotBatch.parseAi("heuristic"));

        assertTrue(HeadlessBotVsBotRunner.createAi(HeadlessBotVsBotRunner.AiSkill.ADVANCED, null)
                instanceof AdvancedAi);

        Path weights = Files.createTempFile("heuristic-seat", ".json");
        Files.write(weights, ("{\"base\":\"BEGINNER\","
                + "\"actionWeights\":[{\"k\":\"deploy\",\"w\":1}],"
                + "\"actionPenalties\":[{\"k\":\"pass\",\"w\":-1}],"
                + "\"choiceWeights\":[{\"k\":\"yes\",\"w\":1}],"
                + "\"choicePenalties\":[{\"k\":\"pass\",\"w\":-1}]}")
                .getBytes(StandardCharsets.UTF_8));

        assertTrue(HeadlessBotVsBotRunner.createAi(HeadlessBotVsBotRunner.AiSkill.HEURISTIC, weights)
                instanceof ConfigurableHeuristicAi);
        // A weights file on the ADVANCED seat must not replace Yoda. The heuristic
        // pack belongs on HEURISTIC, so one game can seat both.
        assertTrue(HeadlessBotVsBotRunner.createAi(HeadlessBotVsBotRunner.AiSkill.ADVANCED, weights)
                instanceof AdvancedAi);

        try {
            HeadlessBotVsBotRunner.createAi(HeadlessBotVsBotRunner.AiSkill.HEURISTIC, null);
            fail("HEURISTIC without a weights file should fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("HEURISTIC"));
        }
    }
}
