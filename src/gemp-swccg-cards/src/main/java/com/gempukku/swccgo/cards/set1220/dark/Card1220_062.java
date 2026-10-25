package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Levitation Attack
 */
public class Card1220_062 extends AbstractUsedInterrupt {
    public Card1220_062() {
        super(Side.DARK, 4, Title.Levitation_Attack, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Vader used his mastery of the Force to levitate objects from around the room. This calculated attack was designed to break Luke's spirit as well as his body.");
        setGameText("Deploy (or retrieve) a device (for free) from Reserve Deck; reshuffle. (Immune to Sense.) OR Cancel Sabotage targeting a device. OR During a battle, place your weapon or device present in Used Pile to add 2 to your total power (3 if Vader present).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LEVITATION_ATTACK_V__DEPLOY_OR_RETRIEVE_DEVICE;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction deploy = new PlayInterruptAction(game, self, gameTextActionId);
            deploy.setText("Deploy a device for free");
            deploy.setImmuneTo(Title.Sense);
            deploy.allowResponses("Deploy a device for free from Reserve Deck",
                    new RespondablePlayCardEffect(deploy) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            deploy.appendEffect(
                                    new DeployCardFromReserveDeckEffect(deploy, Filters.device, true, true));
                        }
                    }
            );
            actions.add(deploy);
        }

        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction retrieve = new PlayInterruptAction(game, self, gameTextActionId);
            retrieve.setText("Retrieve a device");
            retrieve.setImmuneTo(Title.Sense);
            retrieve.allowResponses("Retrieve a device",
                    new RespondablePlayCardEffect(retrieve) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            retrieve.appendEffect(
                                    new RetrieveCardEffect(retrieve, playerId, Filters.device));
                        }
                    }
            );
            actions.add(retrieve);
        }

        if (GameConditions.canTargetToCancel(game, self, Filters.Sabotage)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Sabotage, Title.Sabotage);
            actions.add(action);
        }

        Filter weaponOrDevice = Filters.and(Filters.your(self), Filters.or(Filters.weapon, Filters.device), Filters.presentInBattle);
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, weaponOrDevice)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place weapon or device in Used Pile to add power");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose weapon or device", weaponOrDevice) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " in Used Pile to add to total power",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            int amount = GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.presentInBattle)) ? 3 : 2;
                                            action.appendCost(
                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new ModifyTotalPowerUntilEndOfBattleEffect(action, amount, playerId,
                                                            "Adds " + amount + " to total power"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Sabotage, Filters.device)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
