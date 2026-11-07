package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents cards accepted by a filter (for example, "Wedge") from canceling battle destiny draws
 * at battle locations accepted by a location filter. Only cancels performed by card game text that passes its own
 * card to the "can cancel destiny" check are affected; other cancels are not restricted by this modifier.
 */
public class MayNotCancelBattleDestinyUsingCardModifier extends AbstractModifier {
    private final Filter _cancelingCardFilter;

    /**
     * Creates a modifier that prevents cards accepted by the canceling card filter from canceling battle destiny
     * draws at battle locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param cancelingCardFilter the filter for cards that may not cancel battle destiny draws
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     */
    public MayNotCancelBattleDestinyUsingCardModifier(PhysicalCard source, Filterable locationFilter, Filterable cancelingCardFilter, Condition condition) {
        super(source, null, Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.MAY_NOT_CANCEL_BATTLE_DESTINY_USING_CARD, true);
        _cancelingCardFilter = Filters.and(cancelingCardFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    /**
     * Determines if the specified card may not cancel battle destiny draws.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param cancelingCard the card whose game text would cancel the battle destiny
     * @return true if the card may not cancel battle destiny draws, otherwise false
     */
    public boolean mayNotCancelBattleDestinyUsingCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cancelingCard) {
        return cancelingCard != null && _cancelingCardFilter.accepts(gameState, modifiersQuerying, cancelingCard);
    }
}
