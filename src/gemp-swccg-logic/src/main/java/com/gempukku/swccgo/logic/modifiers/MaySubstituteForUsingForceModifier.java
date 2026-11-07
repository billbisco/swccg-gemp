package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that lets a player do something else "instead of using X Force" (for example, turn cards face down).
 * The value is the most Force the source card can currently stand in for. It counts toward the Force the player
 * has available to use; the source card's "before using Force" trigger performs the substitution on the
 * UseForceEffect (see {@link com.gempukku.swccgo.logic.effects.UseForceEffect#addAmountSubstituted(int)}).
 */
public class MaySubstituteForUsingForceModifier extends AbstractModifier {
    private final Evaluator _evaluator;

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the most Force that may currently be substituted
     * @param playerId the player that may substitute for using Force
     */
    public MaySubstituteForUsingForceModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, null, condition, ModifierType.MAY_SUBSTITUTE_FOR_USING_FORCE, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
