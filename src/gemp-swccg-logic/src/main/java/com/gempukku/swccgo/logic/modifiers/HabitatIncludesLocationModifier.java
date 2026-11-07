package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that adds locations accepted by the location filter to the habitat of creatures accepted by the affect
 * filter (for deploying, moving and the habitat rule).
 */
public class HabitatIncludesLocationModifier extends AbstractModifier {
    private final Filter _locationFilter;

    /**
     * Creates a modifier that adds locations to the habitat of the affected creatures.
     * @param source the source of the modifier
     * @param affectFilter the filter for creatures affected by this modifier
     * @param locationFilter the location filter
     */
    public HabitatIncludesLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        super(source, null, affectFilter, null, ModifierType.HABITAT_INCLUDES_LOCATION, true);
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Habitat includes additional location";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return _locationFilter.accepts(gameState, modifiersQuerying, target);
    }
}
