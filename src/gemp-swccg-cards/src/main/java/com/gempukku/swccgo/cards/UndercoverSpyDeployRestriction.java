package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * Implemented by a character blueprint whose "may deploy as an Undercover spy" option is limited to certain
 * locations (e.g. Boushh (V): "May deploy as an Undercover spy to Coruscant, Tatooine, or same site as a gangster").
 * Deploying normally is not affected; only the Undercover choice is offered or not.
 */
public interface UndercoverSpyDeployRestriction {

    /**
     * Gets the filter for locations where the character may deploy as an Undercover spy.
     * @param game the game
     * @param self the character
     * @return the filter
     */
    Filter getMayDeployAsUndercoverSpyToFilter(SwccgGame game, PhysicalCard self);

    /**
     * Determines if the character may choose to deploy as an Undercover spy to the target location.
     * @param game the game
     * @param character the character
     * @param target the deploy target
     * @return true or false
     */
    static boolean mayDeployAsUndercoverSpyTo(SwccgGame game, PhysicalCard character, PhysicalCard target) {
        if (character.getBlueprint() instanceof UndercoverSpyDeployRestriction) {
            Filter filter = ((UndercoverSpyDeployRestriction) character.getBlueprint()).getMayDeployAsUndercoverSpyToFilter(game, character);
            return filter.accepts(game, target);
        }
        return true;
    }
}
