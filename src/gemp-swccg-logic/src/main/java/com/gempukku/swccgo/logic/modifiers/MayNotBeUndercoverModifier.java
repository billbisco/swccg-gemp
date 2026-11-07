package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for spies that may not be Undercover.
 */
public class MayNotBeUndercoverModifier extends AbstractModifier {

    /**
     * Creates a modifier for spies that may not be Undercover.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public MayNotBeUndercoverModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for spies that may not be Undercover.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeUndercoverModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be Undercover", affectFilter, condition, ModifierType.MAY_NOT_BE_UNDERCOVER, true);
    }
}
