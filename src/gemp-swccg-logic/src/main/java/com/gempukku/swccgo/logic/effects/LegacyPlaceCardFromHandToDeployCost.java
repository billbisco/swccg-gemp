package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.modifiers.LegacyMayIgnorePlaceCardFromHandDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.ArrayList;
import java.util.List;

/**
 * Pays the "place a card from hand on bottom of your Used Pile" deploy costs
 * ({@link com.gempukku.swccgo.logic.modifiers.LegacyPlaceCardFromHandToDeployToLocationModifier}) when cards deploy,
 * letting the player use a once-per-turn ignore
 * ({@link LegacyMayIgnorePlaceCardFromHandDeployCostModifier}) when one applies.
 */
public class LegacyPlaceCardFromHandToDeployCost {

    private LegacyPlaceCardFromHandToDeployCost() {
    }

    /**
     * Gets the cost modifiers that apply to deploying the card to the target.
     * @param game the game
     * @param cardToDeploy the card to deploy
     * @param target the deploy target (a location, or a card at a location), or null
     * @return the cost modifiers (one card from hand each)
     */
    public static List<Modifier> getCosts(SwccgGame game, PhysicalCard cardToDeploy, PhysicalCard target) {
        List<Modifier> costs = new ArrayList<Modifier>();
        if (cardToDeploy == null || target == null)
            return costs;
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.LEGACY__PLACE_CARD_FROM_HAND_TO_DEPLOY_TO_TARGET, cardToDeploy)) {
            if (modifier.isAffectedTarget(gameState, modifiersQuerying, target)) {
                costs.add(modifier);
            }
        }
        return costs;
    }

    private static LegacyMayIgnorePlaceCardFromHandDeployCostModifier findIgnore(SwccgGame game, String playerId, Modifier cost) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        for (Modifier modifier : modifiersQuerying.getModifiers(gameState, ModifierType.LEGACY__MAY_IGNORE_PLACE_CARD_FROM_HAND_TO_DEPLOY_COST)) {
            if (modifier instanceof LegacyMayIgnorePlaceCardFromHandDeployCostModifier) {
                LegacyMayIgnorePlaceCardFromHandDeployCostModifier ignore = (LegacyMayIgnorePlaceCardFromHandDeployCostModifier) modifier;
                if (ignore.ignoresCostFrom(gameState, modifiersQuerying, cost.getSource(gameState))
                        && ignore.isUnusedThisTurn(gameState, modifiersQuerying, playerId)) {
                    return ignore;
                }
            }
        }
        return null;
    }

    private static Filter otherCardsInHand(List<PhysicalCard> cardsBeingDeployed) {
        Filter filter = Filters.any;
        for (PhysicalCard card : cardsBeingDeployed) {
            filter = Filters.and(filter, Filters.not(Filters.sameCardId(card)));
        }
        return filter;
    }

    /**
     * Determines if the player can pay these costs.
     * @param game the game
     * @param playerId the player deploying
     * @param costs the cost modifiers
     * @param cardsBeingDeployed the cards being deployed (they may not be used to pay)
     * @return true or false
     */
    public static boolean canPay(SwccgGame game, String playerId, List<Modifier> costs, List<PhysicalCard> cardsBeingDeployed) {
        if (costs.isEmpty())
            return true;
        int cardsAvailable = Filters.filter(game.getGameState().getHand(playerId), game, otherCardsInHand(cardsBeingDeployed)).size();
        int ignores = 0;
        for (Modifier cost : costs) {
            if (findIgnore(game, playerId, cost) != null) {
                ignores = 1;
                break;
            }
        }
        return cardsAvailable + ignores >= costs.size();
    }

    /**
     * Appends the effects to pay these costs to the sub-action.
     * @param subAction the sub-action
     * @param playerId the player deploying
     * @param costs the cost modifiers
     * @param cardsBeingDeployed the cards being deployed (they may not be used to pay)
     */
    public static void appendCosts(final SubAction subAction, final String playerId, List<Modifier> costs, final List<PhysicalCard> cardsBeingDeployed) {
        for (final Modifier cost : costs) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            final GameState gameState = game.getGameState();
                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                            final LegacyMayIgnorePlaceCardFromHandDeployCostModifier ignore = findIgnore(game, playerId, cost);
                            final Filter otherCards = otherCardsInHand(cardsBeingDeployed);
                            final boolean canPlace = !Filters.filter(gameState.getHand(playerId), game, otherCards).isEmpty();
                            final String costSourceName = GameUtils.getCardLink(cost.getSource(gameState));
                            if (ignore == null) {
                                subAction.appendEffect(
                                        new PutCardFromHandOnBottomOfUsedPileEffect(subAction, playerId, otherCards, false));
                            }
                            else if (!canPlace) {
                                ignore.recordUse(gameState, modifiersQuerying, playerId);
                                gameState.sendMessage(playerId + " ignores the deploy cost on " + costSourceName + " using " + GameUtils.getCardLink(ignore.getSource(gameState)));
                            }
                            else {
                                subAction.appendEffect(
                                        new PlayoutDecisionEffect(subAction, playerId,
                                                new MultipleChoiceAwaitingDecision("Deploy cost on " + GameUtils.getFullName(cost.getSource(gameState)),
                                                        new String[]{"Ignore this deploy cost", "Place a card from hand on bottom of Used Pile"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            ignore.recordUse(gameState, modifiersQuerying, playerId);
                                                            gameState.sendMessage(playerId + " ignores the deploy cost on " + costSourceName + " using " + GameUtils.getCardLink(ignore.getSource(gameState)));
                                                        }
                                                        else {
                                                            subAction.appendEffect(
                                                                    new PutCardFromHandOnBottomOfUsedPileEffect(subAction, playerId, otherCards, false));
                                                        }
                                                    }
                                                }));
                            }
                        }
                    });
        }
    }
}
