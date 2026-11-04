package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Interrupt
 * Subtype: Lost
 * Title: Join Me! (V)
 */
public class Card1291_013 extends AbstractLostInterrupt {
    public Card1291_013() {
        super(Side.DARK, 4, "Join Me!", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'...And together, we can rule the galaxy as father and son!'");
        setGameText("During battle, lose 1 Force to target a Skywalker present with your character armed with a lightsaber. Target is power = 0. OR If you just used a lightsaber to hit a Skywalker, opponent must lose 3 Force or place Skywalker (and all cards on them) in Used Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter yourCharacterArmedWithLightsaber = Filters.and(Filters.your(self), Filters.character, Filters.armedWith(Filters.lightsaber));
        Filter targetFilter = Filters.and(Filters.Skywalker, Filters.character, Filters.presentWith(self, yourCharacterArmedWithLightsaber));

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make a Skywalker power = 0");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Skywalker", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new LoseForceEffect(action, playerId, 1, true));
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " power = 0",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ResetPowerUntilEndOfBattleEffect(action, finalTarget, 0));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.Skywalker, Filters.lightsaber, Filters.your(self))) {
            final PhysicalCard skywalker = ((HitResult) effectResult).getCardHit();
            if (GameConditions.canTarget(game, self, skywalker)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make opponent lose 3 Force or place " + GameUtils.getFullName(skywalker) + " in Used Pile");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Skywalker", skywalker) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Make opponent lose 3 Force or place " + GameUtils.getCardLink(targetedCard) + " in Used Pile",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, opponent,
                                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Lose 3 Force", "Place " + GameUtils.getFullName(finalTarget) + " in Used Pile"}) {
                                                                    @Override
                                                                    protected void validDecisionMade(int index, String result) {
                                                                        if (index == 0) {
                                                                            game.getGameState().sendMessage(opponent + " chooses to lose 3 Force");
                                                                            action.appendEffect(
                                                                                    new LoseForceEffect(action, opponent, 3));
                                                                        } else {
                                                                            game.getGameState().sendMessage(opponent + " chooses to place " + GameUtils.getCardLink(finalTarget) + " in Used Pile");
                                                                            action.appendEffect(
                                                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget, false, Zone.USED_PILE));
                                                                        }
                                                                    }
                                                                }));
                                            }
                                        });
                            }
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
