package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Title: Seeking An Audience (V)
 */
public class Card1106_020 extends AbstractNormalEffect {
    public Card1106_020() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Seeking_An_Audience, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("'With your wisdom, I'm sure that we can work out an arrangement which will be mutually beneficial and enable us to avoid any unpleasant confrontation.'");
        setGameText("Deploy on table. During your deploy phase, while You Can Either Profit By This... or Or Be Destroyed on table, may deploy one C-3PO, Chewie, Lando, Leia, or R2-D2 from Reserve Deck; reshuffle. Once per game, may place this Effect out of play to take a character from Lost Pile into hand. (Immune to Alter.)");
        addIcons(Icon.PREMIUM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OVS__SEEKING_AN_AUDIENCE__DEPLOY_CHARACTER;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, Filters.or(Filters.You_Can_Either_Profit_By_This, Filters.Or_Be_Destroyed))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, new HashSet<Persona>(Arrays.asList(Persona.C3PO, Persona.CHEWIE, Persona.LANDO, Persona.LEIA, Persona.R2D2)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy C-3PO, Chewie, Lando, Leia, or R2-D2 from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.C3PO, Filters.Chewie, Filters.Lando, Filters.Leia, Filters.R2D2), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OVS__SEEKING_AN_AUDIENCE__TAKE_FROM_LOST;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place out of play to take card into hand");
            action.setActionMsg("Take a character into hand from Lost Pile");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            action.appendEffect(
                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.character, true));
            actions.add(action);
        }

        return actions;
    }
}
