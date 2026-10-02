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
        // Zeros prior prefers the real action over Pass. Pure greedyIndex above still ties to 0.
        assertEquals("1", LinearPolicyAi.zeros().decide("~AckbarBot", decision, null));
    }

    @Test
    public void defaultZerosDoNotReturnActivateZero() {
        AwaitingDecision decision = mock(AwaitingDecision.class);
        when(decision.getDecisionType()).thenReturn(AwaitingDecisionType.MULTIPLE_CHOICE);
        when(decision.getText()).thenReturn("Choose one");
        Map<String, String[]> params = new LinkedHashMap<>();
        params.put("results", new String[] {"Activate 0", "Activate 1", "Pass"});
        when(decision.getDecisionParameters()).thenReturn(params);

        String chosen = LinearPolicyAi.zeros().decide("~AckbarBot", decision, null);
        assertTrue("default policy must not return activate 0, got " + chosen, !"0".equals(chosen));
        assertEquals("non-zero activate beats pass and activate 0", "1", chosen);

        JsonObject omitted = new JsonObject();
        omitted.addProperty("schema", "linear.v1");
        omitted.addProperty("featureSchemaVersion", FeatureLayoutV1.SCHEMA_VERSION);
        omitted.addProperty("packedDim", FeatureLayoutV1.PACKED_DIM);
        omitted.addProperty("bagHashDim", 0);
        omitted.addProperty("actionFeatDim", LinearPolicyAi.ACTION_FEAT_DIM);
        LinearPolicyAi missing = LinearPolicyAi.fromJson(omitted, "omitted-W");
        assertEquals("1", missing.decide("~AckbarBot", decision, null));

        JsonObject explicitZeros = new JsonObject();
        explicitZeros.addProperty("schema", "linear.v1");
        explicitZeros.addProperty("featureSchemaVersion", FeatureLayoutV1.SCHEMA_VERSION);
        explicitZeros.addProperty("packedDim", FeatureLayoutV1.PACKED_DIM);
        explicitZeros.addProperty("bagHashDim", 0);
        explicitZeros.addProperty("actionFeatDim", LinearPolicyAi.ACTION_FEAT_DIM);
        JsonArray w = new JsonArray();
        int width = FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.ACTION_FEAT_DIM;
        for (int i = 0; i < width; i++) {
            w.add(0);
        }
        explicitZeros.add("W", w);
        assertEquals("1", LinearPolicyAi.fromJson(explicitZeros, "explicit-zeros").decide("~AckbarBot", decision, null));

        AwaitingDecision amount = mock(AwaitingDecision.class);
        when(amount.getDecisionType()).thenReturn(AwaitingDecisionType.INTEGER);
        when(amount.getText()).thenReturn("Choose amount of Force to activate");
        Map<String, String[]> amountParams = new LinkedHashMap<>();
        amountParams.put("min", new String[] {"0"});
        amountParams.put("max", new String[] {"1"});
        amountParams.put("defaultValue", new String[] {"0"});
        when(amount.getDecisionParameters()).thenReturn(amountParams);
        assertEquals("1", LinearPolicyAi.zeros().decide("~AckbarBot", amount, null));

        // A non-zero weight, even a tiny one, disables the prior.
        float[] nudgedW = new float[FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.DEFAULT_BAG_HASH_DIM + LinearPolicyAi.ACTION_FEAT_DIM];
        nudgedW[FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.DEFAULT_BAG_HASH_DIM + LinearPolicyAi.AF_PASS] = 0.01f;
        LinearPolicyAi nudged = new LinearPolicyAi(nudgedW, 0f, FeatureLayoutV1.PACKED_DIM,
                LinearPolicyAi.DEFAULT_BAG_HASH_DIM, LinearPolicyAi.ACTION_FEAT_DIM, "nudge");
        AwaitingDecision passOrDeploy = mock(AwaitingDecision.class);
        when(passOrDeploy.getDecisionType()).thenReturn(AwaitingDecisionType.MULTIPLE_CHOICE);
        Map<String, String[]> passParams = new LinkedHashMap<>();
        passParams.put("results", new String[] {"Pass", "Deploy Luke"});
        when(passOrDeploy.getDecisionParameters()).thenReturn(passParams);
        assertEquals("0", nudged.decide("~AckbarBot", passOrDeploy, null));
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
