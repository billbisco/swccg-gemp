package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * Sets a just-drawn destiny to a value even if that destiny was already canceled.
 */
public class SetDestinyEvenIfCanceledEffect extends AbstractSuccessfulEffect {
    private final float _resetValue;

    public SetDestinyEvenIfCanceledEffect(Action action, float resetValue) {
        super(action);
        _resetValue = resetValue;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Set destiny to " + GuiUtils.formatAsString(_resetValue);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState == null) {
            return;
        }
        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
        if (drawDestinyEffect.getDrawnDestinyCard() == null && drawDestinyEffect.getSubstituteDestiny() == null
                && !drawDestinyEffect.isDestinyCanceled()) {
            return;
        }
        drawDestinyEffect.resetDestiny(_resetValue);
        String playerId = _action.getPerformingPlayer();
        if (playerId != null) {
            gameState.sendMessage(playerId + " sets " + drawDestinyEffect.getDestinyType().getHumanReadable()
                    + " to " + GuiUtils.formatAsString(_resetValue));
        }
        else {
            gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " sets "
                    + drawDestinyEffect.getDestinyType().getHumanReadable() + " to " + GuiUtils.formatAsString(_resetValue));
        }
    }
}
