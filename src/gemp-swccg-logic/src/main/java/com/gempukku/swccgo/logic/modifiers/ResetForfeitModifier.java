package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets forfeit to an unmodifiable value.
 */
public class ResetForfeitModifier extends AbstractModifier {
    private float _resetValue;
    private boolean _evenIfMayNotBeReduced;

    /**
     * Creates a modifier that resets forfeit to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose forfeit value is reset
     * @param resetValue the reset value
     */
    public ResetForfeitModifier(PhysicalCard source, Filterable affectFilter, float resetValue) {
        this(source, affectFilter, null, resetValue);
    }

    /**
     * Creates a modifier that resets forfeit to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose forfeit value is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     */
    public ResetForfeitModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue) {
        this(source, affectFilter, condition, resetValue, false);
    }

    /**
     * Creates a modifier that resets forfeit to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose forfeit value is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param evenIfMayNotBeReduced true if the reset applies even if the card's forfeit may not normally be reduced
     */
    public ResetForfeitModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue, boolean evenIfMayNotBeReduced) {
        super(source, null, affectFilter, condition, ModifierType.UNMODIFIABLE_FORFEIT_VALUE, true);
        _resetValue = resetValue;
        _evenIfMayNotBeReduced = evenIfMayNotBeReduced;
    }

    @Override
    public boolean isForfeitResetEvenIfMayNotBeReduced() {
        return _evenIfMayNotBeReduced;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Forfeit = " + GuiUtils.formatAsString(_resetValue);
    }

    @Override
    public float getUnmodifiableForfeit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}
