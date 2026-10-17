package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An influence modifier.
 */
public class InfluenceModifier extends AbstractModifier {
    private Evaluator _evaluator;

    public InfluenceModifier(PhysicalCard source, float modifierAmount) {
        this(source, source, null, modifierAmount);
    }

    public InfluenceModifier(PhysicalCard source, Condition condition, float modifierAmount) {
        this(source, source, condition, new ConstantEvaluator(modifierAmount));
    }

    public InfluenceModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, source, null, evaluator);
    }

    public InfluenceModifier(PhysicalCard source, Condition condition, Evaluator evaluator) {
        this(source, source, condition, evaluator);
    }

    public InfluenceModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    public InfluenceModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    public InfluenceModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    public InfluenceModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.INFLUENCE, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Influence +" + GuiUtils.formatAsString(value);
        else
            return "Influence " + GuiUtils.formatAsString(value);
    }

    public float getAmount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
