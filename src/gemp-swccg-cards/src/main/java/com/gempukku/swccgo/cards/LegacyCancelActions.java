package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedForCancelingEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * Cancel actions whose cost is Force loss ("may lose X Force to cancel ..."), which CancelCardActionBuilder only builds
 * with a Force-use cost. Used by Captain Verrack (V) 1282_15 and Loje Nella (V) 1282_17.
 */
public final class LegacyCancelActions {

    private LegacyCancelActions() {
    }

    /**
     * Builds a game text action that targets and cancels a card being played; the cost is losing Force.
     * @param action the game text action to build
     * @param effect the playing card effect
     * @param forceToLose the amount of Force to lose (cannot be reduced)
     */
    public static void buildLoseForceToCancelCardBeingPlayedAction(final AbstractGameTextAction action, final Effect effect, final int forceToLose) {
        final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
        action.setText("Lose " + forceToLose + " Force to cancel " + GameUtils.getFullName(respondableEffect.getCard()));
        action.appendTargeting(
                new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                    @Override
                    protected void cardTargetedToBeCanceled(final PhysicalCard targetedCard) {
                        action.appendCost(
                                new LoseForceEffect(action, action.getPerformingPlayer(), forceToLose, true));
                        action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard),
                                new RespondableEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        action.appendEffect(
                                                new CancelCardBeingPlayedEffect(action, respondableEffect));
                                    }
                                });
                    }
                });
    }
}
