package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.OncePerTurnEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Tatooine: Queen's Landing Site, Virtual Block 6.1. */
public class Card1261_114 extends AbstractSite {
    public Card1261_114() {
        super(Side.LIGHT, "Tatooine: Queen's Landing Site", Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLocationLightSideGameText("Once per turn, may deploy Ric Olie and/or a 327 Nubian here (for -1 Force each) from Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("Force drain +1 here. Opponent may not Force drain here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PREMIUM, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        return Arrays.asList(new ForceDrainModifier(self, 1, playerId),
                new MayNotForceDrainAtLocationModifier(self, game.getOpponent(playerId)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId actionId = GameTextActionId.LEGACY__QUEENS_LANDING_SITE__DOWNLOAD;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, actionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, actionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, actionId);
            action.setText("Deploy Ric Olie and/or 327 Nubian");
            action.appendUsage(new OncePerTurnEffect(action));
            // Ship first permits Ric to deploy aboard it. Each category is optional and limited to one.
            action.appendEffect(new DeployCardsToLocationFromReserveDeckEffect(action,
                    Filters.and(Filters.starship, ModelType.J_TYPE_327_NUBIAN), 0, 1, self, -1, false));
            action.appendEffect(new DeployCardsToLocationFromReserveDeckEffect(action, Filters.Ric, 0, 1, self, -1, false));
            action.appendEffect(new ShufflePileEffect(action, self, playerId, playerId, Zone.RESERVE_DECK, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
