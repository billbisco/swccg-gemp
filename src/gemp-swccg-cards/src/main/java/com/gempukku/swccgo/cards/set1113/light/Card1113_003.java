package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Senate Landing Platform
 */
public class Card1113_003 extends AbstractSite {
    public Card1113_003() {
        super(Side.LIGHT, "Coruscant: Senate Landing Platform", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Starships may deploy here. Once per turn, may deploy a starship site from Reserve Deck if its starship card is on table; reshuffle.");
        setLocationDarkSideGameText("Unless you occupy, [Virtual Set 9] Wipe Them Out, All Of Them ignores starship and holosite sites.");
        setGameText("Light: Starships may deploy here. Once per turn, may deploy a starship site from Reserve Deck if its starship card is on table; reshuffle. Dark: Unless you occupy, [Virtual Set 9] Wipe Them Out, All Of Them ignores starship and holosite sites.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    private Filter virtualSet9WipeThemOut() {
        return Filters.and(Filters.title("Wipe Them Out, All Of Them"), new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_9;
            }
        });
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToLocationModifier(self, Filters.starship, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter starshipSite = Filters.and(Filters.starship_site, new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpot(game, self, Filters.relatedStarshipOrVehicle(physicalCard));
            }
        });
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, com.gempukku.swccgo.common.Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, com.gempukku.swccgo.common.GameTextActionId.LEGACY__ATAT_DEPLOYMENT_PLATFORM__DEPLOY_BIKER_SCOUT_GEAR)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Deploy a starship site from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, starshipSite, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        NotCondition unlessOccupies = new NotCondition(new OccupiesCondition(playerOnDarkSideOfLocation, Filters.or(Filters.starship_site, Filters.holosite)));
        Filter ignoredSites = Filters.or(Filters.starship_site, Filters.holosite);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, virtualSet9WipeThemOut(), unlessOccupies, ignoredSites));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(virtualSet9WipeThemOut(), Filters.attachedTo(ignoredSites)), unlessOccupies));
        return modifiers;
    }
}
