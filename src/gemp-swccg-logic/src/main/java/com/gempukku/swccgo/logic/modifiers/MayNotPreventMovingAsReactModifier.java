package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the affected cards (for example Despair) from preventing cards from moving as a 'react',
 * except as a 'react' away from battle. Their other "may not move" prohibitions still apply.
 */
public class MayNotPreventMovingAsReactModifier extends AbstractModifier {

    /**
     * Creates the modifier.
     * @param source the source card of the modifier
     * @param affectFilter the cards that may not prevent movement as a 'react'
     */
    public MayNotPreventMovingAsReactModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates the modifier.
     * @param source the source card of the modifier
     * @param affectFilter the cards that may not prevent movement as a 'react'
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     */
    public MayNotPreventMovingAsReactModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not prevent cards from moving as a 'react' (except away from battle)", affectFilter, condition, ModifierType.MAY_NOT_PREVENT_MOVING_AS_REACT, true);
    }
}
