package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that treats the source owner's occupation of affected locations as control.
 */
public class TreatsOccupyAsControlModifier extends AbstractModifier {

    /**
     * Creates a modifier that treats the source owner's occupation of affected locations as control.
     * @param source the source of the modifier
     * @param locationFilter the locations whose occupation is treated as control
     */
    public TreatsOccupyAsControlModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null);
    }

    /**
     * Creates a modifier that treats the source owner's occupation of affected locations as control.
     * @param source the source of the modifier
     * @param locationFilter the locations whose occupation is treated as control
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public TreatsOccupyAsControlModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        super(source, "Occupy is treated as control", locationFilter, condition, ModifierType.TREATS_OCCUPY_AS_CONTROL, true);
        _playerId = source.getOwner();
    }
}
