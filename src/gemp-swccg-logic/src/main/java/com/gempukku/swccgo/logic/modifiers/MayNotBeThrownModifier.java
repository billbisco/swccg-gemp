package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from being 'thrown' (for example, a vehicle 'thrown' by Vader).
 * Card game text that 'throws' a card should check ModifiersQuerying.mayNotBeThrown (or Filters.canBeThrown)
 * before choosing or resolving the target.
 */
public class MayNotBeThrownModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'thrown'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeThrownModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'thrown'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeThrownModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be 'thrown'", affectFilter, condition, ModifierType.MAY_NOT_BE_THROWN);
    }
}
