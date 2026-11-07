package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

/** Limits the sum of positive drain modifiers for one player at the affected locations. */
public final class LimitForceDrainBonusesModifier extends AbstractModifier {
    private final int limit;

    public LimitForceDrainBonusesModifier(PhysicalCard source, Filterable locations, int limit, String playerId) {
        super(source, "Force drain bonuses limited to " + limit, Filters.and(Filters.location, locations), null,
                ModifierType.LIMIT_FORCE_DRAIN_BONUSES, true);
        if (limit < 0) throw new IllegalArgumentException("Force drain bonus limit must be nonnegative");
        this.limit = limit;
        _playerId = playerId;
    }

    public int getLimit() {
        return limit;
    }
}
