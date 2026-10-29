package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows cards to be targeted by weapons like a vehicle.
 */
public class MayBeTargetedByWeaponsLikeVehicleModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows cards accepted by filter to be targeted by weapons like a vehicle.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may be targeted
     */
    public MayBeTargetedByWeaponsLikeVehicleModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that allows cards accepted by filter to be targeted by weapons like a vehicle.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayBeTargetedByWeaponsLikeVehicleModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May be targeted by weapons like a vehicle", affectFilter, condition, ModifierType.TARGETED_BY_WEAPONS_LIKE_A_VEHICLE);
    }
}
