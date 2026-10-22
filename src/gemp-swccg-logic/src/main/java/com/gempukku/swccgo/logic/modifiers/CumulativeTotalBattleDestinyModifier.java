package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * Total battle destiny modifier that stacks with other copies of the same title (printed "cumulatively").
 */
public class CumulativeTotalBattleDestinyModifier extends TotalBattleDestinyModifier {
    public CumulativeTotalBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, float modifierAmount, String playerId) {
        super(source, locationFilter, condition, modifierAmount, playerId);
    }

    @Override
    public boolean isCumulative() {
        return true;
    }
}
