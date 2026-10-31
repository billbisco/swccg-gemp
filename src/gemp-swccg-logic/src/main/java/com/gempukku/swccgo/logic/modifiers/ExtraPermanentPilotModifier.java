package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that grants an additional permanent pilot of a specified ability to affected cards.
 */
public class ExtraPermanentPilotModifier extends AbstractModifier {
    private final float _ability;

    public ExtraPermanentPilotModifier(PhysicalCard source, Filterable affectFilter, float ability) {
        super(source, null, affectFilter, null, ModifierType.EXTRA_PERMANENT_PILOTS, true);
        _ability = ability;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Additional permanent pilot of ability " + GuiUtils.formatAsString(_ability);
    }

    @Override
    public float getExtraPermanentPilotAbility(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _ability;
    }
}
