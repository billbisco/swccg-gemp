package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Printed Cloud City face: either player, including this Interrupt's owner. */
public class Card5_149o extends Card5_149 {
    public Card5_149o() {
        setGameText("Cancel Anger, Fear, Aggression when it is inserted or revealed. OR A player who just initiated a battle or Force drain must use 2 Force or it is canceled.");
        setTextState(new TextState("5_149", "Printed",
                "Cloud City printed face (November 1997); dates are bounds: publication by month-end, opponent-only erratum documented by Decipher supplement 30 May 2001, page 9; exact announcement unknown",
                LocalDate.of(1997, 11, 30), LocalDate.of(2001, 5, 30)));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
                                                                       EffectResult result, PhysicalCard self) {
        // The inherited modes already handle the opponent and revealed inserts.
        List<PlayInterruptAction> actions = new ArrayList<>(
                super.getGameTextOptionalAfterActions(playerId, game, result, self));
        if (TriggerConditions.battleInitiated(game, result, playerId))
            actions.add(ownAction(game, self, playerId, false));
        if (TriggerConditions.forceDrainInitiatedBy(game, result, playerId)
                && GameConditions.canCancelForceDrain(game, self))
            actions.add(ownAction(game, self, playerId, true));
        return actions;
    }

    private PlayInterruptAction ownAction(SwccgGame game, PhysicalCard self, String playerId, boolean drain) {
        PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Use 2 Force or cancel " + (drain ? "Force drain" : "battle"));
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                if (!GameConditions.canUseForce(game, playerId, 2)) {
                    cancel();
                    return;
                }
                action.appendEffect(new PlayoutDecisionEffect(action, playerId,
                        new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 2 Force",
                                drain ? "Cancel Force drain" : "Cancel battle"}) {
                            @Override
                            protected void validDecisionMade(int index, String response) {
                                if (index == 0)
                                    action.appendEffect(new UseForceEffect(action, playerId, 2));
                                else
                                    cancel();
                            }
                        }));
            }

            private void cancel() {
                action.appendEffect(drain ? new CancelForceDrainEffect(action) : new CancelBattleEffect(action));
            }
        });
        return action;
    }
}
