package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to total influence at a location.
 */
public class TotalInfluenceAtLocationModifier extends AbstractModifier {
    private Evaluator _evaluator;

    public TotalInfluenceAtLocationModifier(PhysicalCard source, Filterable locationFilter, float modifierAmount, String playerId) {
        this(source, locationFilter, null, modifierAmount, playerId);
    }

    public TotalInfluenceAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, float modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    public TotalInfluenceAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.TOTAL_INFLUENCE_AT_LOCATION, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText = (gameState.getSide(_playerId) == Side.DARK) ? "Dark side" : "Light side";
        if (value >= 0)
            return sideText + " total influence +" + GuiUtils.formatAsString(value);
        else
            return sideText + " total influence " + GuiUtils.formatAsString(value);
    }

    public float getAmount(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        if (_playerId.equals(playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
        else
            return 0;
    }
}
