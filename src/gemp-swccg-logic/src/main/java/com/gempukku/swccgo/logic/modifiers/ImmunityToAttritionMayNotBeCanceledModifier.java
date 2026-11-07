package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents immunity to attrition from being canceled.
 */
public class ImmunityToAttritionMayNotBeCanceledModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents immunity to attrition of the source card from being canceled.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ImmunityToAttritionMayNotBeCanceledModifier(PhysicalCard source, Condition condition) {
        super(source, "Immunity to attrition may not be canceled", source, condition, ModifierType.IMMUNITY_TO_ATTRITION_MAY_NOT_BE_CANCELED);
    }

    /**
     * Creates a modifier that prevents the specified player from canceling immunity to attrition of cards accepted by
     * the filter. Only cancellations that come from cards owned by that player are prevented.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose immunity to attrition may not be canceled
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player who may not cancel the immunity to attrition
     */
    public ImmunityToAttritionMayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, "Immunity to attrition may not be canceled by " + playerId, affectFilter, condition, ModifierType.IMMUNITY_TO_ATTRITION_MAY_NOT_BE_CANCELED);
        _playerId = playerId;
    }
}
