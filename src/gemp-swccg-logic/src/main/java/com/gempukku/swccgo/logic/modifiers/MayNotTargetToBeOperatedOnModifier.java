package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected characters from being targeted to be 'operated' on.
 */
public class MayNotTargetToBeOperatedOnModifier extends AbstractModifier {
    public MayNotTargetToBeOperatedOnModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not target to be 'operated' on", affectFilter, ModifierType.MAY_NOT_TARGET_TO_BE_OPERATED_ON);
    }

    public MayNotTargetToBeOperatedOnModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "May not target to be 'operated' on", affectFilter, condition, ModifierType.MAY_NOT_TARGET_TO_BE_OPERATED_ON);
    }
}
