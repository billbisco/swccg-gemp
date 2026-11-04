package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.AbilityEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfDuelModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelDestinyDrawsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Mission
 * Title: Hunt Down The Dark Acolyte
 */
public class Card1291_066 extends AbstractMission {
    public Card1291_066() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Hunt Down The Dark Acolyte", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V, false);
        setGameText("Deploy on Begun, The Clone War Has. Opponent's characters of ability > 4 are 'acolytes.' Once during your move phase, if no card here, may target Anakin (or Obi-Wan) and initiate a duel with an 'acolyte' present. Each player draws two destiny (those destinies may not be canceled). Add ability. If your total higher, stack 'acolyte' here face down (Mission complete). Otherwise, place target in your Used Pile.\n{Reward}: May immediately take a non-character card into hand from Reserve Deck; reshuffle.\n{Turn the Tide}: If a card with the same persona as card here is deployed, place this Mission in Used Pile.");
        addIcons(Icon.EPISODE_I, Icon.CLONE_ARMY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title(Card1291_050.TITLE);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.title(Card1291_050.TITLE);
    }

    /**
     * Opponent's characters of ability > 4 are 'acolytes.'
     */
    static Filter acolyte(PhysicalCard self) {
        return Filters.and(Filters.opponents(self), Filters.character, Filters.abilityMoreThan(4));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HUNT_DOWN_THE_DARK_ACOLYTE__INITIATE_DUEL;
        final Filter acolyte = acolyte(self);
        Filter targetFilter = Filters.and(Filters.your(self), Filters.or(Filters.Anakin, Filters.ObiWan), Filters.presentWith(self, acolyte));

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && !GameConditions.hasStackedCards(game, self)
                && GameConditions.canTarget(game, self, TargetingReason.TO_BE_DUELED, targetFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Initiate duel with an 'acolyte'");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Anakin or Obi-Wan", TargetingReason.TO_BE_DUELED, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard target) {
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose 'acolyte'", Filters.and(acolyte, Filters.presentWith(target))) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard acolyteCard) {
                                            action.addAnimationGroup(target, acolyteCard);
                                            action.allowResponses("Initiate a duel between " + GameUtils.getCardLink(target) + " and " + GameUtils.getCardLink(acolyteCard),
                                                    new RespondableEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                            action.appendEffect(
                                                                    new DuelEffect(action, acolyteCard, finalTarget, new HuntDownDuelDirections(self, finalTarget, acolyteCard)));
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    private static class HuntDownDuelDirections implements DuelDirections {
        private final PhysicalCard _self;
        private final PhysicalCard _target;
        private final PhysicalCard _acolyte;

        private HuntDownDuelDirections(PhysicalCard self, PhysicalCard target, PhysicalCard acolyte) {
            _self = self;
            _target = target;
            _acolyte = acolyte;
        }

        @Override
        public boolean isEpicDuel() {
            return false;
        }

        @Override
        public boolean isCrossOverToDarkSideAttempt() {
            return false;
        }

        @Override
        public Evaluator getBaseDuelTotal(String playerId, DuelState duelState) {
            return new AbilityEvaluator(duelState.getCharacter(playerId));
        }

        @Override
        public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
            return 2;
        }

        @Override
        public void performDuelDirections(final Action duelAction, SwccgGame game, final DuelState duelState) {
            duelAction.appendEffect(
                    new AddUntilEndOfDuelModifierEffect(duelAction,
                            new MayNotCancelDestinyDrawsModifier(_self, null, true), null));
            duelAction.appendEffect(
                    new DrawDestinyEffect(duelAction, game.getDarkPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getDarkPlayer()), DestinyType.DUEL_DESTINY) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, Float darkTotalDestiny) {
                            if (darkTotalDestiny != null) {
                                duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                            }
                            duelAction.appendEffect(
                                    new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> lightDestinyCardDraws, List<Float> lightDestinyDrawValues, Float lightTotalDestiny) {
                                            if (lightTotalDestiny != null) {
                                                duelState.increaseTotalDuelDestinyFromDraws(game.getLightPlayer(), lightTotalDestiny, lightDestinyCardDraws.size());
                                            }
                                        }
                                    });
                        }
                    });
        }

        @Override
        public void performDuelResults(final Action duelAction, final SwccgGame game, DuelState duelState) {
            final String playerId = _self.getOwner();
            if (playerId.equals(duelState.getWinner())) {
                // If your total higher, stack 'acolyte' here face down (Mission complete).
                duelAction.appendEffect(new StackCardFromTableEffect(duelAction, _acolyte, _self, true));
                // Reward: May immediately take a non-character card into hand from Reserve Deck; reshuffle.
                if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, _self, GameTextActionId.LEGACY__HUNT_DOWN_THE_DARK_ACOLYTE__TAKE_NON_CHARACTER)) {
                    duelAction.appendEffect(
                            new PlayoutDecisionEffect(duelAction, playerId,
                                    new YesNoDecision("Do you want to take a non-character card into hand from Reserve Deck?") {
                                        @Override
                                        protected void yes() {
                                            duelAction.appendEffect(
                                                    new TakeCardIntoHandFromReserveDeckEffect(duelAction, playerId, Filters.not(Filters.character), true));
                                        }
                                    }));
                }
            } else {
                // Otherwise, place target in your Used Pile.
                duelAction.appendEffect(new PlaceCardInUsedPileFromTableEffect(duelAction, _target));
            }
        }
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Turn the Tide: If a card with the same persona as card here is deployed, place this Mission in Used Pile.
        Collection<PhysicalCard> stacked = game.getGameState().getStackedCards(self);
        if (!stacked.isEmpty()) {
            PhysicalCard here = stacked.iterator().next();
            Filter samePersona = here.getBlueprint().getPersonas().isEmpty()
                    ? Filters.title(here.getBlueprint().getTitle())
                    : Filters.samePersonaAs(here);
            if (TriggerConditions.justDeployed(game, effectResult, samePersona)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Place in Used Pile");
                action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
