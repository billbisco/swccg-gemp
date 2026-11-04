package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the affected weapons (or cards with a permanent weapon) from being re-targeted while firing.
 * Read by RetargetWeaponEffect.
 */
public class MayNotBeRetargetedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents weapons accepted by the filter from being re-targeted.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeRetargetedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be re-targeted", affectFilter, condition, ModifierType.MAY_NOT_BE_RETARGETED, true);
    }
}
