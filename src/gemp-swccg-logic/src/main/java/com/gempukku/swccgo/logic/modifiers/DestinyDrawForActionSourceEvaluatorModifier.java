package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A destiny modifier, calculated by an evaluator, for destiny drawn by the specified player for an action with a card
 * accepted by the filter as the action source (e.g. "attempts to 'disable' the Planetary Shield are +2 for each ...").
 * The evaluator is evaluated with the modifier source as its card.
 */
public class DestinyDrawForActionSourceEvaluatorModifier extends AbstractModifier {
    private final Evaluator _evaluator;
    private final PhysicalCard _source;

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for the card that is the action source of the destiny draw
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose destiny draws are affected
     */
    public DestinyDrawForActionSourceEvaluatorModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, affectFilter, condition, ModifierType.EACH_DESTINY_DRAW_FOR_ACTION_SOURCE, true);
        _source = source;
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, _source);
        return "Destiny draw " + (value >= 0 ? "+" : "") + GuiUtils.formatAsString(value);
    }

    @Override
    public float getDestinyDrawFromSourceCardModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard destinyDrawActionSource) {
        if (isForPlayer(playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, _source);
        return 0;
    }
}
