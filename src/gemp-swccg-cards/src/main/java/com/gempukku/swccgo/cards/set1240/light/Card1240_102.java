package com.gempukku.swccgo.cards.set1240.light;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToCardMatchingFilterModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.LandsFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsLandedToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TakesOffFreeFromLocationModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Location
 * Subtype: Site
 * Title: Rebel Cell - Hidden Landing Site
 */
public class Card1240_102 extends AbstractSite {
    public Card1240_102() {
        super(Side.LIGHT, "Rebel Cell - Hidden Landing Site", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLocationLightSideGameText("Your starfighters may deploy here (for -1 Force), and take off and land here for free. Incom Corporation may deploy here (once per game, may use 2 Force to deploy it here from Reserve Deck; reshuffle).");
        setLocationDarkSideGameText("May not deploy to Bespin, Coruscant, Dagobah, Dantooine, Endor, Hoth, or Subjugated Planet. Rebel Cell sites are immune to Revolution and [Premium] No Escape. Limit one copy on table.");
        setGameText("Light: Your starfighters may deploy here (for -1 Force), and take off and land here for free. Incom Corporation may deploy here (once per game, may use 2 Force to deploy it here from Reserve Deck; reshuffle). Dark: May not deploy to Bespin, Coruscant, Dagobah, Dantooine, Endor, Hoth, or Subjugated Planet. Rebel Cell sites are immune to Revolution and [Premium] No Escape. Limit one copy on table.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.TATOOINE);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Dagobah, Title.Dantooine, Title.Endor, Title.Hoth);
    }

    @Override
    public boolean mayNotBePartOfSystem(SwccgGame game, String system) {
        if (super.mayNotBePartOfSystem(game, system)) {
            return true;
        }
        String subjugatedPlanet = game.getGameState().getSubjugatedPlanet();
        return subjugatedPlanet != null && subjugatedPlanet.equals(system);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsLandedToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter), self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter), -1, self, true));
        modifiers.add(new TakesOffFreeFromLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter), self));
        modifiers.add(new LandsFreeToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter), self));
        modifiers.add(new MayDeployToTargetModifier(self, Filters.title("Incom Corporation"), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnDarkSideOfLocation);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, Filters.sameTitle(self), playerOnDarkSideOfLocation));
        modifiers.add(new MayNotDeployModifier(self, Filters.sameTitle(self), opponent));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.titleContains("Rebel Cell"), Title.Revolution));
        modifiers.add(new ImmuneToCardMatchingFilterModifier(self, Filters.titleContains("Rebel Cell"),
                Filters.and(Filters.title(Title.No_Escape), Icon.PREMIUM)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HIDDEN_LANDING_SITE_V__DEPLOY_INCOM;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerOnLightSideOfLocation, 2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Incom Corporation from Reserve Deck");
            action.setActionMsg("Deploy Incom Corporation here from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerOnLightSideOfLocation, 2));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.title("Incom Corporation"), Filters.sameLocation(self), true,
                            DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
