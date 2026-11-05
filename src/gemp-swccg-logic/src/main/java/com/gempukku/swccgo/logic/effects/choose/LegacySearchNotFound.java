package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.LegacySearchNotFoundResult;

import java.util.Collection;

/**
 * Reports searches that did not find the card they were looking for, so cards can respond to "if you just attempted to
 * take a card into hand or deploy a card from Reserve Deck, and the card was not found".
 *
 * Called from the two single-card search effects when their search ends with nothing taken or deployed:
 * TakeCardsIntoHandFromPileEffect (taking cards into hand from a pile by filter) and DeployCardFromPileEffect
 * (deploying a card from a pile by filter). Taking or deploying a specific, already-known card is not a search
 * and is not reported. Each report carries a {@link LegacySearchNotFoundResult.Retry} that repeats the same search
 * in another card pile.
 */
public final class LegacySearchNotFound {

    private LegacySearchNotFound() {
    }

    /**
     * Reports that a take-into-hand search found nothing. The result triggers after the search sub-action's
     * other effects (including any reshuffle).
     */
    static void tookNothingIntoHand(SubAction subAction, String playerId, Zone zone, String cardPileOwner, int minimum, int maximum, boolean topmost, Filter filters, boolean hidden) {
        report(subAction, playerId, cardPileOwner, zone, LegacySearchNotFoundResult.SearchKind.TAKE_INTO_HAND,
                new TakeIntoHandRetry(playerId, cardPileOwner, minimum, maximum, topmost, filters, hidden));
    }

    /**
     * Reports that a deploy search found nothing. The retry must be created from the search's criteria as they were
     * before the search ran (see {@link #deployRetry}).
     */
    static void deployedNothing(SubAction subAction, String playerId, Zone zone, String cardPileOwner, LegacySearchNotFoundResult.Retry retry) {
        report(subAction, playerId, cardPileOwner, zone, LegacySearchNotFoundResult.SearchKind.DEPLOY, retry);
    }

    /**
     * Captures the criteria of a deploy search so the same search can be repeated in another card pile.
     */
    static LegacySearchNotFoundResult.Retry deployRetry(String playerId, Filter cardFilter, Filter targetFilter, Filter ignoreTargetFilterCardFilter,
                                                         String targetSystem, Filter specialLocationConditions, boolean forFree, Filter forFreeCardFilter,
                                                         float changeInCost, Filter changeInCostCardFilter, DeploymentRestrictionsOption deploymentRestrictionsOption,
                                                         DeployAsCaptiveOption deployAsCaptiveOption, boolean asReact) {
        return new DeployRetry(playerId, cardFilter, targetFilter, ignoreTargetFilterCardFilter, targetSystem, specialLocationConditions, forFree,
                forFreeCardFilter, changeInCost, changeInCostCardFilter, deploymentRestrictionsOption, deployAsCaptiveOption, asReact);
    }

    private static void report(SubAction subAction, String playerId, String cardPileOwner, Zone zone, LegacySearchNotFoundResult.SearchKind searchKind,
                               LegacySearchNotFoundResult.Retry retry) {
        PhysicalCard searchSource = subAction.getActionSource();
        subAction.appendEffect(
                new TriggeringResultEffect(subAction,
                        new LegacySearchNotFoundResult(playerId, cardPileOwner, zone, searchKind, searchSource, retry)));
    }

    /**
     * Repeats a take-into-hand search in another card pile.
     */
    private static final class TakeIntoHandRetry extends LegacySearchNotFoundResult.Retry {
        private final String _playerId;
        private final String _cardPileOwner;
        private final int _minimum;
        private final int _maximum;
        private final boolean _topmost;
        private final Filter _filters;
        private final boolean _hidden;

        private TakeIntoHandRetry(String playerId, String cardPileOwner, int minimum, int maximum, boolean topmost, Filter filters, boolean hidden) {
            _playerId = playerId;
            _cardPileOwner = cardPileOwner;
            _minimum = minimum;
            _maximum = maximum;
            _topmost = topmost;
            _filters = filters;
            _hidden = hidden;
        }

        @Override
        public StandardEffect createSearch(Action action, Zone zone, boolean reshuffle) {
            // The filter already accepts cards with a matching permanent aboard, so do not add that again.
            return new TakeCardsIntoHandFromPileEffect(action, _playerId, _minimum, _maximum, zone, _cardPileOwner, _topmost, _filters, reshuffle, _hidden, true) {
                @Override
                protected void cardsTakenIntoHand(Collection<PhysicalCard> cards) {
                }
            };
        }
    }

    /**
     * Repeats a deploy search in another card pile.
     */
    private static final class DeployRetry extends LegacySearchNotFoundResult.Retry {
        private final String _playerId;
        private final Filter _cardFilter;
        private final Filter _targetFilter;
        private final Filter _ignoreTargetFilterCardFilter;
        private final String _targetSystem;
        private final Filter _specialLocationConditions;
        private final boolean _forFree;
        private final Filter _forFreeCardFilter;
        private final float _changeInCost;
        private final Filter _changeInCostCardFilter;
        private final DeploymentRestrictionsOption _deploymentRestrictionsOption;
        private final DeployAsCaptiveOption _deployAsCaptiveOption;
        private final boolean _asReact;

        private DeployRetry(String playerId, Filter cardFilter, Filter targetFilter, Filter ignoreTargetFilterCardFilter, String targetSystem,
                            Filter specialLocationConditions, boolean forFree, Filter forFreeCardFilter, float changeInCost, Filter changeInCostCardFilter,
                            DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, boolean asReact) {
            _playerId = playerId;
            _cardFilter = cardFilter;
            _targetFilter = targetFilter;
            _ignoreTargetFilterCardFilter = ignoreTargetFilterCardFilter;
            _targetSystem = targetSystem;
            _specialLocationConditions = specialLocationConditions;
            _forFree = forFree;
            _forFreeCardFilter = forFreeCardFilter;
            _changeInCost = changeInCost;
            _changeInCostCardFilter = changeInCostCardFilter;
            _deploymentRestrictionsOption = deploymentRestrictionsOption;
            _deployAsCaptiveOption = deployAsCaptiveOption;
            _asReact = asReact;
        }

        @Override
        public StandardEffect createSearch(Action action, Zone zone, boolean reshuffle) {
            return new DeployCardFromPileEffect(action, _playerId, zone, _cardFilter, _targetFilter, _ignoreTargetFilterCardFilter, _targetSystem,
                    _specialLocationConditions, _forFree, _forFreeCardFilter, _changeInCost, _changeInCostCardFilter, _deploymentRestrictionsOption,
                    _deployAsCaptiveOption, _asReact, reshuffle);
        }
    }
}
