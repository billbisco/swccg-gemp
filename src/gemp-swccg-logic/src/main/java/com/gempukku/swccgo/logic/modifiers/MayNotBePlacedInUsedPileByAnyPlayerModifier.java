package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/** Prevents affected cards from being placed in Used Pile, regardless of the placing player. */
public class MayNotBePlacedInUsedPileByAnyPlayerModifier extends MayNotBePlacedInUsedPileModifier {
    public MayNotBePlacedInUsedPileByAnyPlayerModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    public MayNotBePlacedInUsedPileByAnyPlayerModifier(PhysicalCard source, Filterable affectFilter,
                                                     Condition condition) {
        super(source, affectFilter, condition);
    }
}
