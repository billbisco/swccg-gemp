package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.logic.FirstTurn;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The game process for playing starting interrupts.
 */
public class PlayStartingInterruptsGameProcess implements GameProcess {
    private String _darkPlayerId;
    private String _lightPlayerId;
    private boolean _determinedOrder;
    private boolean _lightSideGoesFirst;
    private GameProcess _followingGameProcess;
    private GameProcess _nextProcess;
    private PhysicalCard _darkSideStartingInterrupt;
    private PhysicalCard _lightSideStartingInterrupt;
    // #142 R2b: further Starting Interrupts chosen with "Starting: Interrupts" above 1, played after the first
    private final List<PhysicalCard> _darkExtras = new java.util.ArrayList<PhysicalCard>();
    private final List<PhysicalCard> _lightExtras = new java.util.ArrayList<PhysicalCard>();
    private boolean _choicesMade;

    /**
     * Creates the game process for playing starting interrupts.
     * @param game the game
     */
    public PlayStartingInterruptsGameProcess(SwccgGame game) {
        _darkPlayerId = game.getDarkPlayer();
        _lightPlayerId = game.getLightPlayer();
        _followingGameProcess = new PlayersShuffleAndDrawStartingHandGameProcess(game);
        _nextProcess = this;
    }

    @Override
    public void process(SwccgGame game) {
        if (!_determinedOrder) {
            _determinedOrder = true;
            if (FirstTurn.firstPlayer(game).equals(_lightPlayerId)) {
                _lightSideGoesFirst = true;
            }
        }

        if (!_choicesMade) {
            // Figure out the dark side starting interrupts to choose
            Collection<PhysicalCard> darkSideChoices = Filters.filter(game.getGameState().getReserveDeck(_darkPlayerId), game, Filters.playableAsStartingInterrupt);
            if (!darkSideChoices.isEmpty()) {
                game.getUserFeedback().sendAwaitingDecision(_darkPlayerId, createChooseStartingInterruptDecision(game, _darkPlayerId, darkSideChoices));
            }

            // Figure out the light side starting interrupts to choose
            Collection<PhysicalCard> lightSideChoices = Filters.filter(game.getGameState().getReserveDeck(_lightPlayerId), game, Filters.playableAsStartingInterrupt);
            if (!lightSideChoices.isEmpty()) {
                game.getUserFeedback().sendAwaitingDecision(_lightPlayerId, createChooseStartingInterruptDecision(game, _lightPlayerId, lightSideChoices));
            }

            _choicesMade = true;
            return;
        }

        // Starting Interrupts (if any) have been selected. Play them based on the player order.
        // Note: They are added to the stack in reverse order.
        // #142 R2b: a player's further Starting Interrupts resolve right after their first, in the order chosen
        if (!_lightSideGoesFirst) {
            stackExtras(game, _lightPlayerId, _lightExtras);
            if (_lightSideStartingInterrupt != null)
                game.getActionsEnvironment().addActionToStack(_lightSideStartingInterrupt.getBlueprint().getStartingInterruptAction(_lightPlayerId, game, _lightSideStartingInterrupt));
        }
        stackExtras(game, _darkPlayerId, _darkExtras);
        if (_darkSideStartingInterrupt != null) {
            game.getActionsEnvironment().addActionToStack(_darkSideStartingInterrupt.getBlueprint().getStartingInterruptAction(_darkPlayerId, game, _darkSideStartingInterrupt));
        }
        if (_lightSideGoesFirst) {
            stackExtras(game, _lightPlayerId, _lightExtras);
            if (_lightSideStartingInterrupt != null)
                game.getActionsEnvironment().addActionToStack(_lightSideStartingInterrupt.getBlueprint().getStartingInterruptAction(_lightPlayerId, game, _lightSideStartingInterrupt));
        }

        // If either player is playing a starting interrupt, reveal any Starting Interrupts being
        // played so both players know what they are before the first player actually plays it
        // (since it may influence which Starting Interrupt actions each player wants to do).
        if (_darkSideStartingInterrupt != null || _lightSideStartingInterrupt != null) {
            SystemQueueAction action = new SystemQueueAction();
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (_darkSideStartingInterrupt != null) {
                                game.getGameState().sendMessage(_darkPlayerId + " reveals Starting Interrupt " + GameUtils.getCardLink(_darkSideStartingInterrupt));
                                game.getGameState().showCardOnScreen(_darkSideStartingInterrupt);
                                game.getGameState().setStartingInterruptPlayed(_darkPlayerId, _darkSideStartingInterrupt);
                            }
                            if (_lightSideStartingInterrupt != null) {
                                game.getGameState().sendMessage(_lightPlayerId + " reveals Starting Interrupt " + GameUtils.getCardLink(_lightSideStartingInterrupt));
                                game.getGameState().showCardOnScreen(_lightSideStartingInterrupt);
                                game.getGameState().setStartingInterruptPlayed(_lightPlayerId, _lightSideStartingInterrupt);
                            }
                        }
                    });
            game.getActionsEnvironment().addActionToStack(action);
        }

        _nextProcess = _followingGameProcess;
    }

    /** Stacks a player's further Starting Interrupts so they resolve in the order chosen (the stack is last-in first-out). */
    private void stackExtras(SwccgGame game, String playerId, List<PhysicalCard> extras) {
        for (int i = extras.size() - 1; i >= 0; i--) {
            PhysicalCard card = extras.get(i);
            if (card.getZone() != null && !card.getZone().isInPlay())
                game.getActionsEnvironment().addActionToStack(card.getBlueprint().getStartingInterruptAction(playerId, game, card));
        }
    }

    private AwaitingDecision createChooseStartingInterruptDecision(final SwccgGame game, final String playerId, final Collection<PhysicalCard> possibleStartingInterrupts) {
        int max = Math.min(game.getGameState().getStartingInterruptsCount(), possibleStartingInterrupts.size());
        return new ArbitraryCardsSelectionDecision(max > 1 ? "Choose up to " + max + " starting interrupts" : "Choose starting interrupt",
                new LinkedList<PhysicalCard>(possibleStartingInterrupts), 0, max) {
            @Override
            public void decisionMade(String result) throws DecisionResultInvalidException {
                List<PhysicalCard> selectedInterrupts = getSelectedCardsByResponse(result);
                if (selectedInterrupts.isEmpty())
                    return;

                // #142 R2b: a unique Starting Interrupt is played once (Bill 2026-11-04)
                List<PhysicalCard> chosen = new java.util.ArrayList<PhysicalCard>();
                java.util.Set<String> uniqueTitles = new java.util.HashSet<String>();
                for (PhysicalCard card : selectedInterrupts) {
                    if (card.getBlueprint().getUniqueness() == com.gempukku.swccgo.common.Uniqueness.UNIQUE && !uniqueTitles.add(card.getTitle()))
                        continue;
                    chosen.add(card);
                }
                PhysicalCard selectedPhysicalCard = chosen.get(0);
                List<PhysicalCard> extras = playerId.equals(_darkPlayerId) ? _darkExtras : _lightExtras;
                extras.clear();
                extras.addAll(chosen.subList(1, chosen.size()));

                if (playerId.equals(_darkPlayerId))
                    _darkSideStartingInterrupt = selectedPhysicalCard;
                else
                    _lightSideStartingInterrupt = selectedPhysicalCard;
            }
        };
    }

    @Override
    public GameProcess getNextProcess() {
        return _nextProcess;
    }
}
