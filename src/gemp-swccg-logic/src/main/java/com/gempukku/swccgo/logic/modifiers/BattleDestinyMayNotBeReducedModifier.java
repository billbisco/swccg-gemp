package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents the specified player's battle destiny draws (and total battle destiny) in battles at the
 * affected locations from being reduced. Increases still apply; only reductions (negative modifiers and multipliers
 * below 1) are ignored.
 */
public class BattleDestinyMayNotBeReducedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the specified player's battle destiny draws (and total battle destiny) in
     * battles at locations accepted by the filter from being reduced.
     * @param source the source of the modifier
     * @param locationFilter the battle location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player whose battle destiny may not be reduced
     */
    public BattleDestinyMayNotBeReducedModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, "Battle destiny draws (and total battle destiny) may not be reduced", Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.BATTLE_DESTINY_MAY_NOT_BE_REDUCED, true);
        _playerId = playerId;
    }

    /**
     * Determines if the specified player's battle destiny draws (and total battle destiny) in the current battle may
     * not be reduced.
     * @param modifiersQuerying the modifiers querying
     * @param gameState the game state
     * @param playerId the player drawing battle destiny
     * @return true or false
     */
    public static boolean appliesTo(ModifiersQuerying modifiersQuerying, GameState gameState, String playerId) {
        if (gameState.getBattleState() == null)
            return false;
        PhysicalCard battleLocation = gameState.getBattleState().getBattleLocation();
        if (battleLocation == null || playerId == null)
            return false;
        for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.BATTLE_DESTINY_MAY_NOT_BE_REDUCED, battleLocation)) {
            if (modifier.isForPlayer(playerId))
                return true;
        }
        return false;
    }

    /**
     * Returns the amount to apply to the player's battle destiny (or total battle destiny): the amount itself, or 0
     * if it is a reduction that may not be applied.
     * @param modifiersQuerying the modifiers querying
     * @param gameState the game state
     * @param playerId the player drawing battle destiny
     * @param amount the modifier amount
     * @return the amount to apply
     */
    public static float unlessReduction(ModifiersQuerying modifiersQuerying, GameState gameState, String playerId, float amount) {
        if (amount < 0 && appliesTo(modifiersQuerying, gameState, playerId))
            return 0;
        return amount;
    }
}
