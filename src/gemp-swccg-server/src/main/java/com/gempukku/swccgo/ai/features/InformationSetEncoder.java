package com.gempukku.swccgo.ai.features;

import java.util.Map;

/**
 * Packs an {@link InformationSetV1} into float32[128].
 *
 * <p>This is not a {@code GameState} encoder. It does not read hands, piles, or
 * destiny from the rules engine. It also does not score actions or train a policy.
 * Normalization below is a stable stub (documented divisors), not the final
 * tactical formula set.
 */
public final class InformationSetEncoder {

    private static final String[] SCALAR_LABELS = {
            "darkTurn", "lightTurn", "deciderTurn", "opponentTurn",
            "darkLF", "lightLF", "deciderLF", "opponentLF", "lfDiff",
            "darkForceGen", "lightForceGen", "deciderForceGen", "opponentForceGen",
            "darkHandSize", "lightHandSize", "deciderHandSize", "opponentHandSize",
            "darkReserveSize", "darkForcePileSize", "darkUsedSize", "darkLostSize",
            "lightReserveSize", "lightForcePileSize", "lightUsedSize", "lightLostSize",
            "deciderReserveSize", "deciderForcePileSize", "deciderUsedSize", "deciderLostSize",
            "opponentReserveSize", "opponentForcePileSize", "opponentUsedSize", "opponentLostSize",
            "locationCount", "optionCount", "activateMin", "activateMax",
            "shufflesSinceLastSeenOwn", "ownSeenDestinyCount", "oppRevealedCount",
            "ownRemainingEstimateUnique", "ownHighDestinyRemainingEst",
            "phaseIndex", "publicInPlayCount", "comboPieceCountInHand"
    };

    private InformationSetEncoder() {
    }

    public static float[] encodePacked(InformationSetV1 set) {
        if (set == null) {
            throw new IllegalArgumentException("set required");
        }
        float[] packed = new float[FeatureLayoutV1.PACKED_DIM];
        int phase = FeatureLayoutV1.phaseIndex(set.phase);
        if (phase >= 0) {
            packed[FeatureLayoutV1.PHASE_ONE_HOT + phase] = 1f;
        }
        int decision = FeatureLayoutV1.decisionIndex(set.decisionType);
        if (decision >= 0) {
            packed[FeatureLayoutV1.DECISION_ONE_HOT + decision] = 1f;
        }
        if (SCALAR_LABELS.length != FeatureLayoutV1.SCALAR_COUNT) {
            throw new IllegalStateException("scalar label count drifted from FeatureLayoutV1");
        }
        for (int i = 0; i < SCALAR_LABELS.length; i++) {
            Float raw = set.scalars.get(SCALAR_LABELS[i]);
            packed[FeatureLayoutV1.SCALARS + i] = raw == null ? 0f : normalizeScalar(SCALAR_LABELS[i], raw);
        }
        for (int i = 0; i < FeatureLayoutV1.BOARD_COUNT; i++) {
            float count = i < set.boardCounts.length ? set.boardCounts[i] : 0f;
            packed[FeatureLayoutV1.BOARD + i] = clamp01(count / 63f);
        }
        for (int i = 0; i < FeatureLayoutV1.PROXY_COUNT; i++) {
            float value = i < set.proxies.length ? set.proxies[i] : 0f;
            packed[FeatureLayoutV1.PROXIES + i] = value;
        }
        for (int i = 0; i < FeatureLayoutV1.FLAG_COUNT; i++) {
            boolean flag = i < set.flags.length && set.flags[i];
            packed[FeatureLayoutV1.FLAGS + i] = flag ? 1f : 0f;
        }
        int[] handHist = new int[FeatureLayoutV1.HAND_HIST_COUNT];
        for (Map<String, Object> card : set.ownHand) {
            int bucket = handBucket(card.get("category"));
            handHist[bucket]++;
        }
        float handDenom = Math.max(1, set.ownHand.size());
        for (int i = 0; i < handHist.length; i++) {
            packed[FeatureLayoutV1.HAND_HIST + i] = handHist[i] / handDenom;
        }
        int[] destinyHist = new int[FeatureLayoutV1.DESTINY_HIST_COUNT];
        for (Map<String, Object> event : set.seenHistory) {
            destinyHist[destinyBucket(event.get("destinyValue"))]++;
        }
        float seenDenom = Math.max(1, set.seenHistory.size());
        for (int i = 0; i < destinyHist.length; i++) {
            packed[FeatureLayoutV1.DESTINY_HIST + i] = destinyHist[i] / seenDenom;
        }
        packed[FeatureLayoutV1.OPP_REVEALED_NORM] = clamp01(set.opponentRevealed.size() / 80f);
        Float high = set.scalars.get("ownHighDestinyRemainingEst");
        packed[FeatureLayoutV1.HIGH_DESTINY_REMAINING] = high == null ? 0f : clamp01(high / 40f);
        for (int i = FeatureLayoutV1.PAD_START; i < FeatureLayoutV1.PACKED_DIM; i++) {
            packed[i] = 0f;
        }
        System.arraycopy(packed, 0, set.packed, 0, FeatureLayoutV1.PACKED_DIM);
        return packed;
    }

    private static float normalizeScalar(String label, float raw) {
        if (label.endsWith("LF")) {
            return clamp01(raw / 255f);
        }
        if ("lfDiff".equals(label)) {
            return clamp01((raw + 255f) / 510f);
        }
        if (label.endsWith("ForceGen")) {
            return clamp01(raw / 128f);
        }
        if (label.contains("Turn") || "phaseIndex".equals(label)) {
            return clamp01(raw / 255f);
        }
        if (label.contains("Size") || label.contains("Count") || label.endsWith("Min") || label.endsWith("Max")
                || label.contains("Est") || label.endsWith("Unique") || "locationCount".equals(label)
                || "optionCount".equals(label) || "publicInPlayCount".equals(label)
                || "comboPieceCountInHand".equals(label) || "shufflesSinceLastSeenOwn".equals(label)) {
            return clamp01(raw / 80f);
        }
        return raw;
    }

    private static int handBucket(Object category) {
        if (!(category instanceof String text)) {
            return FeatureLayoutV1.HAND_HIST_COUNT - 1;
        }
        return switch (text) {
            case "CHARACTER" -> 0;
            case "STARSHIP" -> 1;
            case "VEHICLE" -> 2;
            case "WEAPON" -> 3;
            case "DEVICE" -> 4;
            case "EFFECT" -> 5;
            case "INTERRUPT" -> 6;
            case "LOCATION" -> 7;
            case "EPIC_EVENT" -> 8;
            case "JEDI_TEST" -> 9;
            case "OBJECTIVE" -> 10;
            case "DEFENSIVE_SHIELD" -> 11;
            case "CREATURE" -> 12;
            case "PODRACER" -> 13;
            case "UTINNI_EFFECT" -> 14;
            default -> 15;
        };
    }

    private static int destinyBucket(Object value) {
        if (!(value instanceof Number number)) {
            return 0;
        }
        int bucket = (int) Math.floor(number.doubleValue());
        if (bucket < 0) {
            return 0;
        }
        if (bucket >= FeatureLayoutV1.DESTINY_HIST_COUNT) {
            return FeatureLayoutV1.DESTINY_HIST_COUNT - 1;
        }
        return bucket;
    }

    private static float clamp01(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }
}
