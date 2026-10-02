package com.gempukku.swccgo.ai.features;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Builds and packs {@link InformationSetV1} from a live {@link GameState}.
 *
 * <p>Legal information only: own hand identities, both life-force and pile <em>sizes</em>,
 * public in-play cards, and tracker bags. Never encodes opponent hand blueprint ids/titles
 * or Reserve/Used order. Never accepts an exact opponent decklist.
 *
 * <p>Gaps for this slice (documented on {@link InformationSetV1#extractionGaps}):
 * <ul>
 *   <li>Own reserve shuffle is not notified on {@code GameStateListener}.</li>
 *   <li>Face-down Used-pile bodies are not identities (only the public top, or the whole pile when turned face up).</li>
 *   <li>Reserve order stays unknown. Proxies stay stubbed at zero.</li>
 * </ul>
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

    /** Soft cap so FEATURES JSONL lines stay bounded. */
    private static final int PUBLIC_IN_PLAY_CAP = 96;

    private InformationSetEncoder() {
    }

    /**
     * Walk {@code gameState} for the deciding seat and pack float32[128].
     *
     * @param tracker optional match-scoped memory; when null, bags for seen/destiny/reveals stay empty
     */
    public static InformationSetV1 from(GameState gameState, String playerId, AwaitingDecision decision,
                                        InformationSetTracker tracker, String format) {
        if (gameState == null) {
            throw new IllegalArgumentException("gameState required");
        }
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("playerId required");
        }

        InformationSetV1 set = new InformationSetV1();
        set.format = format != null ? format : "";
        set.playerId = playerId;
        set.seededFromExactOpponentDeck = false;

        Side side = safeSide(gameState, playerId);
        set.side = side != null ? side.name() : "DARK";

        Phase phase = gameState.getCurrentPhase();
        set.phase = phase != null ? phase.name() : "BETWEEN_TURNS";

        if (tracker != null) {
            if (!playerId.equals(tracker.getPlayerId())) {
                throw new IllegalArgumentException("tracker playerId mismatch: tracker="
                        + tracker.getPlayerId() + " decider=" + playerId);
            }
            tracker.copyInto(set);
        }

        String darkId = gameState.getDarkPlayer();
        String lightId = gameState.getLightPlayer();
        String opponentId = gameState.getOpponent(playerId);
        boolean deciderIsDark = darkId != null && darkId.equals(playerId);

        fillDecisionFields(set, decision);
        fillOwnHand(set, gameState, playerId);
        fillPublicInPlayAndBoard(set, gameState, playerId, opponentId);
        fillOwnPublicPiles(set, gameState, playerId);
        fillScalars(set, gameState, darkId, lightId, playerId, opponentId, deciderIsDark, tracker);
        encodePacked(set);
        return set;
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

    private static void fillDecisionFields(InformationSetV1 set, AwaitingDecision decision) {
        if (decision == null) {
            set.decisionType = "EMPTY";
            return;
        }
        AwaitingDecisionType type = decision.getDecisionType();
        set.decisionType = type != null ? type.name() : "EMPTY";
        String text = decision.getText() != null ? decision.getText() : "";
        String textLower = text.toLowerCase(Locale.ROOT);
        Map<String, String[]> params = decision.getDecisionParameters();

        int optionCount = 0;
        if (params != null) {
            optionCount = maxAlignedOptionCount(params);
            String[] noPass = params.get("noPass");
            boolean must = noPass != null && noPass.length > 0 && Boolean.parseBoolean(noPass[0]);
            set.mustChoose = must;
            set.passAvailable = !must;
            if (type == AwaitingDecisionType.INTEGER) {
                set.activateMin = parseIntParam(params.get("min"), 0);
                set.activateMax = parseIntParam(params.get("max"), 0);
            }
        }
        set.optionCount = optionCount;
        set.isActivateDecision = type == AwaitingDecisionType.INTEGER
                && (textLower.contains("activate") || textLower.contains("force"));

        set.flags[5] = set.mustChoose;
        set.flags[6] = set.passAvailable;
        set.flags[7] = set.isActivateDecision;
    }

    private static void fillOwnHand(InformationSetV1 set, GameState gameState, String playerId) {
        List<PhysicalCard> hand;
        try {
            hand = gameState.getHand(playerId);
        } catch (RuntimeException ex) {
            return;
        }
        if (hand == null) {
            return;
        }
        List<Map<String, Object>> rows = new ArrayList<>(hand.size());
        for (PhysicalCard card : hand) {
            Map<String, Object> row = cardBag(card, playerId, true);
            if (row != null) {
                rows.add(row);
            }
        }
        rows.sort(Comparator.comparing(r -> String.valueOf(r.getOrDefault("blueprintId", ""))));
        set.ownHand.clear();
        set.ownHand.addAll(rows);
    }

    private static void fillPublicInPlayAndBoard(InformationSetV1 set, GameState gameState,
                                                 String playerId, String opponentId) {
        float[] board = set.boardCounts;
        List<Map<String, Object>> inPlay = new ArrayList<>();
        List<PhysicalCard> all;
        try {
            all = gameState.getAllPermanentCards();
        } catch (RuntimeException ex) {
            return;
        }
        if (all == null) {
            return;
        }
        for (PhysicalCard card : all) {
            if (card == null) {
                continue;
            }
            Zone zone = card.getZone();
            if (zone == null || !zone.isInPlay()) {
                continue;
            }
            SwccgCardBlueprint blueprint = card.getBlueprint();
            if (blueprint == null) {
                continue;
            }
            CardCategory category = blueprint.getCardCategory();
            String owner = card.getOwner();
            boolean deciderOwned = playerId.equals(owner);
            boolean opponentOwned = opponentId != null && opponentId.equals(owner);
            bumpBoard(board, category, deciderOwned, opponentOwned);

            if (inPlay.size() < PUBLIC_IN_PLAY_CAP) {
                Map<String, Object> row = cardBag(card, playerId, false);
                if (row != null) {
                    row.put("zone", zone.name());
                    inPlay.add(row);
                }
            }
        }
        inPlay.sort(Comparator.comparing(r -> String.valueOf(r.getOrDefault("blueprintId", ""))));
        set.publicInPlay.clear();
        set.publicInPlay.addAll(inPlay);

        try {
            List<PhysicalCard> locations = gameState.getLocationsInOrder();
            if (locations != null) {
                set.scalars.put("locationCount", (float) locations.size());
            }
        } catch (RuntimeException ignored) {
            // leave unset
        }
    }

    /**
     * Public own Used/Lost identities. Lost is face up unless turned over; Used is face down
     * except the top card, unless the used piles have been turned face up.
     * Opponent piles are never copied (no hand, no face-down bodies).
     */
    private static void fillOwnPublicPiles(InformationSetV1 set, GameState gameState, String playerId) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (PhysicalCard card : publicOwnPileCards(gameState, playerId)) {
            Map<String, Object> row = cardBag(card, playerId, false);
            if (row == null) {
                continue;
            }
            Zone zone = card.getZone();
            row.put("zone", zone != null ? zone.name() : "PILE");
            row.put("publicPile", Boolean.TRUE);
            rows.add(row);
        }
        rows.sort(Comparator.comparing(r -> String.valueOf(r.getOrDefault("blueprintId", ""))));
        set.ownPublicPiles.clear();
        set.ownPublicPiles.addAll(rows);
    }

    private static List<PhysicalCard> publicOwnPileCards(GameState gameState, String playerId) {
        List<PhysicalCard> out = new ArrayList<>();
        if (playerId == null) {
            return out;
        }
        try {
            boolean usedFaceUp = gameState.isUsedPilesTurnedOver();
            addPublicPileCards(out, gameState.getUsedPile(playerId), usedFaceUp);
            boolean lostFaceUp = !gameState.isLostPileTurnedOver(playerId);
            addPublicPileCards(out, gameState.getLostPile(playerId), lostFaceUp);
        } catch (RuntimeException ignored) {
            // leave whatever was collected
        }
        return out;
    }

    private static void addPublicPileCards(List<PhysicalCard> out, List<PhysicalCard> pile, boolean wholePilePublic) {
        if (pile == null || pile.isEmpty()) {
            return;
        }
        if (wholePilePublic) {
            for (PhysicalCard card : pile) {
                if (card != null) {
                    out.add(card);
                }
            }
            return;
        }
        PhysicalCard top = pile.get(0);
        if (top != null) {
            out.add(top);
        }
    }

    private static void fillScalars(InformationSetV1 set, GameState gameState,
                                    String darkId, String lightId, String playerId, String opponentId,
                                    boolean deciderIsDark, InformationSetTracker tracker) {
        int darkTurn = safeTurn(gameState, darkId);
        int lightTurn = safeTurn(gameState, lightId);
        int darkLF = safeLF(gameState, darkId);
        int lightLF = safeLF(gameState, lightId);
        int darkHand = safeHandSizeOnly(gameState, darkId);
        int lightHand = safeHandSizeOnly(gameState, lightId);
        int darkReserve = safeReserve(gameState, darkId);
        int lightReserve = safeReserve(gameState, lightId);
        int darkForce = safeForcePile(gameState, darkId);
        int lightForce = safeForcePile(gameState, lightId);
        int darkUsed = safePileSize(gameState, darkId, true);
        int lightUsed = safePileSize(gameState, lightId, true);
        int darkLost = safePileSize(gameState, darkId, false);
        int lightLost = safePileSize(gameState, lightId, false);
        float darkGen = safeForceGen(gameState, darkId);
        float lightGen = safeForceGen(gameState, lightId);

        int deciderTurn = deciderIsDark ? darkTurn : lightTurn;
        int opponentTurn = deciderIsDark ? lightTurn : darkTurn;
        int deciderLF = deciderIsDark ? darkLF : lightLF;
        int opponentLF = deciderIsDark ? lightLF : darkLF;
        int deciderHand = deciderIsDark ? darkHand : lightHand;
        int opponentHand = deciderIsDark ? lightHand : darkHand;
        int deciderReserve = deciderIsDark ? darkReserve : lightReserve;
        int opponentReserve = deciderIsDark ? lightReserve : darkReserve;
        int deciderForce = deciderIsDark ? darkForce : lightForce;
        int opponentForce = deciderIsDark ? lightForce : darkForce;
        int deciderUsed = deciderIsDark ? darkUsed : lightUsed;
        int opponentUsed = deciderIsDark ? lightUsed : darkUsed;
        int deciderLost = deciderIsDark ? darkLost : lightLost;
        int opponentLost = deciderIsDark ? lightLost : darkLost;
        float deciderGen = deciderIsDark ? darkGen : lightGen;
        float opponentGen = deciderIsDark ? lightGen : darkGen;

        put(set, "darkTurn", darkTurn);
        put(set, "lightTurn", lightTurn);
        put(set, "deciderTurn", deciderTurn);
        put(set, "opponentTurn", opponentTurn);
        put(set, "darkLF", darkLF);
        put(set, "lightLF", lightLF);
        put(set, "deciderLF", deciderLF);
        put(set, "opponentLF", opponentLF);
        put(set, "lfDiff", deciderLF - opponentLF);
        put(set, "darkForceGen", darkGen);
        put(set, "lightForceGen", lightGen);
        put(set, "deciderForceGen", deciderGen);
        put(set, "opponentForceGen", opponentGen);
        put(set, "darkHandSize", darkHand);
        put(set, "lightHandSize", lightHand);
        put(set, "deciderHandSize", deciderHand);
        put(set, "opponentHandSize", opponentHand);
        put(set, "darkReserveSize", darkReserve);
        put(set, "darkForcePileSize", darkForce);
        put(set, "darkUsedSize", darkUsed);
        put(set, "darkLostSize", darkLost);
        put(set, "lightReserveSize", lightReserve);
        put(set, "lightForcePileSize", lightForce);
        put(set, "lightUsedSize", lightUsed);
        put(set, "lightLostSize", lightLost);
        put(set, "deciderReserveSize", deciderReserve);
        put(set, "deciderForcePileSize", deciderForce);
        put(set, "deciderUsedSize", deciderUsed);
        put(set, "deciderLostSize", deciderLost);
        put(set, "opponentReserveSize", opponentReserve);
        put(set, "opponentForcePileSize", opponentForce);
        put(set, "opponentUsedSize", opponentUsed);
        put(set, "opponentLostSize", opponentLost);
        put(set, "optionCount", set.optionCount);
        put(set, "activateMin", set.activateMin);
        put(set, "activateMax", set.activateMax);
        put(set, "shufflesSinceLastSeenOwn", tracker != null ? tracker.getShufflesOwnReserve() : 0);
        put(set, "ownSeenDestinyCount", set.destinyRecycleAggregate.size());
        put(set, "oppRevealedCount", set.opponentRevealed.size());

        Map<String, Integer> remaining = estimateOwnRemaining(set);
        int remainingUnique = 0;
        for (Integer c : remaining.values()) {
            if (c != null && c > 0) {
                remainingUnique++;
            }
        }
        put(set, "ownRemainingEstimateUnique", remainingUnique);
        float highDestEst = estimateOwnHighDestinyRemaining(set, tracker, remaining);
        put(set, "ownHighDestinyRemainingEst", highDestEst);
        put(set, "phaseIndex", Math.max(0, FeatureLayoutV1.phaseIndex(set.phase)));
        put(set, "publicInPlayCount", set.publicInPlay.size());
        put(set, "comboPieceCountInHand", 0);

        try {
            set.flags[0] = gameState.isDuringForceDrain();
            set.flags[1] = gameState.isDuringForceDrainInitiatedBy(playerId);
            set.flags[2] = gameState.isDuringBattle();
        } catch (RuntimeException ignored) {
            // leave false
        }
        try {
            set.flags[3] = gameState.getObjectivePlayed(playerId) != null;
            if (opponentId != null) {
                set.flags[4] = gameState.getObjectivePlayed(opponentId) != null;
            }
        } catch (RuntimeException ignored) {
            // leave false
        }

        boolean hooked = tracker != null && tracker.isEventsHooked();
        if (!hooked) {
            set.extractionGaps.add("destinyListenerNotHooked");
            set.extractionGaps.add("opponentRevealedNotAutoScraped");
            set.extractionGaps.add("examinePeekNotHooked");
            set.extractionGaps.add("sabaccRevealNotHooked");
        }
        set.extractionGaps.add("ownReserveShuffleNotHooked");
        set.extractionGaps.add("proxiesUnfilled");
        set.extractionGaps.add("buriedOwnUsedIdentitiesNotPublic");
    }

    /**
     * Own remaining multiset estimate: starting prior minus known own hand, public
     * in-play owned copies, and currently public Used/Lost identities.
     * Does not subtract face-down Used-pile bodies or Reserve order.
     */
    private static Map<String, Integer> estimateOwnRemaining(InformationSetV1 set) {
        Map<String, Integer> remaining = new LinkedHashMap<>(set.ownDeckPrior);
        subtractOwnedCounts(remaining, set.ownHand);
        subtractOwnedCounts(remaining, set.publicInPlay);
        subtractOwnedCounts(remaining, set.ownPublicPiles);
        return remaining;
    }

    private static void subtractOwnedCounts(Map<String, Integer> remaining, List<Map<String, Object>> cards) {
        if (cards == null) {
            return;
        }
        for (Map<String, Object> card : cards) {
            if (card == null) {
                continue;
            }
            Object owned = card.get("isDeciderOwned");
            if (owned instanceof Boolean && !((Boolean) owned)) {
                continue;
            }
            // ownHand rows omit isDeciderOwned false; treat missing as own when from ownHand caller
            Object bp = card.get("blueprintId");
            if (!(bp instanceof String) || ((String) bp).isEmpty()) {
                continue;
            }
            Integer cur = remaining.get(bp);
            if (cur == null) {
                continue;
            }
            if (cur <= 1) {
                remaining.remove(bp);
            } else {
                remaining.put((String) bp, cur - 1);
            }
        }
    }

    private static float estimateOwnHighDestinyRemaining(InformationSetV1 set, InformationSetTracker tracker,
                                                         Map<String, Integer> remaining) {
        Map<String, Float> hints = tracker != null ? tracker.getOwnBlueprintDestinyView() : Map.of();
        if (hints.isEmpty()) {
            // Fall back: count known high destinies still visible on own hand / in-play only (lower bound 0).
            return 0f;
        }
        float high = 0f;
        for (Map.Entry<String, Integer> e : remaining.entrySet()) {
            Float dest = hints.get(e.getKey());
            if (dest != null && dest >= 5f && e.getValue() != null && e.getValue() > 0) {
                high += e.getValue();
            }
        }
        return high;
    }

    private static Map<String, Object> cardBag(PhysicalCard card, String deciderId, boolean isHand) {
        if (card == null) {
            return null;
        }
        SwccgCardBlueprint blueprint = card.getBlueprint();
        Map<String, Object> row = new LinkedHashMap<>();
        String blueprintId;
        try {
            blueprintId = card.getBlueprintId(true);
        } catch (RuntimeException ex) {
            blueprintId = null;
        }
        if (blueprintId == null || blueprintId.isEmpty()) {
            return null;
        }
        row.put("blueprintId", blueprintId);
        String title = card.getTitle();
        if (title != null) {
            row.put("title", title);
        }
        if (blueprint != null) {
            CardCategory category = blueprint.getCardCategory();
            if (category != null) {
                row.put("category", category.name());
            }
            Float destiny = blueprint.getDestiny();
            if (destiny != null) {
                row.put("destiny", destiny.doubleValue());
            }
            Side ownerSide = blueprint.getSide();
            if (ownerSide != null) {
                row.put("ownerSide", ownerSide.name());
            }
        }
        String owner = card.getOwner();
        row.put("isDeciderOwned", deciderId != null && deciderId.equals(owner));
        if (isHand) {
            row.put("zone", Zone.HAND.name());
        }
        return row;
    }

    private static void bumpBoard(float[] board, CardCategory category, boolean deciderOwned, boolean opponentOwned) {
        if (category == null || (!deciderOwned && !opponentOwned)) {
            return;
        }
        int offset;
        switch (category) {
            case CHARACTER:
                offset = 0;
                break;
            case STARSHIP:
                offset = 1;
                break;
            case VEHICLE:
                offset = 2;
                break;
            case WEAPON:
                offset = 3;
                break;
            case DEVICE:
                offset = 4;
                break;
            case EFFECT:
                offset = 5;
                break;
            case INTERRUPT:
                offset = 6;
                break;
            case LOCATION:
                offset = 7;
                break;
            default:
                return;
        }
        int base = deciderOwned ? 0 : 8;
        int idx = base + offset;
        if (idx >= 0 && idx < board.length) {
            board[idx] += 1f;
        }
    }

    private static void put(InformationSetV1 set, String key, float value) {
        set.scalars.put(key, value);
    }

    private static Side safeSide(GameState gameState, String playerId) {
        try {
            return gameState.getSide(playerId);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static int safeTurn(GameState gameState, String playerId) {
        if (playerId == null) {
            return 0;
        }
        try {
            return gameState.getPlayersLatestTurnNumber(playerId);
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    private static int safeLF(GameState gameState, String playerId) {
        if (playerId == null) {
            return 0;
        }
        try {
            return gameState.getPlayerLifeForce(playerId);
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    /**
     * Hand <em>size</em> only. Uses {@link List#size()} and never iterates card identities
     * for the opponent seat.
     */
    private static int safeHandSizeOnly(GameState gameState, String playerId) {
        if (playerId == null) {
            return 0;
        }
        try {
            List<PhysicalCard> hand = gameState.getHand(playerId);
            return hand != null ? hand.size() : 0;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    private static int safeReserve(GameState gameState, String playerId) {
        if (playerId == null) {
            return 0;
        }
        try {
            return gameState.getReserveDeckSize(playerId);
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    private static int safeForcePile(GameState gameState, String playerId) {
        if (playerId == null) {
            return 0;
        }
        try {
            return gameState.getForcePileSize(playerId);
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    private static int safePileSize(GameState gameState, String playerId, boolean used) {
        if (playerId == null) {
            return 0;
        }
        try {
            List<PhysicalCard> pile = used ? gameState.getUsedPile(playerId) : gameState.getLostPile(playerId);
            return pile != null ? pile.size() : 0;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    private static float safeForceGen(GameState gameState, String playerId) {
        if (playerId == null) {
            return 0f;
        }
        try {
            return gameState.getPlayersTotalForceGeneration(playerId);
        } catch (RuntimeException ex) {
            return 0f;
        }
    }

    private static int maxAlignedOptionCount(Map<String, String[]> params) {
        int n = 0;
        for (String key : new String[]{"actionId", "cardId", "blueprintId", "actionText", "results", "cardText"}) {
            String[] vals = params.get(key);
            if (vals != null && vals.length > n) {
                n = vals.length;
            }
        }
        return n;
    }

    private static int parseIntParam(String[] vals, int fallback) {
        if (vals == null || vals.length == 0 || vals[0] == null || vals[0].isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(vals[0].trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
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
