package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set1110.dark.Card1110_013;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5: Vader's Anger (V). */
public class Card1250_029 extends Card1110_013 {
    public Card1250_029() {
        setGameText("During any move phase, initiate an epic duel between Vader and a Jedi present with him. Both players draw two destiny and add power. Loser (lowest total) is placed in Used Pile (if Epic Duel on table, Lost Pile instead), losing player loses 2 Force (cannot be reduced), and winning player may retrieve 2 Force.");
        addIcon(Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)) return null;

        Filter vader = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.DARK);
        Filter jedi = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.LIGHT);
        Filter target = Filters.and(jedi, Filters.presentAt(Filters.site), Filters.presentWith(self, vader));
        if (!GameConditions.canTarget(game, self, TargetingReason.TO_BE_DUELED, target)) return null;

        var action = new PlayInterruptAction(game, self);
        action.setText("Initiate epic duel");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Jedi", TargetingReason.TO_BE_DUELED, target) {
            @Override
            protected void cardTargeted(int jediGroup, PhysicalCard selectedJedi) {
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Vader",
                        Filters.and(vader, Filters.presentWith(selectedJedi))) {
                    @Override
                    protected void cardTargeted(int vaderGroup, PhysicalCard selectedVader) {
                        action.addAnimationGroup(selectedJedi, selectedVader);
                        action.allowResponses("Initiate epic duel between Vader and the selected Jedi",
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        action.appendEffect(new DuelEffect(action,
                                                action.getPrimaryTargetCard(vaderGroup), action.getPrimaryTargetCard(jediGroup),
                                                duelDirections(self)));
                                    }
                                });
                    }
                });
            }
        });
        return List.of(action);
    }

    private DuelDirections duelDirections(PhysicalCard source) {
        return new DuelDirections() {
            @Override public boolean isEpicDuel() { return true; }
            @Override public boolean isCrossOverToDarkSideAttempt() { return false; }
            @Override public int getBaseNumDuelDestinyDraws(String player, DuelState duel) { return 2; }

            @Override
            public Evaluator getBaseDuelTotal(String player, DuelState duel) {
                return new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState state, ModifiersQuerying querying, PhysicalCard affected) {
                        return querying.getPower(state, duel.getCharacter(player));
                    }
                };
            }

            @Override
            public void performDuelDirections(Action action, SwccgGame game, DuelState duel) {
                drawDuelDestiny(action, game, duel, game.getDarkPlayer(), game.getLightPlayer());
            }

            @Override
            public void performDuelResults(Action action, SwccgGame game, DuelState duel) {
                PhysicalCard loser = duel.getLosingCharacter();
                PhysicalCard winner = duel.getWinningCharacter();
                if (loser == null || winner == null) return;

                if (GameConditions.canSpot(game, source, Filters.Epic_Duel)) {
                    action.appendEffect(new LoseCardFromTableEffect(action, loser));
                } else {
                    action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, loser));
                }
                action.appendEffect(new LoseForceEffect(action, loser.getOwner(), 2, true));
                String winningPlayer = winner.getOwner();
                action.appendEffect(new PlayoutDecisionEffect(action, winningPlayer, new YesNoDecision("Retrieve 2 Force?") {
                    @Override
                    public void yes() {
                        action.appendEffect(new RetrieveForceEffect(action, winningPlayer, 2));
                    }
                }));
            }
        };
    }

    private void drawDuelDestiny(Action action, SwccgGame game, DuelState duel, String player, String nextPlayer) {
        int count = game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), player);
        action.appendEffect(new DrawDestinyEffect(action, player, count, DestinyType.DUEL_DESTINY) {
            @Override
            protected void destinyDraws(SwccgGame game, List<PhysicalCard> draws, List<Float> values, Float total) {
                if (total != null) duel.increaseTotalDuelDestinyFromDraws(player, total, draws.size());
                if (nextPlayer != null) drawDuelDestiny(action, game, duel, nextPlayer, null);
            }
        });
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
                                                                       EffectResult result, PhysicalCard self) {
        return null;
    }
}
