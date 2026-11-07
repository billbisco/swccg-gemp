package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToCardMatchingFilterModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Location - Site
 * Title: Outpost
 */
public class Card1240_101 extends AbstractSite {
    private static final GameTextActionId DOWNLOAD = GameTextActionId.LEGACY__OUTPOST__DEPLOY_DEVICE_OR_EFFECT;

    public Card1240_101() {
        super(Side.LIGHT, "Outpost", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLocationLightSideGameText("May not deploy to Bespin, Coruscant, Dantooine, Kessel or Tatooine. Once per game, may deploy an Effect that deploys on a character or a device (except a comlink) here from Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("Unless opponent's matching operative at same or related site, those sites are immune to [Premium] No Escape.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Dantooine, Title.Kessel, Title.Tatooine);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(
            String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.isOncePerGame(game, self, DOWNLOAD)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, DOWNLOAD)) {
            return null;
        }
        List<TopLevelGameTextAction> actions = new ArrayList<>(2);
        Filter here = Filters.or(Filters.sameCardId(self), Filters.and(Filters.not(Filters.location), Filters.at(self)));
        Filter nonComlinkDevice = Filters.and(Filters.device, Filters.not(Filters.titleContains("comlink")));
        actions.add(download(playerId, self, gameTextSourceCardId, "Deploy a device here from Reserve Deck",
                nonComlinkDevice, here));
        Filter characters = Filters.and(Filters.character, Filters.at(self));
        if (!Filters.filterAllOnTable(game, characters).isEmpty()) {
            actions.add(download(playerId, self, gameTextSourceCardId,
                    "Deploy an Effect on a character here from Reserve Deck",
                    Filters.and(Filters.Effect, Filters.deploys_on_characters), characters));
        }
        return actions;
    }

    private static TopLevelGameTextAction download(String playerId, PhysicalCard source, int sourceId, String text,
                                                    Filter cards, Filter targets) {
        TopLevelGameTextAction action = new TopLevelGameTextAction(source, playerId, sourceId, DOWNLOAD);
        action.setText(text);
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, cards, targets, true));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(
            String playerId, SwccgGame game, PhysicalCard self) {
        Filter relatedSites = Filters.or(self, Filters.relatedSite(self));
        Filter opposingOperative = Filters.and(Filters.opponents(playerId), Filters.operativeOnMatchingPlanet,
                Filters.at(relatedSites));
        Filter premiumNoEscape = Filters.and(Filters.Effect, Filters.title(Title.No_Escape), Icon.PREMIUM);
        return List.of(new ImmuneToCardMatchingFilterModifier(self, relatedSites,
                new CantSpotCondition(self, opposingOperative), premiumNoEscape));
    }
}
