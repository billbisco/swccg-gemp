package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents the specified player from increasing the deploy cost of affected cards when they deploy
 * to (or aboard something at) a location accepted by the location filter.
 */
public class MayNotHaveDeployCostIncreasedAtLocationModifier extends AbstractModifier {
    private final Filter _locationFilter;

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards being deployed
     * @param locationFilter the filter for the locations deployed to
     * @param playerId the player that may not increase deploy cost, or null for any player
     */
    public MayNotHaveDeployCostIncreasedAtLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter, String playerId) {
        this(source, affectFilter, locationFilter, null, playerId);
    }

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards being deployed
     * @param locationFilter the filter for the locations deployed to
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not increase deploy cost, or null for any player
     */
    public MayNotHaveDeployCostIncreasedAtLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter, Condition condition, String playerId) {
        super(source, "May not have deploy cost increased here", affectFilter, condition, ModifierType.MAY_NOT_HAVE_DEPLOY_COST_INCREASED_AT_LOCATION, true);
        _locationFilter = Filters.and(locationFilter);
        _playerId = playerId;
    }

    /**
     * Determines if the modifier applies when deploying to the specified location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param location the location deployed to
     * @return true or false
     */
    public boolean appliesToDeployLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _locationFilter.accepts(gameState, modifiersQuerying, location);
    }
}
