package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Virtual Block 7.3 Kessel cards: cards stacked on Spice Mine Operations are 'spice' cards.
 */
public final class LegacySpiceCards {
    public static final String SPICE_MINE_OPERATIONS = "Spice Mine Operations";
    public static final Filter SPICE_MINE_OPERATIONS_FILTER = Filters.title(SPICE_MINE_OPERATIONS);

    private LegacySpiceCards() {
    }

    /**
     * Gets the 'spice' cards: the cards stacked on any Spice Mine Operations on table.
     * @param game the game
     * @param source the card performing the query
     * @param filter an extra filter the spice cards must match
     * @return the spice cards
     */
    public static List<PhysicalCard> spiceCards(SwccgGame game, PhysicalCard source, Filterable filter) {
        List<PhysicalCard> result = new ArrayList<PhysicalCard>();
        for (PhysicalCard operations : Filters.filterActive(game, source, SPICE_MINE_OPERATIONS_FILTER)) {
            for (PhysicalCard stacked : game.getGameState().getStackedCards(operations)) {
                if (Filters.and(filter).accepts(game, stacked)) {
                    result.add(stacked);
                }
            }
        }
        return result;
    }

    /**
     * @return the number of 'spice' cards on Spice Mine Operations
     */
    public static int countSpiceCards(SwccgGame game, PhysicalCard source) {
        return spiceCards(game, source, Filters.any).size();
    }
}
