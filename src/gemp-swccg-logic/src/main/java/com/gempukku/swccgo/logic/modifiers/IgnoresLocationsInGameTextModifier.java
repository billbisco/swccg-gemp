package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the game text of the affected cards to ignore the specified locations (for example, "Wipe Them
 * Out, All Of Them ignores starship and holosite sites"). Cards opt in by filtering the locations they check with
 * {@link Filters#notIgnoredByGameTextOf(PhysicalCard)}.
 */
public class IgnoresLocationsInGameTextModifier extends AbstractModifier {
    private final Filter _locationFilter;

    /**
     * Creates a modifier that causes the game text of cards accepted by the affect filter to ignore locations accepted
     * by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the cards whose game text ignores the locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     * @param locationFilter the locations to ignore
     */
    public IgnoresLocationsInGameTextModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, "Ignores certain locations", affectFilter, condition, ModifierType.IGNORES_LOCATIONS_IN_GAME_TEXT);
        _locationFilter = Filters.and(locationFilter);
    }

    public Filter getLocationFilter() {
        return _locationFilter;
    }
}
