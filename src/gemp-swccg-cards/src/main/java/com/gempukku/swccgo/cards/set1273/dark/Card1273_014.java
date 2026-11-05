package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyUnlessRedrawnModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Interrupt
 * Subtype: Used
 * Title: A Dark Time For The Rebellion (V)
 */
public class Card1273_014 extends AbstractUsedInterrupt {
    public Card1273_014() {
        super(Side.DARK, 4, "A Dark Time For The Rebellion", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Absolute control wielded by the Emperor enables the Imperial forces to dominate planetary systems before the Rebel Alliance can gain a foothold.");
        setGameText("If opponent went first this game, retrieve 1 Force when you play this Interrupt. Add or subtract 1 from opponent's just drawn destiny. OR Activate up to 2 Force. OR Take Control into hand from Reserve Deck; reshuffle. OR Until end of turn, battle destiny draws may not be modified or canceled (unless being redrawn).");
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredInterruptPlayedTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<String> players = game.getGameState().getPlayerOrder().getAllPlayers();
        if (!players.isEmpty() && opponent.equals(players.get(0))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, self.getCardId());
            action.setText("Retrieve 1 Force");
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)) {
            final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
            action1.setText("Add 1 to destiny");
            action1.allowResponses(
                    new RespondablePlayCardEffect(action1) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action1.appendEffect(
                                    new ModifyDestinyEffect(action1, 1));
                        }
                    }
            );
            actions.add(action1);

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self);
            action2.setText("Subtract 1 from destiny");
            action2.allowResponses(
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action2.appendEffect(
                                    new ModifyDestinyEffect(action2, -1));
                        }
                    }
            );
            actions.add(action2);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.canActivateForce(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Activate up to 2 Force");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            int maxForceToActivate = Math.min(2, game.getGameState().getReserveDeckSize(playerId));
                            if (maxForceToActivate > 0) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new IntegerAwaitingDecision("Choose amount of Force to activate", 1, maxForceToActivate, maxForceToActivate) {
                                                    @Override
                                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                                        action.appendEffect(
                                                                new ActivateForceEffect(action, playerId, result));
                                                    }
                                                }));
                            }
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId takeActionId = GameTextActionId.LEGACY__A_DARK_TIME_FOR_THE_REBELLION_V__TAKE_CONTROL;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeActionId);
            action.setText("Take Control into hand from Reserve Deck");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Control), true));
                        }
                    }
            );
            actions.add(action);
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Affect battle destiny draws");
        action.setActionMsg("Prevent all battle destiny draws from being modified or canceled (unless being redrawn)");
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action,
                                        new MayNotModifyBattleDestinyModifier(self), "Prevents all battle destiny draws from being modified"));
                        action.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action,
                                        new MayNotCancelBattleDestinyUnlessRedrawnModifier(self), "Prevents all battle destiny draws from being canceled (unless being redrawn)"));
                    }
                }
        );
        actions.add(action);

        return actions;
    }
}
