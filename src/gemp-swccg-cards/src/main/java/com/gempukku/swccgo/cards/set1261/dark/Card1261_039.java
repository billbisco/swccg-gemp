package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.evaluators.AbilityEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.EachDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.MayPlayToInitiateEpicDuelModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Final Legacy Block 6.1 Epic Duel (V). */
public class Card1261_039 extends AbstractEpicEventDeployable {
    public Card1261_039() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Epic_Duel,
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        addIcons(Icon.CLOUD_CITY);
        setGameText("Deploy on table. A Jedi's Resilience may not be played during move phases. The Circle Is Now Complete or Vader's Obsession may be played during any move phase to initiate an epic duel as follows: If Vader present with specified target, each player draws two destiny (if armed with a lightsaber, each destiny is +1); add ability. Loser (lowest total) is lost, losing player loses 2 Force, and winning player may retrieve 2 Force.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var movePhase = new PhaseCondition(Phase.MOVE);
        return List.of(new MayNotPlayModifier(self, Filters.A_Jedis_Resilience, movePhase),
                new MayPlayToInitiateEpicDuelModifier(self, movePhase,
                        Filters.or(Filters.The_Circle_Is_Now_Complete, Filters.Vaders_Obsession)));
    }

    @Override
    public Map<PhysicalCard, Collection<PhysicalCard>> getInitiateEpicDuelMatchup(SwccgGame game,
            PhysicalCard self, Filter dark, Filter light) {
        Map<PhysicalCard, Collection<PhysicalCard>> matchups = new LinkedHashMap<>();
        for (PhysicalCard vader : Filters.filterActive(game, self, dark)) {
            var targets = Filters.filterActive(game, self, null, TargetingReason.TO_BE_DUELED,
                    Filters.and(light, Filters.presentWith(vader)));
            if (!targets.isEmpty())
                matchups.put(vader, targets);
        }
        return matchups;
    }

    @Override
    public DuelDirections getDuelDirections(SwccgGame game) {
        return new DuelDirections() {
            @Override
            public boolean isEpicDuel() {
                return true;
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
            public void performDuelDirections(Action action, SwccgGame game, DuelState duelState) {
                action.appendEffect(drawFor(action, game, duelState, game.getDarkPlayer(),
                        () -> action.appendEffect(drawFor(action, game, duelState, game.getLightPlayer(), () -> {}))));
            }

            @Override
            public void performDuelResults(Action action, SwccgGame game, DuelState duelState) {
                String loser = duelState.getLoser();
                if (loser == null)
                    return;
                action.appendEffect(new LoseCardFromTableEffect(action, duelState.getLosingCharacter()));
                action.appendEffect(new LoseForceEffect(action, loser, 2));
                String winner = duelState.getWinner();
                action.appendEffect(new PlayoutDecisionEffect(action, winner, new YesNoDecision("Retrieve 2 Force?") {
                    @Override
                    protected void yes() {
                        action.appendEffect(new RetrieveForceEffect(action, winner, 2));
                    }
                }));
            }
        };
    }

    private DrawDestinyEffect drawFor(Action action, SwccgGame game, DuelState duel, String player,
            Runnable afterDraw) {
        int count = game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), player);
        return new DrawDestinyEffect(action, player, count, DestinyType.DUEL_DESTINY) {
            @Override
            protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState state) {
                return Filters.armedWith(Filters.lightsaber).accepts(game, duel.getCharacter(player))
                        ? List.of(new EachDestinyModifier(action.getActionSource(), state.getId(), 1)) : null;
            }

            @Override
            protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards, List<Float> values, Float total) {
                if (total != null)
                    duel.increaseTotalDuelDestinyFromDraws(player, total, cards.size());
                afterDraw.run();
            }
        };
    }
}
