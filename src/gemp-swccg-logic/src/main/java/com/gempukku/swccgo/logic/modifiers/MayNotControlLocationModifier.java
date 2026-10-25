package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the specified player from controlling the affected locations.
 */
public class MayNotControlLocationModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the specified player from controlling the affected locations.
     * @param source the source of the modifier
     * @param locationFilter the locations that may not be controlled
     * @param playerId the player who may not control those locations
     */
    public MayNotControlLocationModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that prevents the specified player from controlling the affected locations.
     * @param source the source of the modifier
     * @param locationFilter the locations that may not be controlled
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player who may not control those locations
     */
    public MayNotControlLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, "May not control", Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_CONTROL_LOCATION, true);
        _playerId = playerId;
    }
}
