package com.gempukku.swccgo.cards.set1110.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 10: Fourth Anthology
 * Type: Interrupt
 * Subtype: Lost
 * Title: Vader's Anger
 */
public class Card1110_013 extends AbstractLostInterrupt {
    public Card1110_013() {
        super(Side.DARK, 5, "Vader's Anger", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_10, Rarity.V);
        setVirtualSuffix(true);
        setLore("Anger and aggression fuel the dark side of the Force.");
        setGameText("Target a Jedi present with Vader. Initiate an epic duel between them. Both players draw two destiny and add power. Loser (lowest total) is placed out of play and losing player loses 5 Force. OR If Vader just hit a Jedi, retrieve any three cards.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    public Filter getValidDuelParticipant(Side side, SwccgGame game, final PhysicalCard self) {
        if (side == Side.DARK)
            return Filters.Vader;
        else
            return Filters.Jedi;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter darkSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.DARK);
        Filter lightSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.LIGHT);
        Filter lightSideToTargetFilter = Filters.and(lightSideFilter, Filters.presentAt(Filters.site), Filters.presentWith(self, darkSideFilter));
        if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_DUELED, lightSideToTargetFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Initiate epic duel");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jedi", TargetingReason.TO_BE_DUELED, lightSideToTargetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedCard1) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose Vader", Filters.and(darkSideFilter, Filters.presentWith(targetedCard1))) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedCard2) {
                                            action.addAnimationGroup(targetedCard1, targetedCard2);
                                            action.allowResponses("Initiate an epic duel between " + GameUtils.getCardLink(targetedCard2) + " and " + GameUtils.getCardLink(targetedCard1),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action action) {
                                                            PhysicalCard lightCardToDuel = action.getPrimaryTargetCard(targetGroupId1);
                                                            PhysicalCard darkCardToDuel = action.getPrimaryTargetCard(targetGroupId2);
                                                            action.appendEffect(
                                                                    new DuelEffect(action, darkCardToDuel, lightCardToDuel,
                                                                            new DuelDirections() {
                                                                                @Override
                                                                                public boolean isEpicDuel() {
                                                                                    return true;
                                                                                }

                                                                                @Override
                                                                                public boolean isCrossOverToDarkSideAttempt() {
                                                                                    return false;
                                                                                }

                                                                                @Override
                                                                                public Evaluator getBaseDuelTotal(final String playerForTotal, final DuelState duelState) {
                                                                                    return new BaseEvaluator() {
                                                                                        @Override
                                                                                        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                                                                                            return modifiersQuerying.getPower(gameState, duelState.getCharacter(playerForTotal));
                                                                                        }
                                                                                    };
                                                                                }

                                                                                @Override
                                                                                public int getBaseNumDuelDestinyDraws(String playerForDraws, DuelState duelState) {
                                                                                    return 2;
                                                                                }

                                                                                @Override
                                                                                public void performDuelDirections(final Action duelAction, SwccgGame game, final DuelState duelState) {
                                                                                    duelAction.appendEffect(
                                                                                            new DrawDestinyEffect(duelAction, game.getDarkPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getDarkPlayer()), DestinyType.DUEL_DESTINY) {
                                                                                                @Override
                                                                                                protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
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
                                                                                                            }
                                                                                                    );
                                                                                                }
                                                                                            }
                                                                                    );
                                                                                }

                                                                                @Override
                                                                                public void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState) {
                                                                                    PhysicalCard loser = duelState.getLosingCharacter();
                                                                                    if (loser != null) {
                                                                                        duelAction.appendEffect(
                                                                                                new PlaceCardOutOfPlayFromTableEffect(duelAction, loser));
                                                                                        duelAction.appendEffect(
                                                                                                new LoseForceEffect(duelAction, loser.getOwner(), 5));
                                                                                    }
                                                                                }
                                                                            }));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.Jedi, Filters.any, Filters.Vader)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Retrieve any three cards");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new RetrieveCardsEffect(action, playerId, 3));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
