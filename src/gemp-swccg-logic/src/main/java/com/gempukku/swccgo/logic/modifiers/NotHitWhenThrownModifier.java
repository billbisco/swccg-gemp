package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for cards that are not 'hit' when 'thrown' (for example, a vehicle or starship 'thrown' by Vader or Galen).
 * The card may still be targeted and 'thrown'; only the 'hit' result is skipped. See {@link com.gempukku.swccgo.logic.effects.ThrowCardEffect}.
 */
public class NotHitWhenThrownModifier extends AbstractModifier {

    /**
     * Creates a modifier for cards that are not 'hit' when 'thrown'.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not 'hit' when 'thrown'
     */
    public NotHitWhenThrownModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for cards that are not 'hit' when 'thrown'.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not 'hit' when 'thrown'
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public NotHitWhenThrownModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Not 'hit' when 'thrown'", affectFilter, condition, ModifierType.NOT_HIT_WHEN_THROWN, true);
    }
}
