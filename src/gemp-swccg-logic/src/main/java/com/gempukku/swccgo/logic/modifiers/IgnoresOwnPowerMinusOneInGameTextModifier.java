package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to ignore 'power -1' in their own game text (a power modifier of exactly -1
 * whose source is the affected card itself).
 */
public class IgnoresOwnPowerMinusOneInGameTextModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore 'power -1' in their own game text.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public IgnoresOwnPowerMinusOneInGameTextModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore 'power -1' in their own game text.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public IgnoresOwnPowerMinusOneInGameTextModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Ignores 'power -1' in own game text", affectFilter, condition, ModifierType.IGNORES_OWN_POWER_MINUS_ONE_IN_GAME_TEXT);
    }
}
