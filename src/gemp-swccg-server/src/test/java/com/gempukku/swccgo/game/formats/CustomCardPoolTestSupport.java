package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/** Deck helpers for the custom card pool tests (swccg-guild/work#222), built from the guest decks so no card id is hard-coded. */
public final class CustomCardPoolTestSupport {
    private static final SwccgCardBlueprintLibrary CARDS = VirtualTableScenario._cardLibrary;

    private CustomCardPoolTestSupport() {
    }

    public static int setOf(String id) {
        return Integer.parseInt(CARDS.stripBlueprintModifiers(id).split("_")[0]);
    }

    /** The sets a deck's cards come from. */
    public static List<Integer> setsOf(SwccgDeck... decks) {
        TreeSet<Integer> sets = new TreeSet<>();
        for (SwccgDeck deck : decks) {
            for (String id : deck.getCards())
                sets.add(setOf(id));
            for (String id : deck.getCardsOutsideDeck())
                sets.add(setOf(id));
        }
        return new ArrayList<>(sets);
    }

    public static List<String> cardsFromSet(SwccgDeck deck, int set) {
        TreeSet<String> ids = new TreeSet<>();
        for (String id : deck.getCards())
            if (setOf(id) == set)
                ids.add(CARDS.stripBlueprintModifiers(id));
        for (String id : deck.getCardsOutsideDeck())
            if (setOf(id) == set)
                ids.add(CARDS.stripBlueprintModifiers(id));
        return new ArrayList<>(ids);
    }

    /** A Light Side card from {@code set} the deck does not use, legal in the format: something to ban or add. */
    public static String unusedLightCard(SwccgDeck deck, int set, SwccgFormat format) {
        for (int i = 1; i < 400; i++) {
            String id = set + "_" + i;
            SwccgCardBlueprint card = CARDS.getSwccgoCardBlueprint(id);
            if (card == null || card.getSide() != Side.LIGHT || card.getCardCategory() == CardCategory.GAME_AID
                    || card.getCardCategory() == CardCategory.OBJECTIVE || card.getCardCategory() == CardCategory.DEFENSIVE_SHIELD
                    || deck.getCards().contains(id) || deck.getCardsOutsideDeck().contains(id))
                continue;
            try {
                format.validateCard(id, false);
                return id;
            } catch (DeckInvalidException exp) {
                // not in the format
            }
        }
        throw new AssertionError("no unused Light Side card in set " + set);
    }

    /** The deck with its last in-deck card replaced by {@code card}. */
    public static SwccgDeck withCard(SwccgDeck deck, String card) {
        SwccgDeck copy = new SwccgDeck(deck.getDeckName() + "+" + card);
        List<String> cards = new ArrayList<>(deck.getCards());
        cards.set(cards.size() - 1, card);
        for (String id : cards)
            copy.addCard(id);
        for (String id : deck.getCardsOutsideDeck())
            copy.addCardOutsideDeck(id);
        return copy;
    }
}
