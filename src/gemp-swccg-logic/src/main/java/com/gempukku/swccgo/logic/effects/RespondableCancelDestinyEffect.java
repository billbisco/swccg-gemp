package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * A respondable effect for an attempt to cancel the just drawn destiny. Used with
 * {@code allowResponses(RespondableEffect)} so other cards may respond to (and cancel)
 * the attempt before the destiny is canceled. If the attempt is not canceled, the destiny is canceled as by
 * {@link CancelDestinyEffect}.
 */
public class RespondableCancelDestinyEffect extends RespondableEffect {
    private final Action _cancelingAction;
    private final DrawDestinyEffect _drawDestinyEffect;

    /**
     * Creates a respondable effect for an attempt to cancel the destiny currently being drawn.
     * @param action the action attempting to cancel the destiny
     * @param game the game
     */
    public RespondableCancelDestinyEffect(Action action, SwccgGame game) {
        super(action);
        _cancelingAction = action;
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        _drawDestinyEffect = drawDestinyState != null ? drawDestinyState.getDrawDestinyEffect() : null;
    }

    /**
     * Gets the destiny draw that this effect attempts to cancel.
     * @return the draw destiny effect, or null
     */
    public DrawDestinyEffect getDrawDestinyEffect() {
        return _drawDestinyEffect;
    }

    @Override
    protected void performActionResults(Action targetingAction) {
        _cancelingAction.appendEffect(new CancelDestinyEffect(_cancelingAction));
    }
}
