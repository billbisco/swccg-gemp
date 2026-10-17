package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets influence to an unmodifiable value.
 */
public class ResetInfluenceModifier extends AbstractModifier {
    private float _resetValue;

    public ResetInfluenceModifier(PhysicalCard source, Filterable affectFilter, float resetValue) {
        super(source, null, affectFilter, null, ModifierType.UNMODIFIABLE_INFLUENCE, true);
        _resetValue = resetValue;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Influence = " + GuiUtils.formatAsString(_resetValue);
    }

    public float getAmount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}
