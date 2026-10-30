package com.gempukku.swccgo.cards.set1230.dark;

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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Freeze!
 */
public class Card1230_044 extends AbstractUsedInterrupt {
    public Card1230_044() {
        super(Side.DARK, 6, Title.Freeze, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Despite Wicket's warnings, an Imperial scout got the drop on Leia.");
        setGameText("Deploy Blaster Rack from Reserve Deck; reshuffle. OR During battle, attrition against opponent is +1 for each non-lightsaber weapon you have present. OR Your scout is power +3 for remainder of turn.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId blasterRackId = GameTextActionId.LEGACY__FREEZE_V__DEPLOY_BLASTER_RACK;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, blasterRackId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, blasterRackId);
            action.setText("Deploy Blaster Rack from Reserve Deck");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Blaster_Rack, true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.isDuringBattle(game)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.weapon, Filters.not(Filters.lightsaber), Filters.presentInBattle))) {
            int weapons = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.weapon, Filters.not(Filters.lightsaber), Filters.presentInBattle));
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add " + weapons + " to attrition against opponent");
            final int amount = weapons;
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new AttritionModifier(self, Filters.battleLocation, amount, game.getOpponent(playerId)),
                                            "Attrition against opponent is +" + amount));
                        }
                    }
            );
            actions.add(action);
        }

        Filter yourScout = Filters.and(Filters.your(self), Filters.scout);
        if (GameConditions.canTarget(game, self, yourScout)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make a scout power +3");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose scout", yourScout) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new PowerModifier(self, finalTarget, 3),
                                                            "Make scout power +3"));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}
