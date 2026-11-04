package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.DeviceThatDeploysOnStarshipFilter;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Imperial Vanguard Camp
 */
public class Card1283_013 extends AbstractSite {
    /** Jir: Commander Daine Jir (Virtual Block 6.1, not yet in GEMP), matched by title. */
    private static final Filter JIR = Filters.or(Filters.title("Commander Daine Jir"), Filters.title("Daine Jir"), Filters.title("Jir"));

    public Card1283_013() {
        super(Side.DARK, "Tatooine: Imperial Vanguard Camp", Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setLocationDarkSideGameText("May deploy Jir here from Reserve Deck; reshuffle. Once per game, may use 1 Force to take a device that deploys on a starship into hand from Reserve Deck; reshuffle.");
        setLocationLightSideGameText("If a player controls this site, they may raise their converted Tatooine system to the top.");
        setGameText("Dark: May deploy Jir here from Reserve Deck; reshuffle. Once per game, may use 1 Force to take a device that deploys on a starship into hand from Reserve Deck; reshuffle. Light: If a player controls this site, they may raise their converted Tatooine system to the top.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IMPERIAL_VANGUARD_CAMP__DEPLOY_JIR;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Jir from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, JIR, Filters.here(self), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__IMPERIAL_VANGUARD_CAMP__TAKE_STARSHIP_DEVICE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerOnDarkSideOfLocation, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Take starship device into hand from Reserve Deck");
            action.setActionMsg("Take a device that deploys on a starship into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerOnDarkSideOfLocation, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerOnDarkSideOfLocation, DeviceThatDeploysOnStarshipFilter.INSTANCE, true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return raiseConvertedTatooine(playerOnLightSideOfLocation, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideOpponentsTopLevelActions(String opponentOfPlayerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // AbstractLocation passes the opponent of the player on the Light side here
        return raiseConvertedTatooine(opponentOfPlayerOnLightSideOfLocation, game, self, gameTextSourceCardId);
    }

    // "If a player controls this site, they may raise their converted Tatooine system to the top."
    private List<TopLevelGameTextAction> raiseConvertedTatooine(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter converted = Filters.and(Filters.system, Filters.title(Title.Tatooine), Filters.canBeConvertedByRaisingYourLocationToTop(playerId));
        if (GameConditions.controls(game, playerId, self)
                && GameConditions.canTarget(game, self, converted)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Raise converted Tatooine system");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose converted Tatooine system", converted) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(new ConvertLocationByRaisingToTopEffect(action, targetedCard, true));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
