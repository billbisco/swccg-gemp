package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns 1 if the card would deploy to the target for 0 Force (its deploy cost, printed or after
 * modifiers, is 0), and 0 otherwise. A card that deploys "for free" is not deploying for 0 Force: that case is covered
 * separately (The Professor (V) / Imperial Detention charge half the printed cost for it).
 */
public class OneIfDeploysForZeroForceEvaluator extends BaseEvaluator {

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        return evaluateExpression(gameState, modifiersQuerying, cardAffected, null);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard target) {
        if (!modifiersQuerying.hasDeployCostAttribute(cardAffected)) {
            return 0;
        }
        if (modifiersQuerying.grantedDeployForFree(gameState, cardAffected, target)) {
            return 0;
        }
        float deployCost = modifiersQuerying.getDeployCost(gameState, cardAffected, cardAffected, target, false, null, false, 0, null, false);
        return deployCost == 0 ? 1 : 0;
    }
}
