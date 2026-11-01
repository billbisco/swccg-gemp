package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Harvest
 */
public class Card1115_011 extends AbstractUsedOrStartingInterrupt {
    public Card1115_011() {
        super(Side.LIGHT, 5, Title.Harvest, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Harvest is when I need you the most. It's only one season more. This year we'll make enough on the harvest that I'll be able to hire some more hands.'");
        setGameText("USED: Deploy a device from under your Starting Effect. STARTING: If your starting location was a Tatooine location, deploy from Reserve Deck a farm, two devices, and two Effects that are always [Immune to Alter]. Place this Interrupt in Reserve Deck.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HARVEST_V__RETRIEVE_OR_TAKE_DEVICE;

        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null && GameConditions.hasStackedCards(game, startingEffect, Filters.device)) {
            final PhysicalCard stackedOn = startingEffect;
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Deploy a device from under your Starting Effect");
            action.appendEffect(new com.gempukku.swccgo.logic.effects.choose.DeployStackedCardsEffect(action, stackedOn, Filters.device, 1, 1, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation == null || !Filters.Tatooine_location.accepts(game, startingLocation)) {
            return null;
        }

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HARVEST_V__STARTING_DEPLOY;
        final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.STARTING);
        action.setText("Deploy farm, devices, and Effects from Reserve Deck");
        action.allowResponses("Deploy a farm, two devices, and two Effects that are always immune to Alter from Reserve Deck",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.farm, true, false));
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.device, true, false));
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.device, true, false));
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action,
                                        Filters.and(Filters.Effect, Filters.always_immune_to_Alter), true, false));
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action,
                                        Filters.and(Filters.Effect, Filters.always_immune_to_Alter), true, false));
                        action.appendEffect(
                                new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}
