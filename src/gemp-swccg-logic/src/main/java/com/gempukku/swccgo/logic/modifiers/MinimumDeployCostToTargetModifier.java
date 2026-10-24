package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that floors deploy cost when deploying accepted cards to specified targets.
 */
public class MinimumDeployCostToTargetModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _cardToDeployFilter;

    /**
     * Creates a modifier that floors deploy cost when deploying cards accepted by the affect filter to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is floored
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param minimumCost the minimum deploy cost
     * @param targetFilter the target filter
     */
    public MinimumDeployCostToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int minimumCost, Filterable targetFilter) {
        super(source, null, targetFilter, condition, ModifierType.MIN_DEPLOY_COST_TO_TARGET_REDUCED_TO, true);
        _evaluator = new ConstantEvaluator(minimumCost);
        _cardToDeployFilter = Filters.or(affectFilter, Filters.hasPermanentAboard(Filters.and(affectFilter)));
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploy cost at least " + GuiUtils.formatAsString(_evaluator.evaluateExpression(gameState, modifiersQuerying, self));
    }

    @Override
    public float getDeployCostToTargetModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, PhysicalCard target) {
        if (Filters.and(_cardToDeployFilter).accepts(gameState, modifiersQuerying, cardToDeploy)) {
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, cardToDeploy);
        }
        return 0;
    }
}
