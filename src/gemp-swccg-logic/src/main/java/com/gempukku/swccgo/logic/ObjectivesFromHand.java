package com.gempukku.swccgo.logic;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.ObjectiveSetupAction;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * swccg-guild/work#142 R3, game option "Objectives: deploy from hand" (Bill 2026-11-05, design A1). An Objective in
 * hand may be deployed for free during its owner's deploy phase when each card its setup requires is in that player's
 * hand or already on the table. Its setup then deploys those cards from hand instead of Reserve Deck, and a required
 * card already on the table counts as deployed.
 */
public final class ObjectivesFromHand {
    private ObjectivesFromHand() {
    }

    /**
     * @return true if the action is the setup of an Objective deployed from hand
     */
    public static boolean isSetupFromHand(Action action) {
        return action instanceof ObjectiveSetupAction && ((ObjectiveSetupAction) action).isSetupFromHand();
    }

    /**
     * The card pile a deploy effect takes cards from: hand when it is part of the setup of an Objective deployed from
     * hand and would take them from Reserve Deck, otherwise the pile itself.
     */
    public static Zone pile(Action action, Zone pile) {
        return pile == Zone.RESERVE_DECK && isSetupFromHand(action) ? Zone.HAND : pile;
    }

    /**
     * @return a card on the table the required card filter accepts: any location, or another card of the player's
     */
    public static PhysicalCard onTable(SwccgGame game, String playerId, Filterable required) {
        for (PhysicalCard card : Filters.filterAllOnTable(game, Filters.and(required, Filters.or(Filters.location, Filters.owner(playerId)))))
            return card;
        return null;
    }

    /**
     * Whether each required card is on the table or in the player's hand. A card in hand meets one requirement only.
     * @param required the required card filters, in setup order; null means the Objective can't be deployed from hand
     */
    public static boolean requirementsMet(SwccgGame game, String playerId, List<Filter> required) {
        if (required == null)
            return false;
        Set<PhysicalCard> used = new HashSet<PhysicalCard>();
        for (Filter filter : required) {
            if (onTable(game, playerId, filter) != null)
                continue;
            PhysicalCard match = null;
            for (PhysicalCard card : game.getGameState().getHand(playerId)) {
                if (!used.contains(card) && filter.accepts(game, card)) {
                    match = card;
                    break;
                }
            }
            if (match == null)
                return false;
            used.add(match);
        }
        return true;
    }
}
