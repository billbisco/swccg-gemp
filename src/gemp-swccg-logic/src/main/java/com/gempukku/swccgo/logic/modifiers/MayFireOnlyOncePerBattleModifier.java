package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * Affected weapons may fire only once per battle (cancels fire-twice / fire-repeatedly grants).
 */
public class MayFireOnlyOncePerBattleModifier extends AbstractModifier {

    public MayFireOnlyOncePerBattleModifier(PhysicalCard source, Filterable weaponFilter) {
        this(source, weaponFilter, null);
    }

    public MayFireOnlyOncePerBattleModifier(PhysicalCard source, Filterable weaponFilter, Condition condition) {
        super(source, "May fire only once per battle", weaponFilter, condition, ModifierType.MAY_FIRE_ONLY_ONCE_PER_BATTLE, true);
    }
}
