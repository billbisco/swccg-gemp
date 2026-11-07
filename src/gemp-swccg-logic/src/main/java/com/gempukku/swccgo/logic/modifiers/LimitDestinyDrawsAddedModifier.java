package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that limits how many destiny draws each player (or the specified player) may add to (total) power, or to
 * attrition, during a battle (for example: "neither player may add more than two destiny to power").
 */
public class LimitDestinyDrawsAddedModifier extends AbstractModifier {
    private final int _limit;

    /**
     * Creates a modifier limiting destiny draws added to (total) power or to attrition.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     * @param toAttrition true to limit destiny draws added to attrition, false to limit destiny draws added to power
     * @param limit the maximum number of destiny draws that may be added
     * @param playerId the player limited, or null for both players
     */
    public LimitDestinyDrawsAddedModifier(PhysicalCard source, Condition condition, boolean toAttrition, int limit, String playerId) {
        super(source, null, null, condition, toAttrition ? ModifierType.LIMIT_DESTINY_DRAWS_ADDED_TO_ATTRITION : ModifierType.LIMIT_DESTINY_DRAWS_ADDED_TO_POWER, true);
        _limit = limit;
        _playerId = playerId;
    }

    public int getLimit() {
        return _limit;
    }

    @Override
    public boolean isForPlayer(String playerId) {
        return _playerId == null || _playerId.equals(playerId);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not add more than " + _limit + " destiny to "
                + (getModifierType() == ModifierType.LIMIT_DESTINY_DRAWS_ADDED_TO_ATTRITION ? "attrition" : "power");
    }
}
