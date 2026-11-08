package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that requires the specified player to first use extra Force to flip over (reveal) a 'bluff card' at
 * locations accepted by the location filter.
 */
public class ExtraForceCostToFlipBluffCardModifier extends AbstractModifier {
    private final Evaluator _evaluator;

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where 'bluff cards' are stacked
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     * @param evaluator the evaluator that calculates the extra Force (values below 0 count as 0)
     * @param playerId the player that must use the extra Force to flip a 'bluff card'
     */
    public ExtraForceCostToFlipBluffCardModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, "Extra Force cost to flip a 'bluff card'", locationFilter, condition, ModifierType.EXTRA_FORCE_COST_TO_FLIP_BLUFF_CARD);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public float getValue(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return Math.max(0, _evaluator.evaluateExpression(gameState, modifiersQuerying, location));
    }
}
