package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that lets the affected starfighters add the ability of their pilot to their total when targeted by
 * Tallon Roll.
 */
public class MayAddPilotAbilityWhenTargetedByTallonRollModifier extends AbstractModifier {

    /**
     * Creates a modifier that lets starfighters accepted by the filter add the ability of their pilot to their total
     * when targeted by Tallon Roll.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayAddPilotAbilityWhenTargetedByTallonRollModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May add ability of pilot to total when targeted by Tallon Roll", affectFilter, condition, ModifierType.MAY_ADD_PILOT_ABILITY_WHEN_TARGETED_BY_TALLON_ROLL, true);
    }

    /**
     * Determines if the specified starfighter may add the ability of its pilot to its total when targeted by Tallon Roll.
     * @param modifiersQuerying the modifiers querying
     * @param gameState the game state
     * @param starfighter the targeted starfighter
     * @return true or false
     */
    public static boolean appliesTo(ModifiersQuerying modifiersQuerying, GameState gameState, PhysicalCard starfighter) {
        return !modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.MAY_ADD_PILOT_ABILITY_WHEN_TARGETED_BY_TALLON_ROLL, starfighter).isEmpty();
    }
}
