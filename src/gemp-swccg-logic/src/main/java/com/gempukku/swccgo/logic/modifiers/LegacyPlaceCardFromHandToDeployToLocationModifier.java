package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for a non-Force deploy cost: to deploy an affected card to an affected location (or to a card at that
 * location), the player deploying it must first place a card from hand on bottom of their Used Pile.
 * Watch Your Step (V): "For either player to deploy a card with ability (except a Corellian) to Corellia, that player
 * must first place a card from hand on bottom of their Used Pile."
 */
public class LegacyPlaceCardFromHandToDeployToLocationModifier extends AbstractModifier {
    private final Filter _targetFilter;

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deployment carries the cost
     * @param locationFilter the filter for locations the cost applies to
     */
    public LegacyPlaceCardFromHandToDeployToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        super(source, "Must first place a card from hand on bottom of Used Pile to deploy here", Filters.and(Filters.not(Filters.in_play), affectFilter),
                null, ModifierType.LEGACY__PLACE_CARD_FROM_HAND_TO_DEPLOY_TO_TARGET, true);
        _targetFilter = Filters.locationAndCardsAtLocation(Filters.and(locationFilter));
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return target != null && _targetFilter.accepts(gameState, modifiersQuerying, target);
    }
}
