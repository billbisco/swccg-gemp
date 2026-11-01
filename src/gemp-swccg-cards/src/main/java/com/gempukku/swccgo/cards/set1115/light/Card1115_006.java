package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceStackedCardOutOfPlayEffect;
import com.gempukku.swccgo.logic.effects.choose.ResetAttritionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Beru Stew
 */
public class Card1115_006 extends AbstractUsedOrLostInterrupt {
    public Card1115_006() {
        super(Side.LIGHT, 4, Title.Beru_Stew, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Moisture farmers grow enough food to sustain Tatooine's population. Beru Lars has devised many dishes using herbs and roots naturally found in Tatooine's desert.");
        setGameText("USED: Activate 1 Force (2 if a Lars on table). OR During battle at a farm, add the destiny number of your device on this farm to your total power. LOST: During battle at a farm, after battle destinies are drawn, place a 'crop' out of play to make attrition against you = 0.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter lars = Filters.or(Filters.Beru, Filters.Owen);

        if (GameConditions.canActivateForce(game, playerId)) {
            final int amount = GameConditions.canSpot(game, self, lars) ? 2 : 1;
            if (game.getGameState().getReserveDeckSize(playerId) > 0) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Activate " + amount + " Force");
                action.allowResponses("Activate " + amount + " Force",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                int toActivate = Math.min(amount, game.getGameState().getReserveDeckSize(playerId));
                                if (toActivate > 0) {
                                    action.appendEffect(
                                            new ActivateForceEffect(action, playerId, toActivate));
                                }
                            }
                        }
                );
                actions.add(action);
            }
        }

        Filter deviceOnFarm = Filters.and(Filters.your(self), Filters.device, Filters.at(Filters.and(Filters.farm, Filters.battleLocation)), Filters.canBeTargetedBy(self));
        if (GameConditions.isDuringBattleAt(game, Filters.farm)
                && GameConditions.canTarget(game, self, deviceOnFarm)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add device destiny to total power");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose your device on this farm", deviceOnFarm) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Add destiny of " + GameUtils.getCardLink(targetedCard) + " to total power",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard device = action.getPrimaryTargetCard(targetGroupId);
                                            float destiny = game.getModifiersQuerying().getDestiny(game.getGameState(), device);
                                            action.appendEffect(
                                                    new ModifyTotalPowerUntilEndOfBattleEffect(action, destiny, playerId,
                                                            "Adds " + GameUtils.getCardLink(device) + "'s destiny to total power"));
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
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter hydroponics = Filters.and(Filters.your(self), Filters.title("Lars' Hydroponics Station"), Filters.hasStacked(Filters.any));
        if (TriggerConditions.isBattleDestinyDrawingJustCompletedForBothPlayers(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.farm)
                && GameConditions.canSpot(game, self, hydroponics)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Place 'crop' out of play");
            action.appendCost(
                    new PlaceStackedCardOutOfPlayEffect(action, playerId, hydroponics, Filters.any));
            action.allowResponses("Place a 'crop' out of play to make attrition against you = 0",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new ResetAttritionEffect(action, playerId, 0));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
