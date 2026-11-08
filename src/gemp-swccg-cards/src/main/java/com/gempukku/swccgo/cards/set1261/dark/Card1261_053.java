package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.List;

/** Legacy Block 6.1 Remote Depot. */
public class Card1261_053 extends AbstractSite {
    private static final GameTextActionId SEARCH = GameTextActionId.LEGACY__REMOTE_DEPOT__DEPLOY_EFFECT;
    // Zone-only plays ignore the target constraint in the generic deploy helper.
    private static final Filter ATTACHING_EFFECT = Filters.and(Filters.Effect, new Filter() {
        @Override
        public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
            return card.getBlueprint().getSinglePlayCardZoneOption() == PlayCardZoneOption.ATTACHED;
        }
    });

    public Card1261_053() {
        super(Side.DARK, "Remote Depot", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLocationDarkSideGameText("May not deploy to Bespin, Coruscant, Kessel, Ralltiir, or Tatooine. Once per game, may deploy an Effect that deploys on a character or a device here from Reserve Deck; reshuffle.");
        setLocationLightSideGameText("Unless opponent's Sleen or matching operative at same or related site, those sites are immune to Ounee Ta.");
        setGameText("Dark: May not deploy to Bespin, Coruscant, Kessel, Ralltiir, or Tatooine. Once per game, may deploy an Effect that deploys on a character or a device here from Reserve Deck; reshuffle. Light: Unless opponent's Sleen or matching operative at same site or related site, those sites are immune to Ounee Ta.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Kessel, Title.Ralltiir, Title.Tatooine);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerId, SwccgGame game,
                                                                            PhysicalCard self, int sourceId) {
        Filter target = Filters.and(Filters.or(Filters.character, Filters.device), Filters.at(self));
        if (!GameConditions.isOncePerGame(game, self, SEARCH)
                || !GameConditions.canSpot(game, self, target)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, SEARCH, false))
            return null;

        var action = new TopLevelGameTextAction(self, playerId, sourceId, SEARCH);
        action.setText("Deploy Effect on a character or device here");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, ATTACHING_EFFECT, target, true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSide, SwccgGame game,
                                                                     PhysicalCard self) {
        Filter sites = Filters.or(self, Filters.relatedSite(self));
        Filter opposingException = Filters.and(Filters.opponents(playerOnLightSide),
                Filters.at(sites), Filters.or(Filters.title("Sleen"), Filters.operativeOnMatchingPlanet));
        return List.of(new ImmuneToTitleModifier(self, sites, new CantSpotCondition(self, opposingException), Title.Ounee_Ta));
    }
}
