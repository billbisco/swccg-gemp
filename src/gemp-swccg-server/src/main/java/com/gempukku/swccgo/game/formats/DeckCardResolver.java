package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.List;
import java.util.function.UnaryOperator;

/** A non-persistent card-id transform shared by format-time deck selections. */
final class DeckCardResolver {
    private DeckCardResolver() {
    }

    static SwccgDeck apply(SwccgDeck original, UnaryOperator<String> resolve) {
        if (original == null)
            return null;
        List<String> cards = original.getCards().stream().map(resolve).toList();
        List<String> outside = original.getCardsOutsideDeck().stream().map(resolve).toList();
        if (cards.equals(original.getCards()) && outside.equals(original.getCardsOutsideDeck()))
            return original;
        SwccgDeck result = new SwccgDeck(original.getDeckName());
        result.setFormatIndex(original.getFormatIndex());
        result.setSourceCollection(original.getSourceCollection());
        cards.forEach(result::addCard);
        outside.forEach(result::addCardOutsideDeck);
        return result;
    }
}
