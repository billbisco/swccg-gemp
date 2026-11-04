package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that adds to any attempt to 'align' the affected systems (Nute Gunray (V) 1291_5: "Adds 1 to any
 * attempt to 'align' a system"). The card that makes the attempt (Rally To Our Cause 1291_28) sums these modifiers
 * into its destiny total.
 */
public class AlignAttemptModifier extends AbstractModifier {
    private final float _amount;

    /**
     * Creates a modifier that adds to any attempt to 'align' the affected systems.
     * @param source the source of the modifier
     * @param affectFilter the systems whose 'align' attempts are modified
     * @param amount the amount added to the attempt
     */
    public AlignAttemptModifier(PhysicalCard source, Filterable affectFilter, float amount) {
        this(source, affectFilter, null, amount);
    }

    /**
     * Creates a modifier that adds to any attempt to 'align' the affected systems.
     * @param source the source of the modifier
     * @param affectFilter the systems whose 'align' attempts are modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param amount the amount added to the attempt
     */
    public AlignAttemptModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float amount) {
        super(source, null, affectFilter, condition, ModifierType.ALIGN_ATTEMPT, true);
        _amount = amount;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Adds " + (int) _amount + " to attempts to 'align' it";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _amount;
    }
}
