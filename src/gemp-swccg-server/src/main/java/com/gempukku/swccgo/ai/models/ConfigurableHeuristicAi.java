package com.gempukku.swccgo.ai.models;

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
import java.util.List;

/**
 * Heuristic AI whose {@link KeywordWeight} tables come from a heuristic.v1 {@code weights.json}
 * (or an equivalent in-memory payload). Used by headless gym eval / champ packs without
 * redeploying hardcoded {@link BeginnerAi} / {@link AdvancedAi} arrays.
 */
public class ConfigurableHeuristicAi extends HeuristicAiBase {

    private final KeywordWeight[] actionWeights;
    private final KeywordWeight[] actionPenalties;
    private final KeywordWeight[] choiceWeights;
    private final KeywordWeight[] choicePenalties;
    private final String[] cardHints;
    private final String base;
    private final String sourceLabel;

    public ConfigurableHeuristicAi(Path weightsJson) throws IOException {
        this(readPayload(weightsJson), weightsJson.toString());
    }

    public ConfigurableHeuristicAi(WeightsPayload payload, String sourceLabel) {
        if (payload == null) {
            throw new IllegalArgumentException("weights payload required");
        }
        this.base = payload.base != null ? payload.base : "BEGINNER";
        this.actionWeights = toArray(payload.actionWeights, "actionWeights");
        this.actionPenalties = toArray(payload.actionPenalties, "actionPenalties");
        this.choiceWeights = toArray(payload.choiceWeights, "choiceWeights");
        this.choicePenalties = toArray(payload.choicePenalties, "choicePenalties");
        this.cardHints = payload.cardHints != null && payload.cardHints.length > 0
                ? payload.cardHints.clone()
                : new String[]{"character", "weapon", "effect", "interrupt", "location"};
        this.sourceLabel = sourceLabel != null ? sourceLabel : "in-memory";
    }

    public String getBase() {
        return base;
    }

    public String getSourceLabel() {
        return sourceLabel;
    }

    @Override
    protected KeywordWeight[] getActionWeights() {
        return actionWeights;
    }

    @Override
    protected KeywordWeight[] getActionPenalties() {
        return actionPenalties;
    }

    @Override
    protected KeywordWeight[] getChoiceWeights() {
        return choiceWeights;
    }

    @Override
    protected KeywordWeight[] getChoicePenalties() {
        return choicePenalties;
    }

    @Override
    protected String[] getCardHints() {
        return cardHints;
    }

    public static WeightsPayload readPayload(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            return fromJson(root);
        }
    }

    public static WeightsPayload fromJson(JsonObject root) {
        WeightsPayload p = new WeightsPayload();
        if (root.has("base") && !root.get("base").isJsonNull()) {
            p.base = root.get("base").getAsString();
        }
        p.actionWeights = parseKwList(root.getAsJsonArray("actionWeights"));
        p.actionPenalties = parseKwList(root.getAsJsonArray("actionPenalties"));
        p.choiceWeights = parseKwList(root.getAsJsonArray("choiceWeights"));
        p.choicePenalties = parseKwList(root.getAsJsonArray("choicePenalties"));
        if (root.has("cardHints") && root.get("cardHints").isJsonArray()) {
            JsonArray arr = root.getAsJsonArray("cardHints");
            List<String> hints = new ArrayList<>(arr.size());
            for (JsonElement el : arr) {
                if (el != null && el.isJsonPrimitive()) {
                    hints.add(el.getAsString());
                }
            }
            p.cardHints = hints.toArray(new String[0]);
        }
        return p;
    }

    /** BeginnerAi baseline as a mutable payload (for trainer mutators). */
    public static WeightsPayload beginnerBaseline() {
        // Keep in sync with BeginnerAi.java / trainer export BEGINNER_WEIGHTS.
        WeightsPayload p = new WeightsPayload();
        p.base = "BEGINNER";
        p.actionWeights = java.util.Arrays.asList(
                kw("force drain", 120), kw("initiate battle", 110), kw("battle", 70),
                kw("weapon", 40), kw("fire", 35), kw("deploy", 60), kw("play", 35),
                kw("move", 35), kw("activate", 50), kw("retrieve", 30), kw("draw", 25),
                kw("steal", 25), kw("capture", 25), kw("react", 20), kw("take into hand", 30));
        p.actionPenalties = java.util.Arrays.asList(
                kw("pass", -120), kw("forfeit", -80), kw("lose", -45),
                kw("place in lost pile", -70), kw("place in used pile", -35),
                kw("return to hand", -30), kw("sacrifice", -80), kw("revert", -40));
        p.choiceWeights = java.util.Arrays.asList(
                kw("draw", 40), kw("retrieve", 35), kw("deploy", 30),
                kw("battle destiny", 35), kw("weapon destiny", 35), kw("activate", 30),
                kw("force drain", 40), kw("initiate", 30), kw("capture", 20),
                kw("steal", 20), kw("use", 5), kw("yes", 5));
        p.choicePenalties = java.util.Arrays.asList(
                kw("lose", -45), kw("forfeit", -60), kw("lost pile", -50),
                kw("used pile", -30), kw("return to hand", -25), kw("neither", -25),
                kw("cancel", -20), kw("pass", -30));
        p.cardHints = new String[] {
                "pilot", "weapon", "character", "starship", "vehicle", "droid", "alien",
                "jedi", "sith", "effect", "interrupt", "location", "site", "system"
        };
        return p;
    }

    private static Kw kw(String k, double w) {
        Kw x = new Kw();
        x.k = k;
        x.w = w;
        return x;
    }

    private static KeywordWeight[] toArray(List<Kw> list, String field) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException(field + " must be non-empty");
        }
        KeywordWeight[] out = new KeywordWeight[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Kw kw = list.get(i);
            out[i] = new KeywordWeight(kw.k, (int) Math.round(kw.w));
        }
        return out;
    }

    private static List<Kw> parseKwList(JsonArray arr) {
        List<Kw> list = new ArrayList<>();
        if (arr == null) {
            return list;
        }
        for (JsonElement el : arr) {
            if (el == null || !el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            Kw kw = new Kw();
            kw.k = o.get("k").getAsString();
            kw.w = o.get("w").getAsDouble();
            list.add(kw);
        }
        return list;
    }

    public static final class Kw {
        public String k;
        public double w;
    }

    public static final class WeightsPayload {
        public String base = "BEGINNER";
        public List<Kw> actionWeights = new ArrayList<>();
        public List<Kw> actionPenalties = new ArrayList<>();
        public List<Kw> choiceWeights = new ArrayList<>();
        public List<Kw> choicePenalties = new ArrayList<>();
        public String[] cardHints = new String[0];
    }

}
