package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents destiny draws for the affected action sources (for example, Sense and Alter) from being
 * modified.
 */
public class MayNotModifyDestinyDrawsForActionSourceModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents destiny draws for the affected action sources from being modified.
     * @param source the source of the modifier
     * @param actionSourceFilter the filter for cards whose destiny draws may not be modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     */
    public MayNotModifyDestinyDrawsForActionSourceModifier(PhysicalCard source, Filterable actionSourceFilter, Condition condition) {
        super(source, "Destiny draws may not be modified", actionSourceFilter, condition, ModifierType.MAY_NOT_MODIFY_DESTINY_DRAWS_FOR_ACTION_SOURCE);
    }
}
