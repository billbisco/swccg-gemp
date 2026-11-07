package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.AbilityEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.CrossOverCharacterEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayPlayToInitiateEpicDuelModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Set: Virtual Block 4
 * Type: Epic Event
 * Title: Deep Hatred
 */
public class Card1240_037 extends AbstractEpicEventDeployable {
    public Card1240_037() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Deep_Hatred, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Unless your [Reflections III] Objective on table, deploy on table. Vader's Obsession may be played to initiate an epic duel as follows: If Mara is present with Luke, each player draws two destiny (if armed with a lightsaber, each destiny is +1). Add character's ability. If Karrde on table, your total is -1. Higher total wins. If Luke loses, place him out of play and you may retrieve 5 Force. If Mara loses, cross her to the Light Side (she is now Mara Skywalker). After the duel (unless it was canceled or tied) Mara is ability = 6, adds one battle destiny, and once per battle involving her, may cancel one just drawn destiny.");
        addIcons(Icon.EPISODE_I, Icon.REFLECTIONS_III);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        // Let Them Make The First Move supplies this card as part of its required setup.
        return game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS
                || !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Objective, Icon.REFLECTIONS_III));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayPlayToInitiateEpicDuelModifier(self, Filters.and(Filters.your(self), Filters.Vaders_Obsession)),
                new MayBeTargetedByModifier(self, Filters.any_Mara, Title.Vaders_Obsession));
    }

    @Override
    public Map<PhysicalCard, Collection<PhysicalCard>> getInitiateEpicDuelMatchup(SwccgGame game, PhysicalCard self, Filter darkSideParticipantFilter, Filter lightSideParticipantFilter) {
        Map<PhysicalCard, Collection<PhysicalCard>> matchupsMap = new HashMap<PhysicalCard, Collection<PhysicalCard>>();
        Collection<PhysicalCard> darkSideParticipants = Filters.filterActive(game, self, Filters.and(darkSideParticipantFilter, Filters.any_Mara));
        for (PhysicalCard darkSideCharacter : darkSideParticipants) {
            Collection<PhysicalCard> lightSideParticipants = Filters.filterActive(game, self, null, TargetingReason.TO_BE_DUELED,
                    Filters.and(lightSideParticipantFilter, Filters.Luke, Filters.presentWith(darkSideCharacter)));
            if (!lightSideParticipants.isEmpty()) {
                matchupsMap.put(darkSideCharacter, lightSideParticipants);
            }
        }
        return matchupsMap;
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
                return new DeepHatredDuelTotalEvaluator(game, playerId, duelState);
            }

            @Override
            public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
                return 2;
            }

            @Override
            public void performDuelDirections(final Action duelAction, SwccgGame game, final DuelState duelState) {
                duelAction.appendEffect(drawDuelDestiny(duelAction, game, duelState, game.getDarkPlayer(), game.getLightPlayer()));
            }

            @Override
            public void performDuelResults(final Action action, SwccgGame game, DuelState duelState) {
                String loser = duelState.getLoser();
                if (duelState.isCanceled() || loser == null) {
                    return;
                }

                PhysicalCard mara = duelState.getCharacter(game.getDarkPlayer());
                if (loser.equals(game.getLightPlayer())) {
                    action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, duelState.getLosingCharacter()));
                    if (GameConditions.hasLostPile(game, game.getDarkPlayer())) {
                        String darkPlayer = game.getDarkPlayer();
                        action.appendEffect(new PlayoutDecisionEffect(action, darkPlayer, new YesNoDecision("Retrieve 5 Force?") {
                            @Override
                            protected void yes() {
                                action.insertEffect(new RetrieveForceEffect(action, darkPlayer, 5));
                            }
                        }));
                    }
                } else {
                    action.appendEffect(new CrossOverCharacterEffect(action, mara));
                }

                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        grantAftermath(action, game, mara, duelState.getCardInitiatedDuel());
                    }
                });
            }
        };
    }

    private static DrawDestinyEffect drawDuelDestiny(Action action, SwccgGame game, DuelState duel,
                                                       String player, String nextPlayer) {
        int draws = game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), player);
        return new DrawDestinyEffect(action, player, draws, DestinyType.DUEL_DESTINY) {
            @Override
            protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState draw) {
                if (Filters.armedWith(Filters.lightsaber).accepts(game, duel.getCharacter(player))) {
                    return List.of(new EachDestinyModifier(action.getActionSource(), draw.getId(), 1));
                }
                return null;
            }

            @Override
            protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards, List<Float> values, Float total) {
                if (total != null) {
                    duel.increaseTotalDuelDestinyFromDraws(player, total, cards.size());
                }
                if (nextPlayer != null) {
                    action.appendEffect(drawDuelDestiny(action, game, duel, nextPlayer, null));
                }
            }
        };
    }

    private static void grantAftermath(Action action, SwccgGame game, PhysicalCard mara, PhysicalCard initiator) {
        // This is the Mara who fought, including her crossed-over identity; a later deployment is a new card.
        Filter participant = Filters.and(Filters.onTable, Filters.sameCardId(mara));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new ResetAbilityModifier(mara, participant, 6), null));
        for (String player : List.of(game.getLightPlayer(), game.getDarkPlayer())) {
            Filter participatingForPlayer = Filters.and(participant, Filters.owner(player));
            action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                    new AddsBattleDestinyModifier(mara, new InBattleCondition(mara, participatingForPlayer),
                            1, player, true), null));
        }

        int sourceId = initiator.getCardId();
        action.appendEffect(new AddUntilEndOfGameActionProxyEffect(action, new AbstractActionProxy() {
            @Override
            public List<TriggerAction> getOptionalAfterTriggers(String player, SwccgGame game, EffectResult result) {
                if (player.equals(mara.getOwner())
                        && GameConditions.isDuringBattleWithParticipant(game, participant)
                        && GameConditions.isOncePerBattle(game, mara, player, sourceId)
                        && GameConditions.canCancelDestiny(game, player)
                        && TriggerConditions.isDestinyJustDrawn(game, result)) {
                    var cancel = new OptionalGameTextTriggerAction(mara, player, sourceId);
                    cancel.setText("Cancel a just drawn destiny");
                    cancel.appendUsage(new OncePerBattleEffect(cancel));
                    cancel.appendEffect(new CancelDestinyEffect(cancel));
                    return List.of(cancel);
                }
                return Collections.emptyList();
            }
        }));
    }

    private static class DeepHatredDuelTotalEvaluator extends BaseEvaluator {
        private final SwccgGame _game;
        private final String _playerId;
        private final DuelState _duelState;

        private DeepHatredDuelTotalEvaluator(SwccgGame game, String playerId, DuelState duelState) {
            _game = game;
            _playerId = playerId;
            _duelState = duelState;
        }

        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
            float ability = new AbilityEvaluator(_duelState.getCharacter(_playerId)).evaluateExpression(gameState, modifiersQuerying, cardAffected);
            if (_playerId.equals(_game.getDarkPlayer())
                    && GameConditions.canSpot(_game, _duelState.getCharacter(_playerId), Filters.Talon_Karrde)) {
                return ability - 1;
            }
            return ability;
        }
    }
}
