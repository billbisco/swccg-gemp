package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * Center Of Tyranny (Virtual Block 7.3): "Your destiny draws for [Virtual Block 1] Sabotage are -1 and may not be
 * further reduced." While a player draws destiny for an action whose source card is accepted by the affect filter, each
 * of those destiny draws may not end up lower than the drawn value minus the maximum reduction held by this modifier
 * (the -1 itself is a separate {@link DestinyDrawForActionSourceModifier}). Increases still apply. Applied by
 * {@link #applyLimit} from the destiny draw calculation (swccg-guild/work#346).
 */
public class LegacyDestinyDrawForActionSourceMayNotBeFurtherReducedModifier extends AbstractModifier {
    private final float _maxReduction;

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for action source cards whose destiny draws are protected
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     * @param maxReduction the largest total reduction allowed
     * @param playerId the player whose destiny draws are protected
     */
    public LegacyDestinyDrawForActionSourceMayNotBeFurtherReducedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float maxReduction, String playerId) {
        super(source, null, affectFilter, condition, ModifierType.LEGACY__DESTINY_DRAW_FOR_ACTION_SOURCE_MAY_NOT_BE_FURTHER_REDUCED, true);
        _maxReduction = maxReduction;
        _playerId = playerId;
    }

    /**
     * Creates the modifier with no condition.
     * @param source the source of the modifier
     * @param affectFilter the filter for action source cards whose destiny draws are protected
     * @param maxReduction the largest total reduction allowed
     * @param playerId the player whose destiny draws are protected
     */
    public LegacyDestinyDrawForActionSourceMayNotBeFurtherReducedModifier(PhysicalCard source, Filterable affectFilter, float maxReduction, String playerId) {
        this(source, affectFilter, null, maxReduction, playerId);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Destiny draws for it may not be reduced by more than " + GuiUtils.formatAsString(_maxReduction);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _maxReduction;
    }

    /**
     * Limits a destiny draw for an action source when a modifier of this type protects that player's draws for it.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param playerId the player drawing destiny
     * @param destinyDrawActionSource the source card of the draw destiny action, or null
     * @param drawnValue the destiny value before modifiers
     * @param value the modified destiny value
     * @return the limited value
     */
    public static float applyLimit(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId, PhysicalCard destinyDrawActionSource, float drawnValue, float value) {
        if (destinyDrawActionSource == null)
            return value;
        Float maxReduction = null;
        for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.LEGACY__DESTINY_DRAW_FOR_ACTION_SOURCE_MAY_NOT_BE_FURTHER_REDUCED, destinyDrawActionSource)) {
            if (modifier.isForPlayer(playerId)) {
                float amount = modifier.getValue(gameState, modifiersQuerying, destinyDrawActionSource);
                maxReduction = (maxReduction == null) ? amount : Math.min(maxReduction, amount);
            }
        }
        if (maxReduction == null)
            return value;
        return Math.max(value, drawnValue - maxReduction);
    }
}
