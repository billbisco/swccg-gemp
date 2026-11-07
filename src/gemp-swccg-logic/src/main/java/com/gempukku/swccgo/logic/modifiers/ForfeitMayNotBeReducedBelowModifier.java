package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that prevents forfeit from being reduced below a value ("may not have forfeit reduced below N").
 * Reductions and resets may not take the forfeit below the floor; a forfeit that is already below the floor
 * (before any reduction) may not be reduced at all.
 */
public class ForfeitMayNotBeReducedBelowModifier extends AbstractModifier {
    private final float _floor;

    public ForfeitMayNotBeReducedBelowModifier(PhysicalCard source, Filterable affectFilter, float floor) {
        this(source, affectFilter, null, floor);
    }

    public ForfeitMayNotBeReducedBelowModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float floor) {
        super(source, null, affectFilter, condition, ModifierType.FORFEIT_MAY_NOT_BE_REDUCED_BELOW, true);
        _floor = floor;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Forfeit may not be reduced below " + GuiUtils.formatAsString(_floor);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _floor;
    }
}
