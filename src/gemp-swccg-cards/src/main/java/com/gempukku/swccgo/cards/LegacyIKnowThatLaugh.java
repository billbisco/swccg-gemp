package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared pieces of the Virtual Block 8.2 Jabba's Palace cards built around I Know That Laugh (1282_21) and the
 * Agents In The Court Rep: Loje Nella (V), Rennek (V), Vilmarh, You Will Take Me To Jabba Now (V) and
 * Under Jabba's Protection.
 */
public final class LegacyIKnowThatLaugh {
    public static final String TITLE = "I Know That Laugh";

    private LegacyIKnowThatLaugh() {
    }

    /**
     * Filter for I Know That Laugh.
     */
    public static Filter iKnowThatLaugh() {
        return Filters.title(TITLE);
    }

    /**
     * Filter for the specified player's I Know That Laugh.
     */
    public static Filter yourIKnowThatLaugh(String playerId) {
        return Filters.and(Filters.your(playerId), iKnowThatLaugh());
    }

    /**
     * Filter for cards stacked on the specified player's I Know That Laugh.
     */
    public static Filter stackedOnYourIKnowThatLaugh(PhysicalCard source, String playerId) {
        return Filters.stackedOn(source, yourIKnowThatLaugh(playerId));
    }

    /**
     * Rennek (V): "While stacked on I Know That Laugh, stack face-up." Every other card goes on face down.
     */
    public static boolean stacksFaceUp(PhysicalCard card) {
        return card.getBlueprint().getTitle().equals(Title.Rennek) && card.getBlueprint().hasVirtualSuffix();
    }

    /**
     * Counts the cards stacked on the player's I Know That Laugh and Objective.
     */
    public static int countStackedOnIKnowThatLaughAndObjective(SwccgGame game, String playerId) {
        int count = 0;
        for (PhysicalCard card : Filters.filterActive(game, null, Filters.or(yourIKnowThatLaugh(playerId), Filters.and(Filters.your(playerId), Filters.Objective)))) {
            count += game.getGameState().getStackedCards(card).size();
        }
        return count;
    }

    /**
     * Condition fulfilled while at least n cards are stacked on the player's I Know That Laugh and/or Objective.
     */
    public static Condition stackedOnIKnowThatLaughAndOrObjectiveCondition(final String playerId, final int n) {
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return countStackedOnIKnowThatLaughAndObjective(gameState.getGame(), playerId) >= n;
            }
        };
    }

    /**
     * Filter for a copy of the player's Rep stacked on the player's Objective (Agents In The Court).
     */
    public static Filter repStackedOnYourObjective(SwccgGame game, PhysicalCard source, String playerId) {
        PhysicalCard rep = game.getGameState().getRep(playerId);
        if (rep == null) {
            return Filters.none;
        }
        return Filters.and(Filters.stackedOn(source, Filters.and(Filters.your(playerId), Filters.Objective)), Filters.sameTitle(rep));
    }

    /**
     * "When you play this Interrupt, may place a Rep stacked on your Objective in Used Pile." Appends a choice of
     * zero or one copy of the player's Rep stacked on the player's Objective; the chosen copy goes to Used Pile.
     */
    public static void appendMayPlaceRepInUsedPile(final Action action, final SwccgGame game, PhysicalCard source, final String playerId) {
        List<PhysicalCard> reps = new ArrayList<PhysicalCard>();
        Filter repFilter = repStackedOnYourObjective(game, source, playerId);
        for (PhysicalCard objective : Filters.filterActive(game, source, Filters.and(Filters.your(playerId), Filters.Objective))) {
            reps.addAll(Filters.filter(game.getGameState().getStackedCards(objective), game, repFilter));
        }
        if (reps.isEmpty()) {
            return;
        }
        action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                        new ArbitraryCardsSelectionDecision("Choose a Rep stacked on your Objective to place in Used Pile (or none)", reps, 0, 1) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                List<PhysicalCard> selected = getSelectedCardsByResponse(result);
                                if (!selected.isEmpty()) {
                                    action.appendEffect(
                                            new PutStackedCardInUsedPileEffect(action, playerId, selected.get(0), false));
                                }
                            }
                        }));
    }
}
