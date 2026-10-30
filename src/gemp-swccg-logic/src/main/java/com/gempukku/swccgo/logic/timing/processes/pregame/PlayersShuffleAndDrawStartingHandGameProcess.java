package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;
import com.gempukku.swccgo.logic.timing.processes.turn.BetweenTurnsProcess;

import java.util.Collection;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;

/**
 * The game process for shuffling deck and drawing starting hands.
 */
public class PlayersShuffleAndDrawStartingHandGameProcess implements GameProcess {
    private boolean _choicesOffered;
    private boolean _finished;
    private String _darkPlayerId;
    private String _lightPlayerId;
    private GameProcess _followingGameProcess;

    /**
     * Creates the game process for shuffling deck and drawing starting hands.
     * @param game the game
     */
    public PlayersShuffleAndDrawStartingHandGameProcess(SwccgGame game) {
        _darkPlayerId = game.getDarkPlayer();
        _lightPlayerId = game.getLightPlayer();
        _followingGameProcess = new BetweenTurnsProcess();
    }

    @Override
    public void process(SwccgGame game) {

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (!_choicesOffered) {
            // The starting phase (starting effects, locations/objectives, and starting interrupts) is now complete.
            // Place any cards that are not allowed in Reserve Deck that are still in Reserve Deck out of play. This runs
            // here (after starting interrupts) rather than earlier so that a card allowed to be deployed at game start by a
            // starting interrupt from Reserve Deck (e.g. a double-sided Effect) can be deployed first, and is only removed
            // if it was not deployed. Runs before shuffling/drawing so such a card can never be drawn into a starting hand.
            for (String player : gameState.getPlayerOrder().getAllPlayers()) {
                Collection<PhysicalCard> invalidCards = Filters.filter(gameState.getReserveDeck(player), game, Filters.mayNotBePlacedInReserveDeck);
                if (!invalidCards.isEmpty()) {
                    gameState.sendMessage(GameUtils.getAppendedNames(invalidCards) + " " + GameUtils.be(invalidCards)
                            + " placed out of play due to not being allowed to be placed in Reserve Deck");
                    gameState.removeCardsFromZone(invalidCards);
                    for (PhysicalCard card : invalidCards) {
                        gameState.addCardToZone(card, Zone.OUT_OF_PLAY, card.getOwner());
                    }
                }
            }

            // set current player opposite of player with first turn
            if (modifiersQuerying.hasFlagActive(gameState, ModifierFlag.LIGHT_SIDE_GOES_FIRST)) {
                gameState.setFirstPlayer(_lightPlayerId);
                gameState.sendMessage("Light Side goes first");
            }
            else {
                gameState.setFirstPlayer(_darkPlayerId);
                gameState.sendMessage("Dark Side goes first");
            }

            _choicesOffered = true;
            if (game.getFormat().supportsStartingCredits())
                gameState.sendMessage("Starting credit budget: " + gameState.getStartingCredits() + " per player. Unused credits expire after setup.");
            if (gameState.getStartingCredits() > 0) {
                for (String player : gameState.getPlayerOrder().getAllPlayers()) {
                    offerCreditSelection(game, player);
                }
                return;
            }
        }

        for (String player : gameState.getPlayerOrder().getAllPlayers()) {

            // Shuffle Reserve Deck
            gameState.sendMessage(player + " shuffles " + player + "'s " + Zone.RESERVE_DECK.getHumanReadable());
            gameState.shuffleReserveDeck(player);

            // Draw starting hand
            int openingHandSize = modifiersQuerying.getNumCardsToDrawInStartingHand(gameState, player);
            if (gameState.getStartingCredits() > 0)
                openingHandSize = Math.max(0, openingHandSize - gameState.getHand(player).size());
            gameState.sendMessage(player + " draws "  + openingHandSize + " cards into hand");
            gameState.playerDrawsCardsIntoStartingHandFromReserveDeck(player, openingHandSize);
        }
        // clear any modifiers, counters, action proxies that may exist prior to starting first turn
        game.getModifiersEnvironment().removeEndOfTurnModifiers();
        game.getModifiersEnvironment().removeEndOfTurnCounters();
        game.getActionsEnvironment().removeEndOfTurnActionProxies();
        _finished = true;
    }

    private void offerCreditSelection(final SwccgGame game, final String player) {
        GameState state = game.getGameState();
        final int budget = state.getStartingCredits();
        final int slots = Math.max(0, game.getModifiersQuerying().getNumCardsToDrawInStartingHand(state, player)
                - state.getHand(player).size());
        List<PhysicalCard> eligible = state.getReserveDeck(player).stream()
                .filter(card -> player.equals(card.getOwner()) && card.getBlueprint().getCredits() > 0
                        && card.getBlueprint().getCredits() <= budget).toList();
        if (slots == 0 || eligible.isEmpty())
            return;
        Map<PhysicalCard, String> costs = new LinkedHashMap<>();
        eligible.forEach(card -> costs.put(card, card.getBlueprint().getCredits() + " credits"));
        game.getUserFeedback().sendAwaitingDecision(player, new ArbitraryCardsSelectionDecision(
                "Choose opening-hand cards costing at most " + budget + " credits (or choose none)",
                eligible, eligible, 0, Math.min(slots, eligible.size()), costs) {
            { setParam("startingCredits", String.valueOf(budget)); }
            @Override
            public void decisionMade(String response) throws DecisionResultInvalidException {
                // Reject malformed temporary IDs before the shared parser; no card identities leave this private decision.
                if (response == null || (!response.isEmpty() && !response.matches("temp[0-9]+(,temp[0-9]+)*")))
                    throw new DecisionResultInvalidException();
                List<PhysicalCard> chosen = getSelectedCardsByResponse(response);
                int spent = 0;
                for (PhysicalCard card : chosen) {
                    int cost = card.getBlueprint().getCredits();
                    if (cost <= 0 || !player.equals(card.getOwner()) || !state.getReserveDeck(player).contains(card))
                        throw new DecisionResultInvalidException();
                    spent += cost;
                }
                if (spent > budget)
                    throw new DecisionResultInvalidException("Too many credits selected. Please try again.");
                if (chosen.size() > slots)
                    throw new DecisionResultInvalidException();
                state.removeCardsFromZone(chosen);
                for (PhysicalCard card : chosen)
                    state.addCardToZone(card, Zone.HAND, player);
            }
        });
    }

    @Override
    public GameProcess getNextProcess() {
        return _finished ? _followingGameProcess : this;
    }
}
