package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the affected systems from being 'liberated'.
 */
public class MayNotBeLiberatedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the affected systems from being 'liberated'.
     * @param source the source of the modifier
     * @param affectFilter the systems that may not be 'liberated'
     */
    public MayNotBeLiberatedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents the affected systems from being 'liberated'.
     * @param source the source of the modifier
     * @param affectFilter the systems that may not be 'liberated'
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeLiberatedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be 'liberated'", affectFilter, condition, ModifierType.MAY_NOT_BE_LIBERATED, true);
    }
}
