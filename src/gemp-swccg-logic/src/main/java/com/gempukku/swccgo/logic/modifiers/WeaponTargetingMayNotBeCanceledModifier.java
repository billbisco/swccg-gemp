package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that prevents the targeting of the affected weapons (or cards with a permanent weapon) from being
 * canceled while firing. Read by CancelWeaponTargetingEffect.
 */
public class WeaponTargetingMayNotBeCanceledModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the targeting of weapons accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public WeaponTargetingMayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Targeting may not be canceled", affectFilter, null, ModifierType.LEGACY__WEAPON_TARGETING_MAY_NOT_BE_CANCELED, true);
    }
}
