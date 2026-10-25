package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that grants additional regular moves this turn to the affected cards.
 */
public class MayMakeAdditionalMovesModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that grants additional regular moves this turn to the affected cards.
     * @param source the source of the modifier
     * @param affectFilter the cards that may make additional moves
     * @param numAdditionalMoves the number of additional moves granted
     */
    public MayMakeAdditionalMovesModifier(PhysicalCard source, Filterable affectFilter, int numAdditionalMoves) {
        this(source, affectFilter, null, numAdditionalMoves);
    }

    /**
     * Creates a modifier that grants additional regular moves this turn to the affected cards.
     * @param source the source of the modifier
     * @param affectFilter the cards that may make additional moves
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param numAdditionalMoves the number of additional moves granted
     */
    public MayMakeAdditionalMovesModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int numAdditionalMoves) {
        super(source, "May make additional moves", Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MAY_MAKE_ADDITIONAL_MOVES, true);
        _evaluator = new ConstantEvaluator(numAdditionalMoves);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
