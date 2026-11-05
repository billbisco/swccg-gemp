package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A modifier that prevents cards accepted by the filter from being 'blown away'.
 */
public class MayNotBeBlownAwayModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'blown away'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeBlownAwayModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'blown away'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeBlownAwayModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be 'blown away'", affectFilter, condition, ModifierType.LEGACY__MAY_NOT_BE_BLOWN_AWAY);
    }

    /**
     * Gets the cards that may be 'blown away' (those not affected by a modifier of this type).
     * @param game the game
     * @param cards the cards about to be 'blown away'
     * @return the cards that may still be 'blown away'
     */
    public static Collection<PhysicalCard> mayBeBlownAway(SwccgGame game, Collection<PhysicalCard> cards) {
        List<PhysicalCard> result = new ArrayList<PhysicalCard>();
        for (PhysicalCard card : cards) {
            if (game.getModifiersQuerying().getModifiersAffectingCard(game.getGameState(), ModifierType.LEGACY__MAY_NOT_BE_BLOWN_AWAY, card).isEmpty()) {
                result.add(card);
            }
        }
        return result;
    }
}
