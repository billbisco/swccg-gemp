package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents specified cards from canceling duels.
 */
public class MayNotCancelDuelModifier extends AbstractModifier {
    private Filter _cardFilter;

    /**
     * Creates a modifier that prevents cards accepted by the filter from canceling duels.
     * @param source the source of the modifier
     * @param cardFilter the cards that may not cancel duels
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotCancelDuelModifier(PhysicalCard source, Filterable cardFilter, Condition condition) {
        super(source, "May not cancel duels", source, condition, ModifierType.MAY_NOT_CANCEL_DUEL, true);
        _cardFilter = Filters.and(cardFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not cancel duels";
    }

    public boolean mayNotCancelDuel(SwccgGame game, PhysicalCard cardCanceling) {
        return cardCanceling != null && _cardFilter.accepts(game, cardCanceling);
    }
}
