package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Effect
 * Title: A Disturbance In The Force
 */
public class Card1103_026 extends AbstractNormalEffect {
    public Card1103_026() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Disturbance In The Force", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("The destruction of Alderaan caused a great disturbance in the Force '...as if millions of voices suddenly cried out in terror and were suddenly silenced.'");
        setGameText("Deploy on table. Whever you verify opponent's Reserve Deck, may place a card there with 'podracer' in game text out of play or retrieve 1 Force.");
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.justVerifiedOpponentsReserveDeck(game, effectResult, playerId)) {
            GameTextActionId placeOutOfPlayId = GameTextActionId.LEGACY__A_DISTURBANCE_IN_THE_FORCE_V__PLACE_PODRACER_OUT_OF_PLAY;
            if (GameConditions.canSearchOpponentsReserveDeck(game, playerId, self, placeOutOfPlayId)) {
                final OptionalGameTextTriggerAction placeAction = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, placeOutOfPlayId);
                placeAction.setText("Place podracer card out of play");
                placeAction.appendEffect(
                        new PlaceCardOutOfPlayFromReserveDeckEffect(placeAction, playerId, opponent,
                                Filters.or(Filters.gameTextContains("podracer"), Filters.gameTextContains("podracers")), false));
                actions.add(placeAction);
            }

            final OptionalGameTextTriggerAction retrieveAction = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, GameTextActionId.LEGACY__A_DISTURBANCE_IN_THE_FORCE_V__RETRIEVE_FORCE);
            retrieveAction.setText("Retrieve 1 Force");
            retrieveAction.appendEffect(
                    new RetrieveForceEffect(retrieveAction, playerId, 1));
            actions.add(retrieveAction);
        }
        return actions.isEmpty() ? null : actions;
    }
}
