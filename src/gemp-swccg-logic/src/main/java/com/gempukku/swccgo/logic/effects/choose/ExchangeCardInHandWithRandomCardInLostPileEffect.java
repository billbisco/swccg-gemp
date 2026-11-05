package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ExchangedCardsInCardPileResult;

import java.util.Collection;
import java.util.List;

/**
 * An effect to exchange a card from hand with a random card in the player's Lost Pile.
 */
public class ExchangeCardInHandWithRandomCardInLostPileEffect extends AbstractSubActionEffect {
    private final String _playerId;
    private PhysicalCard _cardFromHand;

    /**
     * Creates an effect to exchange a card from hand with a random card in Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public ExchangeCardInHandWithRandomCardInLostPileEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new ChooseCardsFromHandEffect(subAction, _playerId, 1, 1, Filters.any) {
                    @Override
                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> cards) {
                        if (!cards.isEmpty()) {
                            _cardFromHand = cards.iterator().next();
                        }
                    }
                });
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        List<PhysicalCard> lostPile = gameState.getCardPile(_playerId, Zone.LOST_PILE);
                        if (_cardFromHand == null || lostPile.isEmpty()) {
                            return;
                        }
                        PhysicalCard randomCard = GameUtils.getRandomCards(game, lostPile, 1).get(0);
                        int indexOf = lostPile.indexOf(randomCard);
                        gameState.sendMessage(_playerId + " exchanges a card from hand with " + GameUtils.getCardLink(randomCard) + " (random card) from Lost Pile");
                        gameState.removeCardFromZone(_cardFromHand);
                        gameState.removeCardFromZone(randomCard);
                        gameState.addCardToSpecificPositionInZone(_cardFromHand, Zone.LOST_PILE, _playerId, indexOf);
                        gameState.addCardToZone(randomCard, Zone.HAND, _playerId);
                        game.getActionsEnvironment().emitEffectResult(
                                new ExchangedCardsInCardPileResult(subAction));
                    }
                });
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
