package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected Operatives to not be able to be deployed to moved to same location on matching planet
 * as another of that player's Operatives of same card title.
 */
public class MayNotDeployOrMoveOperativeToLocationsRuleModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected Operatives to not be able to be deployed to moved to same location on matching planet
     * as another of that player's Operatives of same card title.
     * @param source the source of the modifier
     */
    public MayNotDeployOrMoveOperativeToLocationsRuleModifier(PhysicalCard source) {
        this(source, null);
    }

    public MayNotDeployOrMoveOperativeToLocationsRuleModifier(PhysicalCard source, Condition condition) {
        super(source, "May not deploy or move to same matching planet locations as another matching operative", Filters.operative, condition, ModifierType.MAY_NOT_DEPLOY_MOVE_OPERATIVE_RULE);
    }
}
