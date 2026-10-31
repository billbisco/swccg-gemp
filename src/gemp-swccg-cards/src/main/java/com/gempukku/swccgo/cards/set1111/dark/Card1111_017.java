package com.gempukku.swccgo.cards.set1111.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.AbilityEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.CrossOverCharacterEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Epic Event
 * Title: Deep Hatred
 */
public class Card1111_017 extends AbstractEpicEventDeployable {
    public Card1111_017() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Deep_Hatred, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Unless your [Reflections III] Objective on table, deploy on table. Vader's Obsession may be played to initiate an epic duel as follows: If Mara is present with Luke, each player draws two destiny (if armed with a lightsaber, each destiny is +1). Add character's ability. If Karrde on table, your total is -1. Higher total wins. If Luke loses, place him out of play and you may retrieve 5 Force. If Mara loses, cross her to the Light Side (she is now Mara Skywalker). Unless duel canceled or tied, for remainder of game any Mara is ability = 6, adds one battle destiny, and once per battle involving her, may cancel one just drawn destiny.");
        addIcons(Icon.EPISODE_I, Icon.REFLECTIONS_III);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Objective, Icon.REFLECTIONS_III));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Condition remainder = new RemainderOfGameCondition(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayPlayToInitiateEpicDuelModifier(self, Filters.Vaders_Obsession));
        modifiers.add(new MayBeTargetedByModifier(self, Filters.any_Mara, Title.Vaders_Obsession));
        modifiers.add(new ResetAbilityModifier(self, Filters.any_Mara, remainder, 6));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(remainder, new InBattleCondition(self, Filters.any_Mara)), 1, playerId, true));
        return modifiers;
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
                duelAction.appendEffect(
                        new DrawDestinyEffect(duelAction, game.getDarkPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getDarkPlayer()), DestinyType.DUEL_DESTINY) {
                            @Override
                            protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                if (Filters.armedWith(Filters.lightsaber).accepts(game, duelState.getCharacter(game.getDarkPlayer()))) {
                                    Modifier modifier = new EachDestinyModifier(duelAction.getActionSource(), drawDestinyState.getId(), 1);
                                    return Collections.singletonList(modifier);
                                }
                                return null;
                            }
                            @Override
                            protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                if (darkTotalDestiny != null) {
                                    duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                }
                                duelAction.appendEffect(
                                        new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
                                            @Override
                                            protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                if (Filters.armedWith(Filters.lightsaber).accepts(game, duelState.getCharacter(game.getLightPlayer()))) {
                                                    Modifier modifier = new EachDestinyModifier(duelAction.getActionSource(), drawDestinyState.getId(), 1);
                                                    return Collections.singletonList(modifier);
                                                }
                                                return null;
                                            }
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
            public void performDuelResults(final Action action, SwccgGame game, DuelState duelState) {
                final String loser = duelState.getLoser();
                if (loser == null) {
                    return;
                }

                PhysicalCard source = action.getActionSource();
                if (source != null) {
                    source.setWhileInPlayData(new WhileInPlayData(true));
                }

                final PhysicalCard losingCharacter = duelState.getLosingCharacter();
                if (loser.equals(game.getLightPlayer()) && Filters.Luke.accepts(game.getGameState(), game.getModifiersQuerying(), losingCharacter)) {
                    action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, losingCharacter));
                    action.appendEffect(new RetrieveForceEffect(action, game.getDarkPlayer(), 5));
                }
                else if (loser.equals(game.getDarkPlayer()) && Filters.any_Mara.accepts(game.getGameState(), game.getModifiersQuerying(), losingCharacter)) {
                    action.appendEffect(new CrossOverCharacterEffect(action, losingCharacter));
                }
            }
        };
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (self.getWhileInPlayData() != null && self.getWhileInPlayData().getBooleanValue()
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.any_Mara)
                && GameConditions.canCancelDestiny(game, playerId)
                && TriggerConditions.isDestinyJustDrawn(game, effectResult)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel a just drawn destiny");
            action.appendUsage(new OncePerBattleEffect(action));
            action.appendEffect(new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    private static class RemainderOfGameCondition implements Condition {
        private final PhysicalCard _self;

        private RemainderOfGameCondition(PhysicalCard self) {
            _self = self;
        }

        @Override
        public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
            return _self.getWhileInPlayData() != null && _self.getWhileInPlayData().getBooleanValue();
        }
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
                    && GameConditions.canSpot(_game, _duelState.getCharacter(_playerId), Filters.title("Talon Karrde"))) {
                return ability - 1;
            }
            return ability;
        }
    }
}
