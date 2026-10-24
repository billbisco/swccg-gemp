package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents the specified player from looking at opponent's hand using a specified card (or no card).
 */
public class MayNotLookAtOpponentsHandModifier extends AbstractModifier {
    private Filter _sourceFilter;

    public MayNotLookAtOpponentsHandModifier(PhysicalCard source, String playerId, Condition condition, Filterable sourceFilter) {
        super(source, null, sourceFilter, condition, ModifierType.MAY_NOT_LOOK_AT_OPPONENTS_HAND);
        _playerId = playerId;
        _sourceFilter = Filters.and(sourceFilter);
    }

    @Override
    public boolean isActionSource(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard actionSource) {
        return actionSource == null || Filters.and(_sourceFilter).accepts(gameState, modifiersQuerying, actionSource);
    }
}
