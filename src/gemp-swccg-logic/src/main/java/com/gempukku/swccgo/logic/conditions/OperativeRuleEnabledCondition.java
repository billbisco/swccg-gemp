package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/** The shared condition for the global operative-rule modifiers. */
public class OperativeRuleEnabledCondition implements Condition {
    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return gameState.isOperativeRuleEnabled();
    }
}
