package com.gempukku.swccgo.ai.features;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Match-scoped memory for one seat's legal information.
 *
 * <p>Callers push events manually or via {@link InformationSetGameStateListener}.
 * There is no API that accepts the opponent's exact decklist.
 */
public final class InformationSetTracker {

    private final String playerId;
    private final int seenHistoryCap;
    private final Map<String, Integer> ownDeckPrior = new LinkedHashMap<>();
    private final Map<String, Float> ownBlueprintDestiny = new LinkedHashMap<>();
    private final Deque<Map<String, Object>> seenHistory = new ArrayDeque<>();
    private final List<Map<String, Object>> destinyRecycleAggregate = new ArrayList<>();
    private final List<Map<String, Object>> opponentRevealed = new ArrayList<>();
    private int droppedSeenEvents;
    private int shufflesOwnReserve;
    private boolean eventsHooked;
    private int decisionsThisGame;
    private boolean decisionsThisGameSet;

    public InformationSetTracker(String playerId) {
        this(playerId, FeatureLayoutV1.SEEN_HISTORY_CAP);
    }

    public InformationSetTracker(String playerId, int seenHistoryCap) {
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("playerId required");
        }
        if (seenHistoryCap < 32 || seenHistoryCap > 64) {
            throw new IllegalArgumentException("seenHistoryCap must be 32..64, got " + seenHistoryCap);
        }
        this.playerId = playerId;
        this.seenHistoryCap = seenHistoryCap;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getSeenHistoryCap() {
        return seenHistoryCap;
    }

    public int getDroppedSeenEvents() {
        return droppedSeenEvents;
    }

    public void setOwnDeckPrior(Map<String, Integer> blueprintMultiset) {
        ownDeckPrior.clear();
        if (blueprintMultiset != null) {
            ownDeckPrior.putAll(blueprintMultiset);
        }
    }

    /** Printed destiny for own prior blueprints (optional; enables remaining high-destiny estimate). */
    public void setOwnBlueprintDestinyHints(Map<String, Float> destinyByBlueprint) {
        ownBlueprintDestiny.clear();
        if (destinyByBlueprint != null) {
            ownBlueprintDestiny.putAll(destinyByBlueprint);
        }
    }

    public Map<String, Integer> getOwnDeckPriorView() {
        return Collections.unmodifiableMap(ownDeckPrior);
    }

    public Map<String, Float> getOwnBlueprintDestinyView() {
        return Collections.unmodifiableMap(ownBlueprintDestiny);
    }

    /** Set when a {@link InformationSetGameStateListener} is attached for this seat. */
    public void markEventsHooked() {
        eventsHooked = true;
    }

    public boolean isEventsHooked() {
        return eventsHooked;
    }

    /**
     * Refuses an exact opposing list. Soft metagame beliefs are a different store.
     */
    public void rejectExactOpponentDeckPrior() {
        throw new IllegalArgumentException(
                "opponentDeckPriorKnown is forbidden; use in-game reveals plus the soft metagame store");
    }

    public void recordSeen(Map<String, Object> event) {
        if (event == null) {
            return;
        }
        Map<String, Object> copy = new LinkedHashMap<>(event);
        seenHistory.addLast(copy);
        while (seenHistory.size() > seenHistoryCap) {
            seenHistory.removeFirst();
            droppedSeenEvents++;
        }
    }

    public void recordDestinyRecycle(Map<String, Object> fact) {
        if (fact != null) {
            destinyRecycleAggregate.add(new LinkedHashMap<>(fact));
        }
    }

    public void recordOpponentRevealed(Map<String, Object> card) {
        if (card != null) {
            opponentRevealed.add(new LinkedHashMap<>(card));
        }
    }

    public void noteOwnReserveShuffle() {
        shufflesOwnReserve++;
    }

    public int getShufflesOwnReserve() {
        return shufflesOwnReserve;
    }

    /**
     * Decisions already taken in this game. GameState has no such counter;
     * the headless runner is the source. {@link #hasDecisionsThisGame()}
     * stays false until that runner calls this.
     */
    public void setDecisionsThisGame(int count) {
        decisionsThisGame = Math.max(0, count);
        decisionsThisGameSet = true;
    }

    public int getDecisionsThisGame() {
        return decisionsThisGame;
    }

    public boolean hasDecisionsThisGame() {
        return decisionsThisGameSet;
    }

    public void copyInto(InformationSetV1 target) {
        target.playerId = playerId;
        target.seenHistoryCap = seenHistoryCap;
        target.ownDeckPrior.clear();
        target.ownDeckPrior.putAll(ownDeckPrior);
        target.seenHistory.clear();
        target.seenHistory.addAll(seenHistory);
        target.destinyRecycleAggregate.clear();
        target.destinyRecycleAggregate.addAll(destinyRecycleAggregate);
        target.opponentRevealed.clear();
        target.opponentRevealed.addAll(opponentRevealed);
    }

    public int seenHistorySize() {
        return seenHistory.size();
    }

    public int aggregateSize() {
        return destinyRecycleAggregate.size();
    }

    public int opponentRevealedSize() {
        return opponentRevealed.size();
    }
}
