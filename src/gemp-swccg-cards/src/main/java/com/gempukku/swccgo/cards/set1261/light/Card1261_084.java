package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import java.util.ArrayList;
import java.util.List;

/** Republic Logistics, a new title printed over Secure Route. */
public class Card1261_084 extends AbstractNormalEffect {
    private static final String JEDIS_PLANS = "A Jedi's Plans";

    public Card1261_084() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Republic Logistics", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Maintaining unimpeded control of vital transportation channels can strengthen and secure surrounding areas.");
        setGameText("Deploy on table. If A Jedi's Plans not on table, may deploy it from Reserve Deck; reshuffle. Once per turn, you may place a non-unique card with ability from your Lost Pile out of play to retrieve any one unique (•) character of ability < 5, starship, or vehicle (except one with a [Permanent Weapon] or [Maintenance]). (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        GameTextActionId searchId = GameTextActionId.LEGACY__REPUBLIC_LOGISTICS__DEPLOY_JEDIS_PLANS;
        Filter plans = Filters.title(JEDIS_PLANS);
        if (!GameConditions.canSpot(game, self, plans)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, searchId, JEDIS_PLANS)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, searchId);
            action.setText("Deploy A Jedi's Plans from Reserve Deck");
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, plans, true));
            actions.add(action);
        }
        Filter cost = Filters.and(Filters.non_unique, Filters.hasAbility);
        GameTextActionId retrieveId = GameTextActionId.LEGACY__REPUBLIC_LOGISTICS__RETRIEVE_UNIQUE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, retrieveId)
                && GameConditions.canSearchLostPile(game, playerId, self, retrieveId)
                && Filters.canSpot(game.getGameState().getLostPile(playerId), game, cost)) {
            Filter target = Filters.and(Filters.unique, Filters.not(Filters.or(Icon.PERMANENT_WEAPON, Icon.MAINTENANCE)),
                    Filters.or(Filters.and(Filters.character, Filters.abilityLessThan(5)), Filters.starship, Filters.vehicle));
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, retrieveId);
            action.setText("Place a lost card out of play to retrieve a unique card");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, cost, false));
            action.appendEffect(new RetrieveCardEffect(action, playerId, target));
            actions.add(action);
        }
        return actions;
    }
}
