package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.AbilityEvaluator;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.LinkedList;
import java.util.List;

/** Virtual Block 3: A Jedi's Patience (V). */
public class Card1230_099 extends AbstractUsedOrLostInterrupt {
    public Card1230_099() {
        super(Side.LIGHT, 5, "A Jedi's Patience", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Early on in his duel with Vader, Luke took his time in trying to sense his opponent's weaknesses.");
        setGameText("USED: If a captive on table, activate 2 Force. LOST: Once per game, if a Dark Jedi present with a Skywalker (except Shmi), even as a non-frozen captive, they duel. Both players draw 2 destiny; add ability. Higher total wins. For remainder of turn, winner is immune to attrition and the loser may not move.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    public Filter getValidDuelParticipant(Side side, SwccgGame game, PhysicalCard self) {
        return side == Side.LIGHT
                ? Filters.and(Filters.Skywalker, Filters.not(Filters.Shmi), Filters.not(Filters.frozenCaptive))
                : Filters.Dark_Jedi;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        if (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.captive)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Activate 2 Force");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new ActivateForceEffect(action, playerId, 2));
                }
            });
            actions.add(action);
        }

        GameTextActionId duelId = GameTextActionId.LEGACY__A_JEDIS_PATIENCE__DUEL;
        final Filter darkFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.DARK);
        Filter skywalker = Filters.and(game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.LIGHT),
                Filters.presentWith(self, SpotOverride.INCLUDE_CAPTIVE, darkFilter));
        if (GameConditions.isOncePerGame(game, self, duelId)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, TargetingReason.TO_BE_DUELED, skywalker)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, duelId, CardSubtype.LOST);
            action.setText("Initiate duel");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Skywalker", SpotOverride.INCLUDE_CAPTIVE,
                    TargetingReason.TO_BE_DUELED, skywalker) {
                @Override
                protected void cardTargeted(final int lightGroup, final PhysicalCard light) {
                    action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi", TargetingReason.TO_BE_DUELED,
                            Filters.and(darkFilter, Filters.presentWith(light))) {
                        @Override
                        protected void cardTargeted(final int darkGroup, PhysicalCard dark) {
                            action.addAnimationGroup(light, dark);
                            action.allowResponses("Initiate duel between " + GameUtils.getCardLink(light) + " and " + GameUtils.getCardLink(dark),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(new DuelEffect(action, targetingAction.getPrimaryTargetCard(darkGroup),
                                                    targetingAction.getPrimaryTargetCard(lightGroup), getDuelDirections(game, self)));
                                        }
                                    });
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }

    private DuelDirections getDuelDirections(SwccgGame game, final PhysicalCard self) {
        return new DuelDirections() {
            @Override
            public boolean isEpicDuel() { return false; }

            @Override
            public boolean isCrossOverToDarkSideAttempt() { return false; }

            @Override
            public Evaluator getBaseDuelTotal(String playerId, DuelState duelState) {
                return new AbilityEvaluator(duelState.getCharacter(playerId));
            }

            @Override
            public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) { return 2; }

            @Override
            public void performDuelDirections(Action duelAction, SwccgGame game, DuelState duelState) {
                appendDuelDraw(duelAction, game.getDarkPlayer(), duelState);
                appendDuelDraw(duelAction, game.getLightPlayer(), duelState);
            }

            @Override
            public void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState) {
                if (duelState.getWinningCharacter() != null) {
                    duelAction.appendEffect(new AddUntilEndOfTurnModifierEffect(duelAction,
                            new ImmuneToAttritionModifier(self, duelState.getWinningCharacter()), "Winner is immune to attrition"));
                    duelAction.appendEffect(new AddUntilEndOfTurnModifierEffect(duelAction,
                            new MayNotMoveModifier(self, duelState.getLosingCharacter()), "Loser may not move"));
                }
            }
        };
    }

    private void appendDuelDraw(final Action action, final String playerId, final DuelState duelState) {
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                action.appendEffect(new DrawDestinyEffect(action, playerId,
                        game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), playerId), DestinyType.DUEL_DESTINY) {
                    @Override
                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards, List<Float> values, Float total) {
                        if (total != null)
                            duelState.increaseTotalDuelDestinyFromDraws(playerId, total, cards.size());
                    }
                });
            }
        });
    }
}
