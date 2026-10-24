package com.gempukku.swccgo.cards.set1210.light;

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
public class Card1210_154 extends AbstractUsedOrStartingInterrupt {
    public Card1210_154() {
        super(Side.LIGHT, 5, Title.Harvest, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Harvest is when I need you the most. It's only one season more. This year we'll make enough on the harvest that I'll be able to hire some more hands.'");
        setGameText("USED: Retrieve a device, or take a device into hand from Reserve Deck; reshuffle. (Immune to Sense.) STARTING: If your starting location was a Tatooine location, deploy from Reserve Deck a farm, two devices, and two Effects that are always [Immune to Alter]. Place this Interrupt in Reserve Deck.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HARVEST_V__RETRIEVE_OR_TAKE_DEVICE;

        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Retrieve a device");
            action.setImmuneTo(Title.Sense);
            action.allowResponses("Retrieve a device",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, Filters.device));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take a device into hand from Reserve Deck");
            action.setImmuneTo(Title.Sense);
            action.allowResponses("Take a device into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.device, true));
                        }
                    }
            );
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
