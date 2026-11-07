package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 5 Coruscant: Senate Landing Platform, printing 74. */
public class Card1250_074 extends AbstractSite {
    public Card1250_074() {
        super(Side.LIGHT, "Coruscant: Senate Landing Platform", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLocationLightSideGameText("Starships may deploy here. Once per turn, may deploy a starship site from Reserve Deck if its starship card on table; reshuffle.");
        setLocationDarkSideGameText("Unless you occupy, [Virtual Block 5] Wipe Them Out, All Of Them ignores starship and holosite sites.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        return List.of(new MayDeployToLocationModifier(self, Filters.starship, self));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__SENATE_LANDING_PLATFORM__DEPLOY_STARSHIP_SITE;
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) return null;

        Filter relatedShipOnTable = (state, modifiers, site) -> Filters.canSpot(state.getGame(), self,
                Filters.and(Filters.starship, Filters.relatedStarshipOrVehicle(site)));
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, id);
        action.setText("Deploy a starship site from Reserve Deck");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.starship_site, relatedShipOnTable), true));
        return List.of(action);
    }
}
