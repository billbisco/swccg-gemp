package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A "may not add more than X destiny to total attrition" modifier. Caps the number of destiny a player adds to
 * total attrition in a battle from all sources (destiny draws "to attrition only" and cards that "add X destiny to attrition").
 * Ordinary battle destiny draws are not affected. When several of these modifiers apply, the lowest limit is used.
 */
public class MayNotAddMoreThanDestinyToAttritionModifier extends AbstractModifier {
    private final Evaluator _evaluator;

    /**
     * Creates a "may not add more than X destiny to total attrition" modifier.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     * @param maximum the maximum number of destiny the player may add
     * @param playerId the player that is limited, or null for both players
     */
    public MayNotAddMoreThanDestinyToAttritionModifier(PhysicalCard source, Condition condition, int maximum, String playerId) {
        this(source, condition, new ConstantEvaluator(maximum), playerId);
    }

    /**
     * Creates a "may not add more than X destiny to total attrition" modifier.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     * @param evaluator the evaluator that calculates the maximum number of destiny the player may add
     * @param playerId the player that is limited, or null for both players
     */
    public MayNotAddMoreThanDestinyToAttritionModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.battleLocation, condition, ModifierType.MAX_DESTINY_DRAWS_TO_ATTRITION, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String who = _playerId == null ? "Neither player" : (_playerId.equals(gameState.getDarkPlayer()) ? "Dark side" : "Light side");
        return who + (_playerId == null ? " may add more than " : " may not add more than ") + value + " destiny to total attrition";
    }

    /**
     * Gets the maximum number of destiny the affected player may add.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card unused
     * @return the maximum
     */
    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return Math.max(0, (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, card));
    }
}
