package com.gempukku.swccgo.ai.models;

import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.ai.features.FeatureLayoutV1;
import com.gempukku.swccgo.ai.features.InformationSetEncoder;
import com.gempukku.swccgo.ai.features.InformationSetTracker;
import com.gempukku.swccgo.ai.features.InformationSetV1;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Gym-cli linear policy over {@link InformationSetV1#packed}.
 *
 * <p>Score for action {@code a} is
 * {@code bias + dot(W, concat(packed[packedDim], bagHash[bagHashDim], actionFeat[actionFeatDim]))}.
 * Choice is greedy argmax. Ties keep the earliest legal candidate.
 *
 * <p>{@code W} all zeros is a legal stub: it plays the first legal answer.
 * This class does not train and is not distilled from AdvancedAi / YodaBot.
 * Hall / table bots must not load it.
 *
 * <p>Action features ({@value #ACTION_FEAT_DIM}):
 * <ul>
 *   <li>0 pass flag</li>
 *   <li>1 integer value normalized to [0, 1] inside the legal range</li>
 *   <li>2 index normalized across candidates of this decision</li>
 *   <li>3..18 text-hash one-hot (16 buckets)</li>
 *   <li>19..22 blueprintId hash one-hot (4 buckets)</li>
 *   <li>23 constant 1</li>
 * </ul>
 */
public final class LinearPolicyAi implements SwccgAiController {

    public static final String POLICY_KIND = "linear.v1";
    public static final int ACTION_FEAT_DIM = 24;
    public static final int DEFAULT_BAG_HASH_DIM = 16;
    public static final int AF_PASS = 0;
    public static final int AF_INTEGER = 1;
    public static final int AF_INDEX = 2;
    public static final int AF_TEXT_HASH = 3;
    public static final int AF_TEXT_BUCKETS = 16;
    public static final int AF_BP_HASH = 19;
    public static final int AF_BP_BUCKETS = 4;
    public static final int AF_ONES = 23;

    private static final int INTEGER_ENUM_CAP = 24;

    private final float[] weights;
    private final float bias;
    private final int packedDim;
    private final int bagHashDim;
    private final int actionFeatDim;
    private final String sourceLabel;
    private InformationSetTracker tracker;

    public LinearPolicyAi(float[] weights, float bias, int packedDim, int bagHashDim, int actionFeatDim,
                          String sourceLabel) {
        if (packedDim != FeatureLayoutV1.PACKED_DIM) {
            throw new IllegalArgumentException("packedDim must be " + FeatureLayoutV1.PACKED_DIM + ", got " + packedDim);
        }
        if (actionFeatDim != ACTION_FEAT_DIM) {
            throw new IllegalArgumentException("actionFeatDim must be " + ACTION_FEAT_DIM + ", got " + actionFeatDim);
        }
        if (bagHashDim < 0) {
            throw new IllegalArgumentException("bagHashDim must be >= 0");
        }
        int expected = packedDim + bagHashDim + actionFeatDim;
        if (weights == null || weights.length != expected) {
            throw new IllegalArgumentException("W length " + (weights == null ? 0 : weights.length)
                    + " != packed+bag+action " + expected);
        }
        this.weights = weights.clone();
        this.bias = bias;
        this.packedDim = packedDim;
        this.bagHashDim = bagHashDim;
        this.actionFeatDim = actionFeatDim;
        this.sourceLabel = sourceLabel != null ? sourceLabel : "in-memory";
    }

    public static LinearPolicyAi zeros() {
        int bag = DEFAULT_BAG_HASH_DIM;
        float[] w = new float[FeatureLayoutV1.PACKED_DIM + bag + ACTION_FEAT_DIM];
        return new LinearPolicyAi(w, 0f, FeatureLayoutV1.PACKED_DIM, bag, ACTION_FEAT_DIM, "zeros");
    }

    public static LinearPolicyAi load(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("linear.v1 path required");
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            return fromJson(root, path.toString());
        }
    }

    public static LinearPolicyAi fromJson(JsonObject root, String sourceLabel) {
        if (root == null) {
            throw new IllegalArgumentException("linear.v1 json required");
        }
        String schema = stringField(root, "schema", "");
        if (!POLICY_KIND.equals(schema)) {
            throw new IllegalArgumentException("expected schema " + POLICY_KIND + ", got " + schema);
        }
        int featureSchema = intField(root, "featureSchemaVersion", FeatureLayoutV1.SCHEMA_VERSION);
        if (featureSchema != FeatureLayoutV1.SCHEMA_VERSION) {
            throw new IllegalArgumentException("featureSchemaVersion " + featureSchema
                    + " != " + FeatureLayoutV1.SCHEMA_VERSION);
        }
        int packedDim = intField(root, "packedDim", FeatureLayoutV1.PACKED_DIM);
        int bagDim = intField(root, "bagHashDim", 0);
        int actionDim = intField(root, "actionFeatDim", ACTION_FEAT_DIM);
        float bias = root.has("bias") && !root.get("bias").isJsonNull()
                ? root.get("bias").getAsFloat() : 0f;
        float[] w = readWeights(root.get("W"));
        if (w.length == 0) {
            w = new float[packedDim + bagDim + actionDim];
        }
        return new LinearPolicyAi(w, bias, packedDim, bagDim, actionDim, sourceLabel);
    }

    public String policyKind() {
        return POLICY_KIND;
    }

    public String getSourceLabel() {
        return sourceLabel;
    }

    public int getBagHashDim() {
        return bagHashDim;
    }

    /** Match-scoped seen/destiny/reveal memory. Null keeps those bags empty. */
    public void setTracker(InformationSetTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public String decide(String playerId, AwaitingDecision decision, GameState gameState) {
        float[] packed = new float[packedDim];
        float[] bag = new float[bagHashDim];
        if (gameState != null && playerId != null && !playerId.isBlank()) {
            InformationSetV1 set = InformationSetEncoder.from(gameState, playerId, decision, tracker, "");
            int n = Math.min(packed.length, set.packed.length);
            System.arraycopy(set.packed, 0, packed, 0, n);
            if (bagHashDim > 0) {
                bag = bagHash(set, bagHashDim);
            }
        }
        List<Candidate> candidates = enumerate(decision);
        if (candidates.isEmpty()) {
            return "0";
        }
        float[][] feats = new float[candidates.size()][];
        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.get(i);
            feats[i] = actionFeatures(c.text, c.blueprintId, c.pass, c.integerNorm, c.indexNorm);
        }
        int best = greedyIndex(packed, bag, feats, weights, bias);
        if (best < 0 || best >= candidates.size()) {
            return candidates.get(0).raw;
        }
        return candidates.get(best).raw;
    }

    /**
     * Greedy index. Equal scores keep the lowest index.
     * {@code bagHash} may be empty when the pack is packed-only ({@code bagHashDim == 0}).
     */
    public static int greedyIndex(float[] packed, float[] bagHash, float[][] actionFeats, float[] w, float bias) {
        if (actionFeats == null || actionFeats.length == 0) {
            return -1;
        }
        int best = 0;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (int a = 0; a < actionFeats.length; a++) {
            double score = scoreOf(packed, bagHash, actionFeats[a], w, bias);
            if (score > bestScore) {
                bestScore = score;
                best = a;
            }
        }
        return best;
    }

    public static double scoreOf(float[] packed, float[] bagHash, float[] actionFeat, float[] w, float bias) {
        double score = bias;
        int p = 0;
        if (w == null) {
            return score;
        }
        if (packed != null) {
            for (float v : packed) {
                if (p >= w.length) {
                    return score;
                }
                score += w[p++] * v;
            }
        }
        if (bagHash != null) {
            for (float v : bagHash) {
                if (p >= w.length) {
                    return score;
                }
                score += w[p++] * v;
            }
        }
        if (actionFeat != null) {
            for (float v : actionFeat) {
                if (p >= w.length) {
                    return score;
                }
                score += w[p++] * v;
            }
        }
        return score;
    }

    public static float[] actionFeatures(String text, String blueprintId, boolean pass,
                                         float integerNorm, float indexNorm) {
        float[] feat = new float[ACTION_FEAT_DIM];
        feat[AF_PASS] = (pass || isPassText(text)) ? 1f : 0f;
        feat[AF_INTEGER] = integerNorm;
        feat[AF_INDEX] = indexNorm;
        String hashed = text == null ? "" : text.toLowerCase(Locale.ROOT);
        feat[AF_TEXT_HASH + bucket(hashed, AF_TEXT_BUCKETS)] = 1f;
        if (blueprintId != null && !blueprintId.isEmpty()) {
            feat[AF_BP_HASH + bucket(blueprintId, AF_BP_BUCKETS)] = 1f;
        }
        feat[AF_ONES] = 1f;
        return feat;
    }

    public static float[] bagHash(InformationSetV1 set, int dim) {
        float[] hash = new float[Math.max(0, dim)];
        if (set == null || dim <= 0) {
            return hash;
        }
        absorb(hash, set.ownHand);
        absorb(hash, set.publicInPlay);
        absorb(hash, set.ownPublicPiles);
        absorb(hash, set.opponentRevealed);
        absorb(hash, set.seenHistory);
        float max = 0f;
        for (float v : hash) {
            if (v > max) {
                max = v;
            }
        }
        if (max > 0f) {
            for (int i = 0; i < hash.length; i++) {
                hash[i] /= max;
            }
        }
        return hash;
    }

    static List<Candidate> enumerate(AwaitingDecision decision) {
        List<Candidate> out = new ArrayList<>();
        if (decision == null || decision.getDecisionType() == null) {
            out.add(new Candidate("0", "", "", false, 0f, 0f));
            return out;
        }
        Map<String, String[]> params = decision.getDecisionParameters();
        AwaitingDecisionType type = decision.getDecisionType();
        switch (type) {
            case EMPTY:
                out.add(new Candidate("pass", "Pass", "", true, 0f, 0f));
                break;
            case MULTIPLE_CHOICE:
                addIndexedTexts(out, firstArray(params, "results", "index", "choice"));
                break;
            case ACTION_CHOICE:
            case CARD_ACTION_CHOICE:
                addActions(out, params, type);
                break;
            case INTEGER:
                addIntegers(out, params);
                break;
            case CARD_SELECTION:
            case ARBITRARY_CARDS:
                addCards(out, params);
                break;
            default:
                out.add(new Candidate("0", "", "", false, 0f, 0f));
                break;
        }
        if (out.isEmpty()) {
            out.add(new Candidate("0", "", "", false, 0f, 0f));
        }
        return out;
    }

    private static void addIndexedTexts(List<Candidate> out, String[] texts) {
        if (texts == null || texts.length == 0) {
            out.add(new Candidate("0", "", "", false, 0f, 0f));
            return;
        }
        for (int i = 0; i < texts.length; i++) {
            String text = texts[i] != null ? texts[i] : "";
            out.add(new Candidate(Integer.toString(i), text, "", isPassText(text), 0f, norm(i, texts.length)));
        }
    }

    private static void addActions(List<Candidate> out, Map<String, String[]> params, AwaitingDecisionType type) {
        String[] texts = params != null ? params.get("actionText") : null;
        String[] ids = params != null ? params.get("actionId") : null;
        String[] blueprints = params != null ? params.get("blueprintId") : null;
        int n = texts != null ? texts.length : (ids != null ? ids.length : 0);
        for (int i = 0; i < n; i++) {
            String text = at(texts, i);
            out.add(new Candidate(Integer.toString(i), text, at(blueprints, i), isPassText(text), 0f, norm(i, Math.max(n, 1))));
        }
        boolean noPass = params != null && boolFirst(params.get("noPass"));
        if (type == AwaitingDecisionType.CARD_ACTION_CHOICE && !noPass) {
            out.add(new Candidate("", "Pass", "", true, 0f, 1f));
        }
        if (out.isEmpty()) {
            // ACTION_CHOICE rejects an empty result; CARD_ACTION_CHOICE treats it as pass.
            String raw = type == AwaitingDecisionType.ACTION_CHOICE ? "0" : "";
            out.add(new Candidate(raw, "Pass", "", true, 0f, 0f));
        }
    }

    private static void addIntegers(List<Candidate> out, Map<String, String[]> params) {
        int min = parseFirst(params, "min", 0);
        int max = parseFirst(params, "max", min);
        if (max < min) {
            max = min;
        }
        int fallback = parseFirst(params, "defaultValue", min);
        int[] values = integerSamples(min, max, fallback);
        for (int i = 0; i < values.length; i++) {
            int value = values[i];
            float integerNorm = max == min ? 0f : (value - min) / (float) (max - min);
            out.add(new Candidate(Integer.toString(value), "INTEGER " + value, "", false, integerNorm,
                    norm(i, values.length)));
        }
    }

    private static void addCards(List<Candidate> out, Map<String, String[]> params) {
        String[] cardIds = params != null ? params.get("cardId") : null;
        if (cardIds == null || cardIds.length == 0) {
            out.add(new Candidate("", "Pass", "", true, 0f, 0f));
            return;
        }
        String[] selectable = params.get("selectable");
        String[] texts = params.get("cardText");
        if (texts == null) {
            texts = params.get("testingText");
        }
        String[] blueprints = params.get("blueprintId");
        int min = parseFirst(params, "min", 0);
        int max = parseFirst(params, "max", cardIds.length);
        if (max < min) {
            max = min;
        }
        List<Integer> selectableIdx = new ArrayList<>();
        for (int i = 0; i < cardIds.length; i++) {
            if (isSelectable(selectable, i) && cardIds[i] != null) {
                selectableIdx.add(i);
            }
        }
        if (min == 0) {
            out.add(new Candidate("", "Pass", "", true, 0f, 0f));
        }
        if (selectableIdx.isEmpty()) {
            return;
        }
        if (min <= 1 && max >= 1) {
            for (int order = 0; order < selectableIdx.size(); order++) {
                int i = selectableIdx.get(order);
                out.add(new Candidate(cardIds[i], at(texts, i), at(blueprints, i), false, 0f,
                        norm(order, selectableIdx.size())));
            }
            return;
        }
        int take = Math.min(Math.max(min, 1), Math.min(max, selectableIdx.size()));
        StringBuilder raw = new StringBuilder();
        StringBuilder text = new StringBuilder();
        for (int k = 0; k < take; k++) {
            int i = selectableIdx.get(k);
            if (raw.length() > 0) {
                raw.append(',');
                text.append(' ');
            }
            raw.append(cardIds[i]);
            text.append(at(texts, i));
        }
        out.add(new Candidate(raw.toString(), text.toString(), "", false, 0f, 0f));
    }

    static int[] integerSamples(int min, int max, int preferred) {
        int span = max - min;
        if (span <= INTEGER_ENUM_CAP) {
            int[] all = new int[span + 1];
            for (int i = 0; i <= span; i++) {
                all[i] = min + i;
            }
            return all;
        }
        LinkedHashSet<Integer> values = new LinkedHashSet<>();
        values.add(min);
        values.add(max);
        if (preferred < min) {
            preferred = min;
        } else if (preferred > max) {
            preferred = max;
        }
        values.add(preferred);
        for (int k = 1; k <= 6; k++) {
            values.add(min + (int) Math.round(span * (k / 7.0)));
        }
        int[] out = new int[values.size()];
        int i = 0;
        for (int value : values) {
            out[i++] = value;
        }
        return out;
    }

    static boolean isPassText(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        String t = text.toLowerCase(Locale.ROOT).trim();
        return t.equals("pass") || t.startsWith("pass ") || t.endsWith(" pass") || t.contains(" pass ");
    }

    static int bucket(String value, int buckets) {
        if (buckets <= 1) {
            return 0;
        }
        int hash = value == null ? 0 : value.hashCode();
        int mod = hash % buckets;
        return mod < 0 ? mod + buckets : mod;
    }

    private static void absorb(float[] hash, List<Map<String, Object>> bag) {
        if (bag == null) {
            return;
        }
        for (Map<String, Object> row : bag) {
            if (row == null) {
                continue;
            }
            Object id = row.get("blueprintId");
            if (id == null) {
                id = row.get("title");
            }
            if (id == null) {
                continue;
            }
            hash[bucket(String.valueOf(id), hash.length)] += 1f;
        }
    }

    private static float[] readWeights(JsonElement element) {
        if (element == null || element.isJsonNull() || !element.isJsonArray()) {
            return new float[0];
        }
        JsonArray arr = element.getAsJsonArray();
        float[] w = new float[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            JsonElement el = arr.get(i);
            w[i] = el == null || el.isJsonNull() ? 0f : el.getAsFloat();
        }
        return w;
    }

    private static String stringField(JsonObject root, String key, String fallback) {
        if (!root.has(key) || root.get(key).isJsonNull()) {
            return fallback;
        }
        return root.get(key).getAsString();
    }

    private static int intField(JsonObject root, String key, int fallback) {
        if (!root.has(key) || root.get(key).isJsonNull()) {
            return fallback;
        }
        return root.get(key).getAsInt();
    }

    private static String[] firstArray(Map<String, String[]> params, String... keys) {
        if (params == null) {
            return null;
        }
        for (String key : keys) {
            String[] value = params.get(key);
            if (value != null && value.length > 0) {
                return value;
            }
        }
        return null;
    }

    private static int parseFirst(Map<String, String[]> params, String key, int fallback) {
        if (params == null) {
            return fallback;
        }
        String[] value = params.get(key);
        if (value == null || value.length == 0 || value[0] == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value[0]);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static boolean boolFirst(String[] value) {
        return value != null && value.length > 0 && Boolean.parseBoolean(value[0]);
    }

    private static boolean isSelectable(String[] selectable, int index) {
        if (selectable == null || index < 0 || index >= selectable.length || selectable[index] == null) {
            return true;
        }
        return Boolean.parseBoolean(selectable[index]);
    }

    private static String at(String[] values, int index) {
        if (values == null || index < 0 || index >= values.length || values[index] == null) {
            return "";
        }
        return values[index];
    }

    private static float norm(int index, int count) {
        if (count <= 1) {
            return 0f;
        }
        return index / (float) (count - 1);
    }

    static final class Candidate {
        final String raw;
        final String text;
        final String blueprintId;
        final boolean pass;
        final float integerNorm;
        final float indexNorm;

        Candidate(String raw, String text, String blueprintId, boolean pass, float integerNorm, float indexNorm) {
            this.raw = raw != null ? raw : "";
            this.text = text != null ? text : "";
            this.blueprintId = blueprintId != null ? blueprintId : "";
            this.pass = pass;
            this.integerNorm = integerNorm;
            this.indexNorm = indexNorm;
        }
    }
}
