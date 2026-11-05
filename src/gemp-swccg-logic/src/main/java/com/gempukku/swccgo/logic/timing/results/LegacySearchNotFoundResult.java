package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * This effect result is triggered when a player just searched a card pile to take a card into hand or to deploy a
 * card, and the card was not found (nothing was taken into hand or deployed).
 *
 * The result carries the search that failed, so a responding card can repeat the same search in another card pile
 * ("...the card was not found, may ... search your Force Pile for that card").
 */
public class LegacySearchNotFoundResult extends EffectResult {

    /**
     * What the failed search was trying to do with the card it was looking for.
     */
    public enum SearchKind {
        TAKE_INTO_HAND("take a card into hand"),
        DEPLOY("deploy a card");

        private final String _humanReadable;

        SearchKind(String humanReadable) {
            _humanReadable = humanReadable;
        }

        public String getHumanReadable() {
            return _humanReadable;
        }
    }

    /**
     * Repeats the failed search, with the same card criteria, in another card pile.
     */
    public abstract static class Retry {
        /**
         * Creates an effect that searches the specified card pile for the card the failed search was looking for, and
         * takes it into hand or deploys it the same way the failed search would have.
         * @param action the action performing the new search
         * @param zone the card pile to search
         * @param reshuffle true if the card pile is reshuffled afterwards, otherwise false
         * @return the effect
         */
        public abstract StandardEffect createSearch(Action action, Zone zone, boolean reshuffle);
    }

    private final String _cardPileOwner;
    private final Zone _cardPile;
    private final SearchKind _searchKind;
    private final PhysicalCard _searchSource;
    private final Retry _retry;

    /**
     * Creates an effect result that is triggered when a search to take a card into hand or deploy a card did not find the card.
     * @param playerId the player who searched
     * @param cardPileOwner the owner of the card pile searched
     * @param cardPile the card pile searched
     * @param searchKind what the search was trying to do with the card
     * @param searchSource the card whose action made the search, or null
     * @param retry repeats the same search in another card pile
     */
    public LegacySearchNotFoundResult(String playerId, String cardPileOwner, Zone cardPile, SearchKind searchKind, PhysicalCard searchSource, Retry retry) {
        super(Type.LEGACY__SEARCH_NOT_FOUND, playerId);
        _cardPileOwner = cardPileOwner;
        _cardPile = cardPile;
        _searchKind = searchKind;
        _searchSource = searchSource;
        _retry = retry;
    }

    /**
     * Determines if the effect result is the specified player just failing to find a card while searching their own
     * card pile to take a card into hand or deploy a card.
     * @param effectResult the effect result
     * @param playerId the player
     * @param cardPile the card pile searched
     * @return true or false
     */
    public static boolean isOwnSearchNotFound(EffectResult effectResult, String playerId, Zone cardPile) {
        if (effectResult instanceof LegacySearchNotFoundResult) {
            LegacySearchNotFoundResult result = (LegacySearchNotFoundResult) effectResult;
            return playerId.equals(result.getPerformingPlayerId())
                    && playerId.equals(result._cardPileOwner)
                    && result._cardPile == cardPile;
        }
        return false;
    }

    public String getCardPileOwner() {
        return _cardPileOwner;
    }

    public Zone getCardPile() {
        return _cardPile;
    }

    public SearchKind getSearchKind() {
        return _searchKind;
    }

    /**
     * Gets the card whose action made the search.
     * @return the card, or null
     */
    public PhysicalCard getSearchSource() {
        return _searchSource;
    }

    /**
     * Creates an effect that repeats the failed search in the specified card pile.
     * @param action the action performing the new search
     * @param zone the card pile to search
     * @param reshuffle true if the card pile is reshuffled afterwards, otherwise false
     * @return the effect
     */
    public StandardEffect createSearchOfOtherPile(Action action, Zone zone, boolean reshuffle) {
        return _retry.createSearch(action, zone, reshuffle);
    }

    @Override
    public String getText(SwccgGame game) {
        return "Just failed to find a card in " + _cardPileOwner + "'s " + _cardPile.getHumanReadable() + " (attempted to " + _searchKind.getHumanReadable() + ")";
    }
}
