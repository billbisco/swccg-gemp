package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for cards that may not relocate.
 */
public class MayNotRelocateModifier extends AbstractModifier {

    /**
     * Creates a modifier for cards that may not relocate.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public MayNotRelocateModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for cards that may not relocate.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotRelocateModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not relocate", affectFilter, condition, ModifierType.MAY_NOT_RELOCATE, true);
    }
}
