package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that allows the player to peek at and reorder the top cards in the specified Used Pile.
 */
public class PeekAtAndReorderTopCardsOfUsedPileEffect extends PeekAtAndReorderTopCardsOfCardPileEffect {

    /**
     * Create an effect that allows the performing player to peek at and reorder the top cards in the specified player's Used Pile.
     * @param action the action performing this effect
     * @param cardPileOwner the card pile owner
     * @param numCards the number of cards to peek at and reorder
     */
    public PeekAtAndReorderTopCardsOfUsedPileEffect(Action action, String cardPileOwner, int numCards) {
        super(action, action.getPerformingPlayer(), Zone.USED_PILE, cardPileOwner, numCards);
    }
}
