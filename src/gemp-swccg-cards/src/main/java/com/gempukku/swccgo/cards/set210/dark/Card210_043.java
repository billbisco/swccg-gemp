package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfForcePileAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Effect
 * Title: Death Star Reactor Terminal
 */
public class Card210_043 extends AbstractNormalEffect {
    public Card210_043() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Death Star Reactor Terminal", Uniqueness.UNRESTRICTED, ExpansionSet.SET_10, Rarity.V);
        setLore("The Death Star has many terminals coupled to the main reactor for power distribution throughout the immense space station.");
        setGameText("Deploy on table. X = number of your devices on table. Once during your turn, may peek at the top X cards of your Force Pile and take one into hand, or [download] (or deploy from Lost Pile) a device (except Restraining Bolt or Tractor Beam). [Immune to Alter.]");
        setVirtualSuffix(true);
        addIcons(Icon.VIRTUAL_SET_10);
        addImmuneToCardTitle(Title.Alter);
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {

        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();;


        // Once during your turn (any phase; current PC card image), may:
        // 1) peek at the top X cards of your Force Pile and take one into hand, where X = number of your devices on table, or
        // 2) [download] (or deploy from Lost Pile) a device (except Restraining Bolt or Tractor Beam).
        // All share one action id and one once-per-turn limit.
        GameTextActionId gameTextActionId = GameTextActionId.REACTOR_TERMINAL__ONCE_PER_TURN_OPTIONS;

        Filter limitedDevices = Filters.and(
                Filters.device,
                Filters.not(Filters.title(Title.Restraining_Bolt)),
                Filters.not(Filters.title("Tractor Beam")));


        // Deploy device from Lost Pile
        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, true)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy device from Lost Pile");
            action.setActionMsg("Deploy a device from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromLostPileEffect(action, limitedDevices, false, false));

            actions.add(action);
        }


        // Deploy device from Reserve Deck
        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy device from Reserve Deck");
            action.setActionMsg("Deploy a device from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, limitedDevices, false, true));

            actions.add(action);
        }


        // Peek at the top X cards of your Force Pile and take one into hand, where X = number of your devices on table.
        int numDevicesOnTable = Filters.countAllOnTable(game, Filters.and(Filters.your(playerId), Filters.device));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringYourTurn(game, playerId)
                && numDevicesOnTable > 0
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top X of Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardsOfForcePileAndChooseCardsToTakeIntoHandEffect(action, playerId, numDevicesOnTable, 1, 1));
            actions.add(action);
        }


        return actions;
    }
}
