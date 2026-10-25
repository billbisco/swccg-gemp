package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the specified player from having a senate majority.
 */
public class MayNotHaveSenateMajorityModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the specified player from having a senate majority.
     * @param source the source of the modifier
     * @param playerId the player who may not have a senate majority
     */
    public MayNotHaveSenateMajorityModifier(PhysicalCard source, String playerId) {
        this(source, null, playerId);
    }

    /**
     * Creates a modifier that prevents the specified player from having a senate majority.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player who may not have a senate majority
     */
    public MayNotHaveSenateMajorityModifier(PhysicalCard source, Condition condition, String playerId) {
        super(source, "May not have senate majority", source, condition, ModifierType.MAY_NOT_HAVE_SENATE_MAJORITY, true);
        _playerId = playerId;
    }
}
