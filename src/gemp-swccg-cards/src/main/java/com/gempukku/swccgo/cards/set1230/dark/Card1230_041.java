package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Dead Ewok
 */
public class Card1230_041 extends AbstractUsedInterrupt {
    public Card1230_041() {
        super(Side.DARK, 5, Title.Dead_Ewok, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Many Ewoks gave their lives in the Battle of Endor.");
        setGameText("Cancel Ellors Madak. (Immune to Sense.) OR Use 2 Force to cancel the game text of an opponent's alien for remainder of turn. OR Place an Ewok Catapult in opponent's Used Pile. OR Make an Ewok power and forfeit = 0 for remainder of turn.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        if (GameConditions.canTargetToCancel(game, self, Filters.title(Title.Ellorrs_Madak))) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Ellorrs_Madak), Title.Ellorrs_Madak);
            actions.add(action);
        }

        Filter alien = Filters.and(Filters.opponents(self), Filters.alien);
        if (GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTarget(game, self, alien)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel opponent's alien game text");
            action.appendCost(new UseForceEffect(action, playerId, 2));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose alien", alien) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }

        Filter catapult = Filters.and(Filters.opponents(self), Filters.title("Ewok Catapult"));
        if (GameConditions.canTarget(game, self, catapult)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place Ewok Catapult in Used Pile");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Ewok Catapult", catapult) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }

        Filter ewok = Filters.Ewok;
        if (GameConditions.canTarget(game, self, ewok)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reset an Ewok's power and forfeit to 0");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Ewok", ewok) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new ResetPowerUntilEndOfTurnEffect(action, finalTarget, 0));
                                            action.appendEffect(
                                                    new ResetForfeitUntilEndOfTurnEffect(action, finalTarget, 0));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Ellorrs_Madak))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
