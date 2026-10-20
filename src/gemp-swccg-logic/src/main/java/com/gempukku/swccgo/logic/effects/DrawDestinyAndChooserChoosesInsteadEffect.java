package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the current destiny draw to instead draw X, with a specified player (who need not be the
 * player drawing) choosing Y of them. Example: "if opponent is about to draw a weapon destiny targeting Han, they draw
 * two and you choose one".
 */
public class DrawDestinyAndChooserChoosesInsteadEffect extends AbstractSuccessfulEffect {
    private final int _drawX;
    private final int _chooseY;
    private final String _chooserPlayerId;

    /**
     * Creates an effect that causes the current destiny draw to instead draw X, with the specified player choosing Y.
     * @param action the action performing this effect
     * @param drawX number of destiny to draw
     * @param chooseY number of destiny to choose
     * @param chooserPlayerId the player who chooses the destiny
     */
    public DrawDestinyAndChooserChoosesInsteadEffect(Action action, int drawX, int chooseY, String chooserPlayerId) {
        super(action);
        _drawX = drawX;
        _chooseY = chooseY;
        _chooserPlayerId = chooserPlayerId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (drawDestinyEffect.canDrawAndChoose(game, _chooseY)) {
                gameState.sendMessage(drawDestinyEffect.getPlayerDrawingDestiny() + " will draw " + _drawX + " "
                        + drawDestinyEffect.getDestinyType().getHumanReadable() + " and " + _chooserPlayerId + " will choose " + _chooseY);
                drawDestinyEffect.setDrawXAndChooseY(_drawX, _chooseY);
                drawDestinyEffect.setDrawXChooser(_chooserPlayerId);
            }
        }
    }
}
