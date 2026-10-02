package com.gempukku.swccgo.ai.models;

import com.gempukku.swccgo.ai.HeadlessBotVsBotRunner;
import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.ai.features.FeatureLayoutV1;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinearPolicyAiTest {

    @Test
    public void knownWeightsPreferDeployOverPass() throws Exception {
        float[] packed = new float[FeatureLayoutV1.PACKED_DIM];
        packed[FeatureLayoutV1.phaseIndex("DEPLOY")] = 1f;
        float[][] actions = new float[][] {
                LinearPolicyAi.actionFeatures("Pass", "", true, 0f, 0f),
                LinearPolicyAi.actionFeatures("Deploy Luke", "1_5", false, 0f, 1f)
        };
        int width = FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.ACTION_FEAT_DIM;
        float[] weights = new float[width];
        // Action block starts right after packed when bagHashDim is 0.
        weights[FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.AF_PASS] = -8f;
        int chosen = LinearPolicyAi.greedyIndex(packed, new float[0], actions, weights, 0f);
        assertEquals(1, chosen);

        float[] zeros = new float[width];
        assertEquals("zeros keep the first legal action",
                0, LinearPolicyAi.greedyIndex(packed, new float[0], actions, zeros, 0f));

        JsonObject root = new JsonObject();
        root.addProperty("schema", "linear.v1");
        root.addProperty("featureSchemaVersion", 1);
        root.addProperty("packedDim", FeatureLayoutV1.PACKED_DIM);
        root.addProperty("bagHashDim", 0);
        root.addProperty("actionFeatDim", LinearPolicyAi.ACTION_FEAT_DIM);
        root.addProperty("bias", 0);
        JsonArray w = new JsonArray();
        for (float value : weights) {
            w.add(value);
        }
        root.add("W", w);
        Path file = Files.createTempFile("linear-v1-", ".json");
        Files.writeString(file, root.toString());

        LinearPolicyAi loaded = LinearPolicyAi.load(file);
        assertEquals("linear.v1", loaded.policyKind());

        AwaitingDecision decision = mock(AwaitingDecision.class);
        when(decision.getDecisionType()).thenReturn(AwaitingDecisionType.MULTIPLE_CHOICE);
        when(decision.getText()).thenReturn("Choose one");
        Map<String, String[]> params = new LinkedHashMap<>();
        params.put("results", new String[] {"Pass", "Deploy Luke"});
        when(decision.getDecisionParameters()).thenReturn(params);

        assertEquals("1", loaded.decide("~AckbarBot", decision, null));
        assertEquals("0", LinearPolicyAi.zeros().decide("~AckbarBot", decision, null));
    }

    @Test
    public void headlessFactoryKeepsBeginnerAndSelectsLinear() {
        SwccgAiController beginner = HeadlessBotVsBotRunner.createAi(HeadlessBotVsBotRunner.AiSkill.BEGINNER);
        assertTrue(beginner instanceof BeginnerAi);
        SwccgAiController linear = HeadlessBotVsBotRunner.createAi(HeadlessBotVsBotRunner.AiSkill.LINEAR, null);
        assertTrue(linear instanceof LinearPolicyAi);
        assertEquals("linear.v1", ((LinearPolicyAi) linear).policyKind());
    }
}
