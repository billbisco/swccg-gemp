package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.GameTextOccupationFilters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/** Occupation condition whose printed word may be replaced on its source card. */
public class GameTextOccupiesCondition implements Condition {
    private final int _locationId;
    private final Filter _occupies;

    public GameTextOccupiesCondition(String playerId, PhysicalCard textSource, PhysicalCard location) {
        _locationId = location.getPermanentCardId();
        _occupies = GameTextOccupationFilters.occupies(playerId, textSource);
    }

    @Override
    public boolean isFulfilled(GameState state, ModifiersQuerying modifiers) {
        PhysicalCard location = state.findCardByPermanentId(_locationId);
        return location != null && _occupies.accepts(state, modifiers, location);
    }
}
