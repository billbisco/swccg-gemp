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

    @Test
    public void zerosPassOptionalResponsesAndDoNotRepeatMoveOrActivate() {
        LinearPolicyAi ai = LinearPolicyAi.zeros();
        AwaitingDecision embark = actionChoice("Choose Move action or Pass", "Embark");
        AwaitingDecision disembark = actionChoice("Choose Move action or Pass", "Disembark");
        assertEquals("0", ai.decide("~OzzelBot", embark, null));
        assertEquals("0", ai.decide("~OzzelBot", disembark, null));
        assertEquals("second Embark loses to pass", "", ai.decide("~OzzelBot", embark, null));
        assertEquals("second Disembark loses to pass", "", ai.decide("~OzzelBot", disembark, null));

        AwaitingDecision transfer = actionChoice("Perform a ship-docked action or Pass", "Transfer to other starship");
        assertEquals("0", ai.decide("~OzzelBot", transfer, null));
        assertEquals("second Transfer loses to pass", "", ai.decide("~OzzelBot", transfer, null));

        AwaitingDecision activate = actionChoice("Choose Activate action or Pass", "Activate Force");
        assertEquals("0", ai.decide("~OzzelBot", activate, null));
        assertEquals("second Activate Force loses to pass", "", ai.decide("~OzzelBot", activate, null));

        AwaitingDecision optional = actionChoice("Use 1 Force - Optional responses", "Cancel your Alter");
        assertEquals("optional response is passed", "", LinearPolicyAi.zeros().decide("~OzzelBot", optional, null));

        // A non-zero weight disables the soft prior, so optional windows may be answered.
        // The once-per-phase hard cap still refuses a second copy of the same move label.
        float[] nudgedW = new float[FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.DEFAULT_BAG_HASH_DIM + LinearPolicyAi.ACTION_FEAT_DIM];
        nudgedW[0] = 0.01f;
        LinearPolicyAi learned = new LinearPolicyAi(nudgedW, 0f, FeatureLayoutV1.PACKED_DIM,
                LinearPolicyAi.DEFAULT_BAG_HASH_DIM, LinearPolicyAi.ACTION_FEAT_DIM, "nudge-repeat");
        assertEquals("0", learned.decide("~OzzelBot", optional, null));
        assertEquals("0", learned.decide("~OzzelBot", embark, null));
        assertEquals("second Embark is capped even with non-zero W", "", learned.decide("~OzzelBot", embark, null));
    }

    @Test
    public void nonzeroWeightsThatPreferEmbarkDoNotTakeASecondEmbark() {
        int embarkBucket = LinearPolicyAi.bucket("embark", LinearPolicyAi.AF_TEXT_BUCKETS);
        int otherBucket = LinearPolicyAi.bucket("deploy luke", LinearPolicyAi.AF_TEXT_BUCKETS);
        assertTrue("text buckets must differ so the weight prefers Embark only", embarkBucket != otherBucket);

        float[] weights = new float[FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.DEFAULT_BAG_HASH_DIM
                + LinearPolicyAi.ACTION_FEAT_DIM];
        int actionBase = FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.DEFAULT_BAG_HASH_DIM;
        weights[actionBase + LinearPolicyAi.AF_TEXT_HASH + embarkBucket] = 1f;
        assertTrue(!LinearPolicyAi.allZero(weights));

        LinearPolicyAi ai = new LinearPolicyAi(weights, 0f, FeatureLayoutV1.PACKED_DIM,
                LinearPolicyAi.DEFAULT_BAG_HASH_DIM, LinearPolicyAi.ACTION_FEAT_DIM, "prefer-embark");
        AwaitingDecision choice = actionChoice("Choose Move action or Pass", "Embark", "Deploy Luke");
        assertEquals("learned score takes the first Embark", "0", ai.decide("~OzzelBot", choice, null));
        assertEquals("second Embark loses to the other legal action", "1", ai.decide("~OzzelBot", choice, null));
    }

    @Test
    public void packedInteractionFlipsTheChosenAction() {
        // Identical features except bit 0. That bit pairs with packed slot 0
        // (0 % INTERACT_FEAT_DIM == 0). Direct action weights alone cannot flip.
        int bit = 0;
        float[] featA = new float[LinearPolicyAi.ACTION_FEAT_DIM];
        float[] featB = new float[LinearPolicyAi.ACTION_FEAT_DIM];
        featA[bit] = 1f;
        featA[LinearPolicyAi.AF_ONES] = 1f;
        featB[LinearPolicyAi.AF_ONES] = 1f;
        float[][] actions = new float[][] {featA, featB};

        float[] p1 = new float[FeatureLayoutV1.PACKED_DIM];
        p1[bit] = 1f;
        float[] p2 = new float[FeatureLayoutV1.PACKED_DIM];
        p2[bit] = -1f;

        int width16 = FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.DEFAULT_BAG_HASH_DIM
                + LinearPolicyAi.ACTION_FEAT_DIM;
        float[] interact = new float[width16];
        interact[bit] = 1f;
        float[] bag = new float[LinearPolicyAi.DEFAULT_BAG_HASH_DIM];
        assertEquals("P1 prefers the action with the bit set",
                0, LinearPolicyAi.greedyIndex(p1, bag, actions, interact, 0f));
        assertEquals("P2 prefers the action with the bit clear",
                1, LinearPolicyAi.greedyIndex(p2, bag, actions, interact, 0f));

        int width0 = FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.ACTION_FEAT_DIM;
        float[] interactNoBag = new float[width0];
        interactNoBag[bit] = 1f;
        assertEquals(0, LinearPolicyAi.greedyIndex(p1, new float[0], actions, interactNoBag, 0f));
        assertEquals(1, LinearPolicyAi.greedyIndex(p2, new float[0], actions, interactNoBag, 0f));

        float[] directOnly = new float[width0];
        directOnly[FeatureLayoutV1.PACKED_DIM + bit] = 1f;
        assertEquals("direct weight does not depend on packed",
                0, LinearPolicyAi.greedyIndex(p1, new float[0], actions, directOnly, 0f));
        assertEquals(0, LinearPolicyAi.greedyIndex(p2, new float[0], actions, directOnly, 0f));

        assertEquals("zeros still tie to the earliest action",
                0, LinearPolicyAi.greedyIndex(p1, bag, actions, new float[width16], 0f));
    }

    @Test
    public void decisionKindsFollowAdvancedContainsAndDoNotInventScores() {
        float[] battle = LinearPolicyAi.actionFeatures("Initiate battle", "", false, 0f, 0f);
        assertEquals(1f, battle[kind("initiate battle")], 0f);
        assertEquals(1f, battle[kind("battle")], 0f);
        assertEquals(0f, battle[kind("deploy")], 0f);
        assertEquals(1f, battle[LinearPolicyAi.AF_ONES], 0f);
        assertEquals(50, LinearPolicyAi.ACTION_FEAT_DIM);
        assertEquals(26, LinearPolicyAi.AF_KIND_COUNT);
        assertEquals("force drain", LinearPolicyAi.DECISION_KIND_KEYWORDS[0]);
        assertEquals("pass", LinearPolicyAi.DECISION_KIND_KEYWORDS[18]);
        assertEquals("revert", LinearPolicyAi.DECISION_KIND_KEYWORDS[25]);
        float[] passFeat = LinearPolicyAi.actionFeatures("Pass", "", true, 0f, 0f);
        assertEquals(1f, passFeat[LinearPolicyAi.AF_PASS], 0f);
        assertEquals(1f, passFeat[kind("pass")], 0f);
    }

    @Test
    public void nonzeroWeightsCannotRepeatActivateZeroOrIntegerZero() {
        float[] weights = new float[FeatureLayoutV1.PACKED_DIM + LinearPolicyAi.DEFAULT_BAG_HASH_DIM
                + LinearPolicyAi.ACTION_FEAT_DIM];
        weights[0] = 0.01f;
        assertTrue(!LinearPolicyAi.allZero(weights));
        LinearPolicyAi ai = new LinearPolicyAi(weights, 0f, FeatureLayoutV1.PACKED_DIM,
                LinearPolicyAi.DEFAULT_BAG_HASH_DIM, LinearPolicyAi.ACTION_FEAT_DIM, "nonzero-cap");

        AwaitingDecision activate = actionChoice("Choose one", "Activate 0", "Activate 1");
        assertEquals("first activate 0 is still legal", "0", ai.decide("~AckbarBot", activate, null));
        assertEquals("second activate 0 is capped", "1", ai.decide("~AckbarBot", activate, null));

        LinearPolicyAi amounts = new LinearPolicyAi(weights.clone(), 0f, FeatureLayoutV1.PACKED_DIM,
                LinearPolicyAi.DEFAULT_BAG_HASH_DIM, LinearPolicyAi.ACTION_FEAT_DIM, "nonzero-integer");
        AwaitingDecision amount = mock(AwaitingDecision.class);
        when(amount.getDecisionType()).thenReturn(AwaitingDecisionType.INTEGER);
        when(amount.getText()).thenReturn("Choose amount of Force to activate");
        Map<String, String[]> amountParams = new LinkedHashMap<>();
        amountParams.put("min", new String[] {"0"});
        amountParams.put("max", new String[] {"1"});
        amountParams.put("defaultValue", new String[] {"0"});
        when(amount.getDecisionParameters()).thenReturn(amountParams);
        assertEquals("0", amounts.decide("~AckbarBot", amount, null));
        assertEquals("1", amounts.decide("~AckbarBot", amount, null));
    }

    private static int kind(String keyword) {
        String[] kinds = LinearPolicyAi.DECISION_KIND_KEYWORDS;
        for (int i = 0; i < kinds.length; i++) {
            if (kinds[i].equals(keyword)) {
                return LinearPolicyAi.AF_KIND + i;
            }
        }
        throw new IllegalArgumentException(keyword);
    }

    private static AwaitingDecision actionChoice(String decisionText, String... actionTexts) {
        AwaitingDecision decision = mock(AwaitingDecision.class);
        when(decision.getDecisionType()).thenReturn(AwaitingDecisionType.CARD_ACTION_CHOICE);
        when(decision.getText()).thenReturn(decisionText);
        Map<String, String[]> params = new LinkedHashMap<>();
        params.put("actionText", actionTexts);
        String[] ids = new String[actionTexts.length];
        for (int i = 0; i < actionTexts.length; i++) {
            ids[i] = Integer.toString(i + 1);
        }
        params.put("actionId", ids);
        when(decision.getDecisionParameters()).thenReturn(params);
        return decision;
    }

}
