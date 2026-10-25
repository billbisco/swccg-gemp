package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * Caps how much a card's deploy cost may be reduced when deploying to specified locations.
 */
public class MaximumToReduceDeployCostByToLocationModifier extends AbstractModifier {
    private final Evaluator _evaluator;
    private final Filter _targetFilter;

    public MaximumToReduceDeployCostByToLocationModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable locationFilter) {
        super(source, null, affectFilter, null, ModifierType.MAX_AMOUNT_TO_REDUCE_DEPLOY_COST_BY_TO_TARGET, true);
        _evaluator = new ConstantEvaluator(modifierAmount);
        _targetFilter = Filters.locationAndCardsAtLocation(Filters.and(locationFilter));
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return _targetFilter.accepts(gameState, modifiersQuerying, targetCard);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return "Deploy cost here may be reduced by maximum of " + GuiUtils.formatAsString(_evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard));
    }

    @Override
    public float getMaximumToReduceDeployCostBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
