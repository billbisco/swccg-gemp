package com.gempukku.swccgo.ai.features;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.GameStats;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * GEMP hooks for InformationSetTracker: destiny, interrupts, and reveals this seat
 * was actually shown.
 *
 * <p>Attaches via {@link com.gempukku.swccgo.game.SwccgGame#addGameStateListener}. Opponent
 * examine/peek decisions are recorded only when {@code decisionRequired}'s player id is
 * this seat (the cards were shown here). Public sabacc reveals and a face-up top of the
 * opponent reserve are recorded for both seats. Unrevealed opponent hand identities are
 * never scraped from {@code cardCreated} or from the other seat's decisions.
 *
 * <p>Not hooked here: reserve-deck shuffle notifications. Face-down Used bodies are not
 * treated as known unless this seat is shown them (examine) or the pile is turned face up
 * (that scrape lives on the encoder, own seat only).
 */
public final class InformationSetGameStateListener implements GameStateListener {

    private final InformationSetTracker tracker;
    private int destinyEvents;
    private int interruptEvents;
    private int opponentRevealEvents;
    private final Set<Integer> consumedDecisionIds = new HashSet<>();
    private final Set<Integer> recordedSabaccCards = new HashSet<>();

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
        Map<String, Object> row = cardRow(card, "DESTINY", destinyText, "IN_USED_UNTIL_SHUFFLE");
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
        Map<String, Object> row = cardRow(card, "INTERRUPT_PLAYED", null, "IN_USED_UNTIL_SHUFFLE");
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

    private void noteOpponentReveal(PhysicalCard card, String how, String recycleHint) {
        if (card == null || !isOpponentOwned(card)) {
            return;
        }
        Map<String, Object> row = cardRow(card, how, null, recycleHint);
        if (row == null) {
            return;
        }
        opponentRevealEvents++;
        tracker.recordSeen(row);
        tracker.recordDestinyRecycle(row);
        tracker.recordOpponentRevealed(row);
    }

    private static boolean isHiddenOrShownRevealZone(Zone zone) {
        if (zone == null) {
            return false;
        }
        if (!zone.isPublic() || zone.isFaceDown()) {
            return true;
        }
        return zone == Zone.REVEALED_SABACC_HAND;
    }

    private static String howForShownZone(Zone zone) {
        if (zone == null) {
            return "EXAMINE";
        }
        switch (zone) {
            case HAND:
                return "EXAMINE_HAND";
            case SABACC_HAND:
                return "EXAMINE_SABACC";
            case REVEALED_SABACC_HAND:
                return "SABACC_REVEAL";
            case RESERVE_DECK:
            case TOP_OF_RESERVE_DECK:
                return "PEEK_RESERVE";
            case FORCE_PILE:
            case TOP_OF_FORCE_PILE:
            case FROZEN_PILE:
            case TOP_OF_FROZEN_PILE:
                return "PEEK_FORCE";
            case USED_PILE:
            case TOP_OF_USED_PILE:
                return "EXAMINE_USED";
            case LOST_PILE:
            case TOP_OF_LOST_PILE:
                return "EXAMINE_LOST";
            default:
                return "EXAMINE_" + zone.name();
        }
    }

    private static String recycleHintForZone(Zone zone) {
        if (zone == Zone.HAND || zone == Zone.SABACC_HAND || zone == Zone.REVEALED_SABACC_HAND) {
            return "SEEN_NOT_RECYCLED";
        }
        return "IN_USED_UNTIL_SHUFFLE";
    }

    private static Map<String, Object> cardRow(PhysicalCard card, String how, String destinyText, String recycleHint) {
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
        row.put("recycleHint", recycleHint != null ? recycleHint : "IN_USED_UNTIL_SHUFFLE");
        return row;
    }

    // --- unused GameStateListener methods (required by interface) ---

    @Override
    public void cardCreated(PhysicalCard card, GameState gameState, boolean restoreSnapshot) {
        // Initial state dump and in-play creates are covered by InformationSetEncoder.publicInPlay.
        // Opponent hand / unrevealed sabacc must not be scraped here.
        if (card == null || card.getZone() != Zone.REVEALED_SABACC_HAND || !isOpponentOwned(card)) {
            return;
        }
        if (!recordedSabaccCards.add(System.identityHashCode(card))) {
            return;
        }
        noteOpponentReveal(card, "SABACC_REVEAL", "SEEN_NOT_RECYCLED");
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
        // Top of reserve turned face up is public. Turning it back down is not a new identity.
        if (card == null || gameState == null || !isOpponentOwned(card)) {
            return;
        }
        String owner = card.getOwner();
        try {
            if (owner != null && gameState.isTopCardOfReserveDeckRevealed(owner)) {
                noteOpponentReveal(card, "PEEK_RESERVE", "IN_USED_UNTIL_SHUFFLE");
            }
        } catch (RuntimeException ignored) {
            // flag unavailable
        }
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
        // Identities arrive on the following cardCreated once the zone is REVEALED_SABACC_HAND.
        // Recording here would still see private SABACC_HAND zones.
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
        // Animation only; identities come from the decision shown to this seat.
    }

    @Override
    public void cardActivated(String playerPerforming, PhysicalCard card, GameState gameState) {
    }

    @Override
    public void decisionRequired(String playerId, AwaitingDecision awaitingDecision) {
        if (awaitingDecision == null || playerId == null || !playerId.equals(tracker.getPlayerId())) {
            // The other seat's decisions can contain their unrevealed hand. Never read those.
            return;
        }
        int decisionId = awaitingDecision.getAwaitingDecisionId();
        if (decisionId != 0 && !consumedDecisionIds.add(decisionId)) {
            return;
        }
        if (!(awaitingDecision instanceof ArbitraryCardsSelectionDecision)) {
            return;
        }
        Collection<PhysicalCard> shown = ((ArbitraryCardsSelectionDecision) awaitingDecision).getShownCards();
        if (shown == null) {
            return;
        }
        for (PhysicalCard card : shown) {
            if (card == null || !isOpponentOwned(card)) {
                continue;
            }
            Zone zone = card.getZone();
            if (!isHiddenOrShownRevealZone(zone)) {
                continue;
            }
            noteOpponentReveal(card, howForShownZone(zone), recycleHintForZone(zone));
        }
    }
}
