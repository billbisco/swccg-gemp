package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.cards.AbstractNonuniqueVehicleSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EnterExitCostForCharactersModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: AT-AT Deployment Platform
 */
public class Card1113_014 extends AbstractNonuniqueVehicleSite {
    public Card1113_014() {
        super(Side.DARK, Title.AT_AT_Deployment_Platform, Filters.AT_AT, Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setLocationDarkSideGameText("May deploy Biker Scout Gear here from Reserve Deck; reshuffle. While Biker Scout Gear is here, 'deploy for free' in its game text may be treated as 'deploy from Reserve Deck for free.'");
        setLocationLightSideGameText("Your characters may enter/exit here for 2 Force each. Unless you occupy, AT-AT's immunity to attrition is +1 (or immune to attrition if non-unique).");
        setGameText("Dark: May deploy Biker Scout Gear here from Reserve Deck; reshuffle. While Biker Scout Gear is here, 'deploy for free' in its game text may be treated as 'deploy from Reserve Deck for free.' Light: Your characters may enter/exit here for 2 Force each. Unless you occupy, AT-AT's immunity to attrition is +1 (or immune to attrition if non-unique).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.SCOMP_LINK);
    }

    private Filter bikerScoutGearCardsWithoutAbility() {
        return Filters.and(
                Filters.or(Filters.scout_blaster, Filters.Comlink, Filters.DH17_blaster, Filters.Blaster_Rack),
                Filters.not(Filters.hasAbilityOrHasPermanentPilotWithAbility));
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter relatedAtAt = Filters.relatedStarshipOrVehicle(self);
        NotCondition unlessLightOccupies = new NotCondition(new OccupiesCondition(playerOnLightSideOfLocation, self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EnterExitCostForCharactersModifier(self, 2, playerOnLightSideOfLocation));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(relatedAtAt, Filters.unique), unlessLightOccupies, 1));
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.and(relatedAtAt, Filters.non_unique), unlessLightOccupies));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId deployGearId = GameTextActionId.LEGACY__ATAT_DEPLOYMENT_PLATFORM__DEPLOY_BIKER_SCOUT_GEAR;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, deployGearId, Title.Biker_Scout_Gear)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, deployGearId);
            action.setText("Deploy Biker Scout Gear from Reserve Deck");
            action.setActionMsg("Deploy Biker Scout Gear here from Reserve Deck");
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Biker_Scout_Gear), true));
            actions.add(action);
        }

        if (GameConditions.canSpot(game, self, Filters.here(self))
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, GameTextActionId.LEGACY__ATAT_DEPLOYMENT_PLATFORM__DEPLOY_CARD_IN_GAME_TEXT)
                && Filters.canSpot(game, self, Filters.and(Filters.title(Title.Biker_Scout_Gear), Filters.here(self)))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Deploy a Biker Scout Gear card from Reserve Deck for free");
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, bikerScoutGearCardsWithoutAbility(), true, true));
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }
}
