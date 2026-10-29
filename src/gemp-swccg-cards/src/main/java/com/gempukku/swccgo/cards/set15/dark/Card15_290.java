package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.cards.set1.dark.Card1_271;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Card15_290 extends Card1_271 {
    public Card15_290() {
        setCardSubtype(CardSubtype.LOST);
        setAlternateDestiny(3);
        setLore("");
        setGameText("If Vader and Obi-Wan are present at same site, use 1 Force to initiate a duel between them. Compare their power, and add 2 if that character is armed with a lightsaber. Loser (lowest total) of duel is placed out of play. OR During your move phase, if Vader moves to Luke's site from an adjacent site, begin a duel between them. Each adds two destiny to power. Winner retrieves lost Force equal to the difference. Loser loses same amount of Force, plus the character.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("The Circle Is Now Complete", "Vader's Obsession");
    }
    @Override public String getTitle() { return "The Circle Is Now Complete & Vader's Obsession"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {
            final Filter darkSideFilter = Filters.or(game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.DARK));
            Filter lightSideFilter = Filters.and(Filters.Luke, game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.LIGHT));
            if (TriggerConditions.movedFromLocationToLocation(game, effectResult, darkSideFilter, Filters.adjacentSiteTo(self, lightSideFilter), Filters.sameSiteAs(self, lightSideFilter))) {
                MovedResult movedResult = (MovedResult) effectResult;
                final Collection<PhysicalCard> movedCards = Filters.filter(movedResult.getMovedCards(), game, darkSideFilter);
                Filter lightSideToTargetFilter = Filters.and(lightSideFilter, Filters.at(Filters.site), Filters.with(self, Filters.in(movedCards)));
                if (movedResult.isMoveComplete()
                        && GameConditions.canTarget(game, self, TargetingReason.TO_BE_DUELED, lightSideToTargetFilter)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Initiate duel");
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose Light Side character", TargetingReason.TO_BE_DUELED, lightSideToTargetFilter) {
                                @Override
                                protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedCard1) {
                                    action.appendTargeting(
                                            new TargetCardOnTableEffect(action, playerId, "Choose Dark Side character", Filters.in(movedCards)) {
                                                @Override
                                                protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedCard2) {
                                                    action.addAnimationGroup(targetedCard1, targetedCard2);
                                                    // Allow response(s)
                                                    action.allowResponses("Initiate duel between " + GameUtils.getCardLink(targetedCard2) + " and " + GameUtils.getCardLink(targetedCard1),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action action) {
                                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                                    PhysicalCard lightCardToDuel = action.getPrimaryTargetCard(targetGroupId1);
                                                                    PhysicalCard darkCardToDuel = action.getPrimaryTargetCard(targetGroupId2);

                                                                    // Perform result(s)
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
                                                                                            return new PowerEvaluator(duelState.getCharacter(playerId));
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
                                                                                        public void performDuelResults(Action action, SwccgGame game, final DuelState duelState) {
                                                                                            if (duelState.getWinner() != null) {
                                                                                                float difference = duelState.getFinalDuelTotal(duelState.getWinner()) - duelState.getFinalDuelTotal(duelState.getLoser());

                                                                                                // Winner retrieves Force equal to difference
                                                                                                action.appendEffect(
                                                                                                        new RetrieveForceEffect(action, duelState.getWinner(), difference) {
                                                                                                            @Override
                                                                                                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                                                                                return Collections.singletonList(duelState.getWinningCharacter());
                                                                                                            }
                                                                                                        });
                                                                                                // Loser loses Force equal to difference
                                                                                                action.appendEffect(
                                                                                                        new LoseForceEffect(action, duelState.getLoser(), difference));
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, duelState.getLosingCharacter()));
                                                                                            }
                                                                                        }
                                                                                    }));
                                                                }
                                                            });
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final Filter darkSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.DARK);
            Filter lightSideFilter = Filters.and(Filters.ObiWan, game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.LIGHT));
            Filter lightSideToTargetFilter = Filters.and(lightSideFilter, Filters.presentAt(Filters.site), Filters.presentWith(self, darkSideFilter));
            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_DUELED, lightSideToTargetFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Initiate duel");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Light Side character", TargetingReason.TO_BE_DUELED, lightSideToTargetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedCard1) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Dark Side character", Filters.and(darkSideFilter, Filters.presentWith(targetedCard1))) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedCard2) {
                                                action.addAnimationGroup(targetedCard1, targetedCard2);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new UseForceEffect(action, playerId, 1));
                                                // Allow response(s)
                                                action.allowResponses("Initiate duel between " + GameUtils.getCardLink(targetedCard2) + " and " + GameUtils.getCardLink(targetedCard1),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action action) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard lightCardToDuel = action.getPrimaryTargetCard(targetGroupId1);
                                                                PhysicalCard darkCardToDuel = action.getPrimaryTargetCard(targetGroupId2);

                                                                // Perform result(s)
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
                                                                                                PhysicalCard character = duelState.getCharacter(playerId);
                                                                                                float total =  modifiersQuerying.getPower(gameState, character);
                                                                                                if (Filters.armedWith(Filters.lightsaber).accepts(gameState, modifiersQuerying, character)) {
                                                                                                    total += 2;
                                                                                                }
                                                                                                return total;
                                                                                            }
                                                                                        };
                                                                                    }

                                                                                    @Override
                                                                                    public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
                                                                                        return 0;
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
                                                                                    public void performDuelResults(Action action, SwccgGame game, DuelState duelState) {
                                                                                        // Place losing character out of play
                                                                                        if (duelState.getLosingCharacter() != null) {
                                                                                            action.appendEffect(
                                                                                                    new PlaceCardOutOfPlayFromTableEffect(action, duelState.getLosingCharacter()));
                                                                                        }
                                                                                    }
                                                                                }));
                                                            }
                                                        });
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    public Filter getValidDuelParticipant(Side side, SwccgGame game, PhysicalCard self) {
        return side == Side.DARK ? Filters.or(Filters.Vader, Filters.grantedMayBeTargetedBy(self)) : Filters.or(Filters.ObiWan, Filters.Luke);
    }
}
