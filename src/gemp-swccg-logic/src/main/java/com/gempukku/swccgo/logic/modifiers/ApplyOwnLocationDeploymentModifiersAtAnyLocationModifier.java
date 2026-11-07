package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes affected cards to apply their own deployment modifiers for certain locations (for example,
 * "Endor deployment modifiers") at any location. A card's own deployment modifier counts as one for those locations
 * when its target filter accepts a location on table that the location filter accepts.
 */
public class ApplyOwnLocationDeploymentModifiersAtAnyLocationModifier extends AbstractModifier {
    private final Filter _locationFilter;

    /**
     * Creates a modifier that causes cards accepted by the filter to apply their own deployment modifiers for
     * locations accepted by the location filter at any location.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param locationFilter the filter for the locations whose deployment modifiers are applied anywhere
     */
    public ApplyOwnLocationDeploymentModifiersAtAnyLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, locationFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to apply their own deployment modifiers for
     * locations accepted by the location filter at any location.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param locationFilter the filter for the locations whose deployment modifiers are applied anywhere
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ApplyOwnLocationDeploymentModifiersAtAnyLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter, Condition condition) {
        super(source, "Applies own location deployment modifiers at any location", affectFilter, condition, ModifierType.APPLIES_OWN_LOCATION_DEPLOYMENT_MODIFIERS_AT_ANY_LOCATION);
        _locationFilter = Filters.and(locationFilter);
    }

    /**
     * Determines if a deployment modifier with the specified target filter is a modifier for the locations this
     * modifier covers.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param targetFilter the target filter of the deployment modifier
     * @return true or false
     */
    public boolean isDeploymentModifierForCoveredLocation(GameState gameState, ModifiersQuerying modifiersQuerying, Filter targetFilter) {
        for (PhysicalCard location : Filters.filterTopLocationsOnTable(gameState.getGame(), _locationFilter)) {
            if (targetFilter.accepts(gameState, modifiersQuerying, location))
                return true;
        }
        return false;
    }
}
