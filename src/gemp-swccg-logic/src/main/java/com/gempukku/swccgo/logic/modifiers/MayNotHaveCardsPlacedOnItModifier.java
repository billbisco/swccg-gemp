package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that prevents the specified player from placing cards on cards accepted by the filter.
 */
public class MayNotHaveCardsPlacedOnItModifier extends AbstractModifier {

    public MayNotHaveCardsPlacedOnItModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        super(source, "May not have cards placed on it", affectFilter, null, ModifierType.MAY_NOT_HAVE_CARDS_PLACED_ON_IT);
        _playerId = playerId;
    }
}
