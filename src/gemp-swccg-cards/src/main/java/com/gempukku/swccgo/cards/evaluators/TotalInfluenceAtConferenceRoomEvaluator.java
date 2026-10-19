package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Evaluator for a player's total influence at Death Star: Conference Room.
 */
public class TotalInfluenceAtConferenceRoomEvaluator extends BaseEvaluator {
    private final int _permSourceCardId;
    private final String _playerId;

    public TotalInfluenceAtConferenceRoomEvaluator(PhysicalCard source, String playerId) {
        _permSourceCardId = source.getPermanentCardId();
        _playerId = playerId;
    }

    public static float get(SwccgGame game, PhysicalCard source, String playerId) {
        PhysicalCard room = Filters.findFirstActive(game, source, Filters.Death_Star_Conference_Room);
        if (room == null) {
            return 0;
        }
        return game.getModifiersQuerying().getTotalInfluenceAtLocation(game.getGameState(), playerId, room);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        return get(gameState.getGame(), source, _playerId);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
        return evaluateExpression(gameState, modifiersQuerying, cardAffected);
    }
}
