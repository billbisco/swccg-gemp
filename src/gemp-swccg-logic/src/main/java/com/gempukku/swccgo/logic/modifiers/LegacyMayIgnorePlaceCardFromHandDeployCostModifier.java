package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that lets each player, once per turn, ignore a "place a card from hand on bottom of Used Pile to deploy"
 * cost ({@link LegacyPlaceCardFromHandToDeployToLocationModifier}) whose source matches a filter.
 * Begin Landing Your Troops &amp; The Dark Path: "Once per turn, each player may ignore any deploy cost on Watch Your Step."
 */
public class LegacyMayIgnorePlaceCardFromHandDeployCostModifier extends AbstractModifier {
    private final Filter _costSourceFilter;
    private final GameTextActionId _usageId;

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param costSourceFilter the filter for the cards whose deploy cost may be ignored
     * @param usageId the identifier used to count each player's once-per-turn use
     */
    public LegacyMayIgnorePlaceCardFromHandDeployCostModifier(PhysicalCard source, Filterable costSourceFilter, GameTextActionId usageId) {
        super(source, "Once per turn, each player may ignore a deploy cost", null, null, ModifierType.LEGACY__MAY_IGNORE_PLACE_CARD_FROM_HAND_TO_DEPLOY_COST, true);
        _costSourceFilter = Filters.and(costSourceFilter);
        _usageId = usageId;
    }

    /**
     * Determines if the cost from the specified source may be ignored by this modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param costSource the source card of the deploy cost
     * @return true or false
     */
    public boolean ignoresCostFrom(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard costSource) {
        return costSource != null && _costSourceFilter.accepts(gameState, modifiersQuerying, costSource);
    }

    /**
     * Determines if the player has not yet used this modifier this turn.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param playerId the player
     * @return true or false
     */
    public boolean isUnusedThisTurn(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId) {
        PhysicalCard source = getSource(gameState);
        return source != null && modifiersQuerying.getUntilEndOfTurnLimitCounter(source, playerId, source.getCardId(), _usageId).getUsedLimit() < 1;
    }

    /**
     * Records the player's use of this modifier this turn.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param playerId the player
     */
    public void recordUse(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId) {
        PhysicalCard source = getSource(gameState);
        if (source != null)
            modifiersQuerying.getUntilEndOfTurnLimitCounter(source, playerId, source.getCardId(), _usageId).incrementToLimit(1, 1);
    }
}
