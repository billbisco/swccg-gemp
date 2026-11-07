package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.FirstTurn;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;

import java.util.ArrayList;
import java.util.List;

/** Offers each player any Epic Event that can deploy itself at game start. */
public class PlayStartingEpicEventsGameProcess implements GameProcess {
    private final GameProcess _followingGameProcess;
    private String _firstPlayer;
    private int _playersChecked;

    public PlayStartingEpicEventsGameProcess(SwccgGame game) {
        _followingGameProcess = new PlayStartingInterruptsGameProcess(game);
    }

    @Override
    public void process(SwccgGame game) {
        if (_playersChecked == 0) {
            _firstPlayer = FirstTurn.firstPlayer(game);
        }
        String playerId = _playersChecked++ == 0 ? _firstPlayer : game.getOpponent(_firstPlayer);
        List<PhysicalCard> choices = game.getGameState().getReserveDeck(playerId).stream()
                .filter(card -> card.getBlueprint().getCardCategory() == CardCategory.EPIC_EVENT)
                .filter(card -> card.getBlueprint().isStartingEpicEvent())
                .filter(card -> getPlayAction(game, playerId, card) != null)
                .toList();
        if (choices.isEmpty()) {
            return;
        }

        game.getUserFeedback().sendAwaitingDecision(playerId,
                new ArbitraryCardsSelectionDecision("Choose starting Epic Event", new ArrayList<>(choices), 0, 1) {
                    @Override
                    public void decisionMade(String result) throws DecisionResultInvalidException {
                        List<PhysicalCard> selected = getSelectedCardsByResponse(result);
                        if (selected.isEmpty()) {
                            return;
                        }
                        PlayCardAction action = getPlayAction(game, playerId, selected.getFirst());
                        if (action == null) {
                            throw new DecisionResultInvalidException("That starting Epic Event cannot be played.");
                        }
                        game.getActionsEnvironment().addActionToStack(action);
                    }
                });
    }

    private PlayCardAction getPlayAction(SwccgGame game, String playerId, PhysicalCard card) {
        if (card.getZone() != Zone.RESERVE_DECK) {
            return null;
        }
        return card.getBlueprint().getPlayCardAction(playerId, game, card, card, false, 0,
                null, null, null, null, null, false, 0, Filters.any, null);
    }

    @Override
    public GameProcess getNextProcess() {
        return _playersChecked < 2 ? this : _followingGameProcess;
    }
}
