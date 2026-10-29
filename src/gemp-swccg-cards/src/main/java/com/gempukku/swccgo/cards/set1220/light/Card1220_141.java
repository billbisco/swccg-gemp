package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Lost
 * Title: Courage Of A Skywalker
 */
public class Card1220_141 extends AbstractLostInterrupt {
    public Card1220_141() {
        super(Side.LIGHT, 2, Title.Courage_Of_A_Skywalker, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Despite being alone, trapped and desperately outmatched, Luke continued his battle with the Dark Lord of the Sith.");
        setGameText("During battle, unless Inner Strength on table, initiate a duel between your participating Skywalker (except Shmi) present and a participating Dark Jedi present. Both players draw two destiny and add ability. Higher total wins. Loser is lost.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    public Filter getValidDuelParticipant(Side side, SwccgGame game, final PhysicalCard self) {
        if (side == Side.LIGHT) {
            return Filters.and(Filters.your(self), Filters.Skywalker, Filters.not(Filters.Shmi), Filters.participatingInBattle, Filters.presentAt(Filters.site));
        }
        return Filters.and(Filters.Dark_Jedi, Filters.participatingInBattle, Filters.presentAt(Filters.site));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        if (GameConditions.isDuringBattle(game)
                && !GameConditions.canSpot(game, self, Filters.Inner_Strength)) {
            final Filter darkSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.DARK);
            Filter lightSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.LIGHT);
            Filter lightSideToTargetFilter = Filters.and(lightSideFilter, Filters.presentWith(self, darkSideFilter));
            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_DUELED, lightSideToTargetFilter)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Initiate duel");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Skywalker", TargetingReason.TO_BE_DUELED, lightSideToTargetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedCard1) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi", Filters.and(darkSideFilter, Filters.presentWith(targetedCard1))) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedCard2) {
                                                action.addAnimationGroup(targetedCard1, targetedCard2);
                                                action.allowResponses("Initiate duel between " + GameUtils.getCardLink(targetedCard1) + " and " + GameUtils.getCardLink(targetedCard2),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                PhysicalCard lightCardToDuel = action.getPrimaryTargetCard(targetGroupId1);
                                                                PhysicalCard darkCardToDuel = action.getPrimaryTargetCard(targetGroupId2);
                                                                action.appendEffect(
                                                                        new DuelEffect(action, darkCardToDuel, lightCardToDuel,
                                                                                new DuelDirections() {
                                                                                    @Override
                                                                                    public boolean isEpicDuel() {
                                                                                        return false;
                                                                                    }

                                                                                    @Override
                                                                                    public boolean isCrossOverToDarkSideAttempt() {
                                                                                        return false;
                                                                                    }

                                                                                    @Override
                                                                                    public Evaluator getBaseDuelTotal(final String playerId, final DuelState duelState) {
                                                                                        return new BaseEvaluator() {
                                                                                            @Override
                                                                                            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                                                                                                return modifiersQuerying.getAbility(gameState, duelState.getCharacter(playerId));
                                                                                            }
                                                                                        };
                                                                                    }

                                                                                    @Override
                                                                                    public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
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
                                                                                        if (duelState.getLosingCharacter() != null) {
                                                                                            duelAction.appendEffect(
                                                                                                    new LoseCardFromTableEffect(duelAction, duelState.getLosingCharacter()));
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
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
