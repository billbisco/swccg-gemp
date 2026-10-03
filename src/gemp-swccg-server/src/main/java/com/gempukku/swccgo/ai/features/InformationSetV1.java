package com.gempukku.swccgo.ai.features;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hybrid information set: dense {@code packed} plus sparse bags.
 * This object never has a field for an exact opponent decklist.
 */
public final class InformationSetV1 {

    public int schemaVersion = FeatureLayoutV1.SCHEMA_VERSION;
    public String format = "";
    public String side = "DARK";
    public String playerId = "";
    public String phase = "ACTIVATE";
    public String decisionType = "EMPTY";
    public int optionCount;
    public boolean mustChoose;
    public boolean passAvailable;
    public boolean isActivateDecision;
    public int activateMin;
    public int activateMax;
    public int seenHistoryCap = FeatureLayoutV1.SEEN_HISTORY_CAP;
    /** Always false in legal encoding; gym must not seed the opposing list. */
    public boolean seededFromExactOpponentDeck = false;

    public final Map<String, Integer> ownDeckPrior = new LinkedHashMap<>();
    public final List<Map<String, Object>> ownHand = new ArrayList<>();
    public final List<Map<String, Object>> publicInPlay = new ArrayList<>();
    /** Own Used/Lost cards whose identities are public right now (face-up pile or visible top). */
    public final List<Map<String, Object>> ownPublicPiles = new ArrayList<>();
    /**
     * Face-up cards stacked on a public card. Face-down stacks are sizes only
     * (see packed table facts), never identities.
     */
    public final List<Map<String, Object>> publicFaceUpStacks = new ArrayList<>();
    public final List<Map<String, Object>> seenHistory = new ArrayList<>();
    public final List<Map<String, Object>> destinyRecycleAggregate = new ArrayList<>();
    public final List<Map<String, Object>> opponentRevealed = new ArrayList<>();
    public final List<String> extractionGaps = new ArrayList<>();
    public final float[] packed = new float[FeatureLayoutV1.PACKED_DIM];

    /** Scalar inputs consumed by the encoder. Keys are layout labels, not JSON extras. */
    public final Map<String, Float> scalars = new LinkedHashMap<>();
    public final float[] boardCounts = new float[FeatureLayoutV1.BOARD_COUNT];
    public final float[] proxies = new float[FeatureLayoutV1.PROXY_COUNT];
    public final boolean[] flags = new boolean[FeatureLayoutV1.FLAG_COUNT];

    public Map<String, Object> toMap() {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("schemaVersion", schemaVersion);
        row.put("format", format);
        row.put("side", side);
        row.put("playerId", playerId);
        row.put("phase", phase);
        row.put("decisionType", decisionType);
        row.put("optionCount", optionCount);
        row.put("mustChoose", mustChoose);
        row.put("passAvailable", passAvailable);
        row.put("isActivateDecision", isActivateDecision);
        row.put("activateMin", activateMin);
        row.put("activateMax", activateMax);
        row.put("seenHistoryCap", seenHistoryCap);
        row.put("seededFromExactOpponentDeck", seededFromExactOpponentDeck);
        Map<String, Object> prior = new LinkedHashMap<>();
        prior.put("source", "startingDecklist");
        prior.put("blueprintMultiset", ownDeckPrior);
        row.put("deckPrior", prior);
        row.put("ownHand", ownHand);
        row.put("publicInPlay", publicInPlay);
        row.put("ownPublicPiles", ownPublicPiles);
        row.put("publicFaceUpStacks", publicFaceUpStacks);
        row.put("seenHistory", seenHistory);
        row.put("destinyRecycleAggregate", destinyRecycleAggregate);
        row.put("opponentRevealed", opponentRevealed);
        if (!extractionGaps.isEmpty()) {
            row.put("extractionGaps", extractionGaps);
        }
        List<Float> packedList = new ArrayList<>(packed.length);
        for (float v : packed) {
            packedList.add(v);
        }
        row.put("packed", packedList);
        if (row.containsKey(FeatureLayoutV1.FORBIDDEN_OPPONENT_DECK_KEY)) {
            throw new IllegalStateException("exact opponent deck leaked into InformationSetV1");
        }
        if (seededFromExactOpponentDeck) {
            throw new IllegalStateException("seededFromExactOpponentDeck must stay false");
        }
        return row;
    }
}
