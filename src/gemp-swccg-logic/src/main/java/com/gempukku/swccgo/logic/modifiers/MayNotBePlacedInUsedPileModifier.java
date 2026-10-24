package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents cards affected from being placed in Used Pile by the opponent of the source owner.
 */
public class MayNotBePlacedInUsedPileModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being placed in Used Pile.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBePlacedInUsedPileModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being placed in Used Pile.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBePlacedInUsedPileModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be placed in Used Pile", affectFilter, condition, ModifierType.MAY_NOT_BE_PLACED_IN_USED_PILE);
    }
}
