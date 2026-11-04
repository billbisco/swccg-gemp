package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * You Swindled Me! (V) (Virtual Block 8.2) USED: "Reveal the top four cards of your Reserve Deck and separate them into
 * two piles of any size. Opponent places one pile in your hand and the other on top of your Used Pile in any order."
 * <p>
 * The performing player reveals the top cards of their Reserve Deck and chooses the cards of the first pile (any number,
 * including none or all); the rest form the second pile. The opponent chooses which pile goes to the performing player's
 * hand, then places the other pile's cards one at a time on top of the performing player's Used Pile (the last card
 * placed ends up on top).
 */
public class LegacyRevealTopCardsSplitIntoHandAndUsedPileEffect extends AbstractSubActionEffect {
    private final String _playerId;
    private final int _count;

    /**
     * @param action the action performing this effect
     * @param playerId the player whose Reserve Deck is revealed (and who separates the piles)
     * @param count the number of cards to reveal
     */
    public LegacyRevealTopCardsSplitIntoHandAndUsedPileEffect(Action action, String playerId, int count) {
        super(action);
        _playerId = playerId;
        _count = count;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        final GameState gameState = game.getGameState();
                        List<PhysicalCard> deck = gameState.getCardPile(_playerId, Zone.RESERVE_DECK);
                        int count = Math.min(deck.size(), _count);
                        final List<PhysicalCard> topCards = new LinkedList<PhysicalCard>(deck.subList(0, count));
                        if (topCards.isEmpty()) {
                            gameState.sendMessage(_playerId + "'s Reserve Deck is empty");
                            return;
                        }
                        gameState.markCardsRevealedFromPile(topCards);
                        gameState.sendMessage(_playerId + " reveals the top " + topCards.size() + " card" + GameUtils.s(topCards) + " of Reserve Deck: " + GameUtils.getAppendedNames(topCards));

                        game.getUserFeedback().sendAwaitingDecision(_playerId,
                                new ArbitraryCardsSelectionDecision("Choose the cards for pile 1 (the rest form pile 2)", topCards, topCards, 0, topCards.size()) {
                                    @Override
                                    public void decisionMade(String result) throws DecisionResultInvalidException {
                                        final List<PhysicalCard> pile1 = new LinkedList<PhysicalCard>(getSelectedCardsByResponse(result));
                                        final List<PhysicalCard> pile2 = new LinkedList<PhysicalCard>(topCards);
                                        pile2.removeAll(pile1);
                                        gameState.sendMessage(_playerId + " separates the cards into pile 1 (" + describe(pile1) + ") and pile 2 (" + describe(pile2) + ")");
                                        opponentChoosesPileForHand(game, subAction, pile1, pile2);
                                    }
                                });
                    }
                }
        );
        return subAction;
    }

    private static String describe(List<PhysicalCard> pile) {
        return pile.isEmpty() ? "no cards" : GameUtils.getAppendedNames(pile);
    }

    private void opponentChoosesPileForHand(final SwccgGame game, final SubAction subAction, final List<PhysicalCard> pile1, final List<PhysicalCard> pile2) {
        final String opponent = game.getOpponent(_playerId);
        game.getUserFeedback().sendAwaitingDecision(opponent,
                new MultipleChoiceAwaitingDecision("Choose the pile to place in " + _playerId + "'s hand (the other goes on top of " + _playerId + "'s Used Pile)",
                        new String[]{"Pile 1: " + describe(pile1), "Pile 2: " + describe(pile2)}) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        List<PhysicalCard> toHand = (index == 0) ? pile1 : pile2;
                        List<PhysicalCard> toUsedPile = (index == 0) ? pile2 : pile1;
                        game.getGameState().sendMessage(opponent + " places pile " + (index + 1) + " in " + _playerId + "'s hand");
                        if (!toHand.isEmpty()) {
                            subAction.appendEffect(
                                    new TakeCardsIntoHandFromReserveDeckEffect(subAction, _playerId, toHand, false));
                        }
                        opponentPlacesOnUsedPile(game, subAction, new LinkedList<PhysicalCard>(toUsedPile));
                    }
                });
    }

    private void opponentPlacesOnUsedPile(final SwccgGame game, final SubAction subAction, final List<PhysicalCard> remaining) {
        if (remaining.isEmpty())
            return;
        if (remaining.size() == 1) {
            subAction.appendEffect(
                    new PutCardFromReserveDeckOnTopOfCardPileEffect(subAction, remaining.get(0), Zone.USED_PILE, false));
            return;
        }
        final String opponent = game.getOpponent(_playerId);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        game.getUserFeedback().sendAwaitingDecision(opponent,
                                new ArbitraryCardsSelectionDecision("Choose the next card to place on top of " + _playerId + "'s Used Pile (cards placed later go on top of it)", remaining, remaining, 1, 1) {
                                    @Override
                                    public void decisionMade(String result) throws DecisionResultInvalidException {
                                        PhysicalCard card = getSelectedCardsByResponse(result).get(0);
                                        remaining.remove(card);
                                        subAction.appendEffect(
                                                new PutCardFromReserveDeckOnTopOfCardPileEffect(subAction, card, Zone.USED_PILE, false));
                                        opponentPlacesOnUsedPile(game, subAction, remaining);
                                    }
                                });
                    }
                }
        );
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
