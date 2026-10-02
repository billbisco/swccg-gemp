package com.gempukku.swccgo.ai.features;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.GameStats;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Cheapest correct GEMP hooks for InformationSetTracker: destiny draws and interrupts.
 *
 * <p>Attaches via {@link com.gempukku.swccgo.game.SwccgGame#addGameStateListener}. Destiny
 * draws are public to both seats; opponent-owned draws/interrupts also land in
 * {@code opponentRevealed}. Never encodes unrevealed opponent hand/deck identities.
 *
 * <p>Not hooked here (documented as extractionGaps): examine-from-hand, sabacc reveals,
 * reserve-deck shuffle notifications, Used/Lost pile identity scrapes beyond destiny.
 */
public final class InformationSetGameStateListener implements GameStateListener {

    private final InformationSetTracker tracker;
    private int destinyEvents;
    private int interruptEvents;
    private int opponentRevealEvents;

    public InformationSetGameStateListener(InformationSetTracker tracker) {
        this.tracker = Objects.requireNonNull(tracker, "tracker");
        this.tracker.markEventsHooked();
    }

    public InformationSetTracker getTracker() {
        return tracker;
    }

    public int getDestinyEvents() {
        return destinyEvents;
    }

    public int getInterruptEvents() {
        return interruptEvents;
    }

    public int getOpponentRevealEvents() {
        return opponentRevealEvents;
    }

    @Override
    public String getPlayerId() {
        return tracker.getPlayerId();
    }

    @Override
    public void destinyDrawn(PhysicalCard card, GameState gameState, String destinyText) {
        if (card == null) {
            return;
        }
        Map<String, Object> row = cardRow(card, "DESTINY", destinyText);
        if (row == null) {
            return;
        }
        destinyEvents++;
        tracker.recordSeen(row);
        tracker.recordDestinyRecycle(row);
        if (isOpponentOwned(card)) {
            opponentRevealEvents++;
            tracker.recordOpponentRevealed(row);
        }
    }

    @Override
    public void interruptPlayed(PhysicalCard card, GameState gameState) {
        if (card == null) {
            return;
        }
        Map<String, Object> row = cardRow(card, "INTERRUPT_PLAYED", null);
        if (row == null) {
            return;
        }
        interruptEvents++;
        tracker.recordSeen(row);
        if (isOpponentOwned(card)) {
            opponentRevealEvents++;
            tracker.recordOpponentRevealed(row);
        }
    }

    private boolean isOpponentOwned(PhysicalCard card) {
        String owner = card.getOwner();
        return owner != null && !owner.equals(tracker.getPlayerId());
    }

    private static Map<String, Object> cardRow(PhysicalCard card, String how, String destinyText) {
        String blueprintId;
        try {
            blueprintId = card.getBlueprintId(true);
        } catch (RuntimeException ex) {
            blueprintId = null;
        }
        if (blueprintId == null || blueprintId.isEmpty()) {
            return null;
        }
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("blueprintId", blueprintId);
        row.put("how", how);
        String title = card.getTitle();
        if (title != null) {
            row.put("title", title);
        }
        String owner = card.getOwner();
        if (owner != null) {
            row.put("owner", owner);
        }
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint != null) {
            CardCategory category = blueprint.getCardCategory();
            if (category != null) {
                row.put("category", category.name());
            }
            Side side = blueprint.getSide();
            if (side != null) {
                row.put("ownerSide", side.name());
            }
            Float printed = blueprint.getDestiny();
            if (printed != null) {
                row.put("destinyValue", printed.doubleValue());
            }
        }
        try {
            Float used = card.getDestinyValueToUse();
            if (used != null) {
                row.put("destinyValue", used.doubleValue());
            }
        } catch (RuntimeException ignored) {
            // keep printed if any
        }
        if (destinyText != null && !destinyText.isEmpty()) {
            row.put("destinyText", destinyText);
        }
        Zone zone = card.getZone();
        if (zone != null) {
            row.put("zone", zone.name());
        }
        row.put("recycleHint", "IN_USED_UNTIL_SHUFFLE");
        return row;
    }

    // --- unused GameStateListener methods (required by interface) ---

    @Override
    public void cardCreated(PhysicalCard card, GameState gameState, boolean restoreSnapshot) {
        // Initial state dump and in-play creates are covered by InformationSetEncoder.publicInPlay.
        // Do not scrape hand identities here (own hand comes from GameState; opponent hand is private).
    }

    @Override
    public void cardReplaced(PhysicalCard card, GameState gameState) {
    }

    @Override
    public void locationsRemoved(Collection<Integer> locationIndexes) {
    }

    @Override
    public void cardMoved(PhysicalCard card, GameState gameState) {
    }

    @Override
    public void cardRotated(PhysicalCard card, GameState gameState) {
    }

    @Override
    public void cardFlipped(PhysicalCard card, GameState gameState) {
    }

    @Override
    public void cardTurnedOver(PhysicalCard card, GameState gameState) {
    }

    @Override
    public void cardsRemoved(String playerPerforming, Collection<PhysicalCard> cards) {
    }

    @Override
    public void setPlayerOrder(List<String> playerIds) {
    }

    @Override
    public void startBattle(PhysicalCard location, Collection<PhysicalCard> cards) {
    }

    @Override
    public void addToBattle(PhysicalCard card, GameState gameState) {
    }

    @Override
    public void removeFromBattle(PhysicalCard card, GameState gameState) {
    }

    @Override
    public void finishBattle() {
    }

    @Override
    public void startAttack(PhysicalCard location, String playerAttacking, String playerDefending,
                            Collection<PhysicalCard> attackingCards, Collection<PhysicalCard> defendingCards) {
    }

    @Override
    public void finishAttack() {
    }

    @Override
    public void startDuel(PhysicalCard location, Collection<PhysicalCard> cards) {
    }

    @Override
    public void finishDuel() {
    }

    @Override
    public void startLightsaberCombat(PhysicalCard location, Collection<PhysicalCard> cards) {
    }

    @Override
    public void finishLightsaberCombat() {
    }

    @Override
    public void startSabacc() {
    }

    @Override
    public void revealSabaccHands() {
    }

    @Override
    public void finishSabacc() {
    }

    @Override
    public void setCurrentPlayerId(String playerId) {
    }

    @Override
    public void setCurrentPhase(String currentPhase) {
    }

    @Override
    public void sendMessage(String message) {
        // Shuffle messages exist but parsing is fragile; leave ownReserveShuffleNotHooked.
    }

    @Override
    public void sendGameStats(GameStats gameStats) {
    }

    @Override
    public void cardAffectedByCard(String playerPerforming, PhysicalCard card,
                                   Collection<PhysicalCard> affectedCard, GameState gameState) {
        // Examine / peek effects sometimes surface here; not reliably identity-complete. Gap.
    }

    @Override
    public void cardActivated(String playerPerforming, PhysicalCard card, GameState gameState) {
    }

    @Override
    public void decisionRequired(String playerId, AwaitingDecision awaitingDecision) {
    }
}
