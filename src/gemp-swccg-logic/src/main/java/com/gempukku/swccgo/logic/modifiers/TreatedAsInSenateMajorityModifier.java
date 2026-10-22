package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that treats the affected characters as being in a senate majority (Geonosis: Separatist Council Room
 * 1291_19: "While two senators here, they are in a senate majority").
 */
public class TreatedAsInSenateMajorityModifier extends AbstractModifier {

    /**
     * Creates a modifier that treats the affected characters as being in a senate majority.
     * @param source the source of the modifier
     * @param affectFilter the characters treated as in a senate majority
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public TreatedAsInSenateMajorityModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "In a senate majority", affectFilter, condition, ModifierType.TREATED_AS_IN_SENATE_MAJORITY, true);
    }
}
