package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents Force retrieval for the specified player from affected cards from being canceled.
 */
public class ForceRetrievalFromCardMayNotBeCanceledModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents Force retrieval for the specified player from affected cards from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose Force retrieval may not be canceled
     * @param playerRetrieving the player whose Force retrieval may not be canceled
     */
    public ForceRetrievalFromCardMayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter, String playerRetrieving) {
        this(source, affectFilter, null, playerRetrieving);
    }

    /**
     * Creates a modifier that prevents Force retrieval for the specified player from affected cards from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose Force retrieval may not be canceled
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerRetrieving the player whose Force retrieval may not be canceled
     */
    public ForceRetrievalFromCardMayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerRetrieving) {
        super(source, "Force retrieval may not be canceled", affectFilter, condition, ModifierType.FORCE_RETRIEVAL_FROM_CARD_MAY_NOT_BE_CANCELED);
        _playerId = playerRetrieving;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Force retrieval may not be canceled";
    }
}
