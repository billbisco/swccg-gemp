package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that replaces the player's location (and Jedi Master) Force icons with the opponent's when activating Force.
 * Personal Force generation is unchanged.
 */
public class UseOpponentsForceIconsWhenActivatingModifier extends AbstractModifier {

    /**
     * Creates a modifier that uses opponent's Force icons instead of yours when activating Force.
     * @param source the source of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public UseOpponentsForceIconsWhenActivatingModifier(PhysicalCard source, String playerId) {
        super(source, "Use opponent's Force icons when activating", null, ModifierType.TOTAL_FORCE_GENERATION);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = getTotalForceGenerationModifier(_playerId, gameState, modifiersQuerying);
        String sideText = gameState.getDarkPlayer().equals(_playerId) ? "Dark side" : "Light side";
        if (value >= 0)
            return sideText + " total Force generation +" + GuiUtils.formatAsString(value);
        return sideText + " total Force generation " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getTotalForceGenerationModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (!playerId.equals(_playerId)) {
            return 0;
        }
        String opponent = gameState.getOpponent(playerId);
        return modifiersQuerying.getTotalForceIconCount(gameState, opponent)
                - modifiersQuerying.getTotalForceIconCount(gameState, playerId);
    }
}
