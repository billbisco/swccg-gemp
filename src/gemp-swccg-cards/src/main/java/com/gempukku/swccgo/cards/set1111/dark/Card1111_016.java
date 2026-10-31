package com.gempukku.swccgo.cards.set1111.dark;

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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SuspendCardUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardsIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Interrupt
 * Subtype: Used
 * Title: Cold Feet
 */
public class Card1111_016 extends AbstractUsedInterrupt {
    public Card1111_016() {
        super(Side.DARK, 5, Title.Cold_Feet, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("Wampas pack snow around the appendages of captured prey, making use of Hoth's cold environment to immobilize them.");
        setGameText("Take a 'grabber' into hand (or play a Defensive Shield) from under your Starting Effect. OR Cancel It Can Wait, Surprise Assault, or Yerka Mig. OR Suspend Bacta Tank ('patient' remains on Effect), Bo Shuda, Goo Nee Tay, or No Questions Asked for remainder of turn.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null) {
            if (GameConditions.hasStackedCards(game, startingEffect, Filters.grabber)) {
                final PhysicalCard stackedOn = startingEffect;
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Take grabber into hand from Starting Effect");
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new TakeStackedCardsIntoHandEffect(action, playerId, 1, 1, stackedOn, Filters.grabber));
                    }
                });
                actions.add(action);
            }

            Filter shieldFilter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
            if (GameConditions.hasStackedCards(game, startingEffect, shieldFilter)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Play a Defensive Shield");
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, startingEffect, shieldFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                action.appendEffect(new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }

        if (GameConditions.canTargetToCancel(game, self, Filters.It_Can_Wait)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.It_Can_Wait, Title.It_Can_Wait);
            actions.add(action);
        }
        if (GameConditions.canTargetToCancel(game, self, Filters.Surprise_Assault)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Surprise_Assault, Title.Surprise_Assault);
            actions.add(action);
        }
        if (GameConditions.canTargetToCancel(game, self, Filters.title(Title.Yerka_Mig))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Yerka_Mig), Title.Yerka_Mig);
            actions.add(action);
        }

        Filter suspendFilter = Filters.or(Filters.Bacta_Tank, Filters.Bo_Shuda, Filters.Goo_Nee_Tay, Filters.title(Title.No_Questions_Asked));
        if (GameConditions.canSpot(game, self, suspendFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Suspend card");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target card to suspend", suspendFilter) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(new SuspendCardUntilEndOfTurnEffect(action, finalTarget));
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.It_Can_Wait, Filters.Surprise_Assault, Filters.title(Title.Yerka_Mig)))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
