package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Title: Baktoid Armor Workshop
 */
public class Card1274_003 extends AbstractNormalEffect {
    public Card1274_003() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Baktoid Armor Workshop", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setLore("Once begun, the occupation of Naboo was swift, and devastatingly effective.");
        setGameText("Deploy on table. Your [Theed Palace] vehicles are defense value and immunity to attrition +1. May deploy Deployment Orders from Reserve Deck; reshuffle. Once per game, if Deployment Orders on table, may deploy an exterior battleground site not on table from Reserve Deck; reshuffle. Once per game, may retrieve a [Theed Palace] vehicle into hand. (Immune to Alter.)");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    private static final Filter DEPLOYMENT_ORDERS = Filters.title("Deployment Orders");

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourTheedPalaceVehicles = Filters.and(Filters.your(self), Icon.THEED_PALACE, Filters.vehicle);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, yourTheedPalaceVehicles, 1));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, yourTheedPalaceVehicles, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BAKTOID_ARMOR_WORKSHOP__DEPLOY_DEPLOYMENT_ORDERS;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, "Deployment Orders")) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Deployment Orders from Reserve Deck");
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, DEPLOYMENT_ORDERS, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__BAKTOID_ARMOR_WORKSHOP__DEPLOY_EXTERIOR_BATTLEGROUND_SITE;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, DEPLOYMENT_ORDERS)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy exterior battleground site from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.site, Filters.exterior_battleground_site), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__BAKTOID_ARMOR_WORKSHOP__RETRIEVE_THEED_PALACE_VEHICLE;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a [Theed Palace] vehicle into hand");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, Filters.and(Icon.THEED_PALACE, Filters.vehicle)));
            actions.add(action);
        }
        return actions;
    }
}
