package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnBottomOfForcePileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Objective
 * Title: Republic At War / Aggressive Negotiations
 */
public class Card1291_069_BACK extends AbstractObjective {
    public static final String TITLE = "Aggressive Negotiations";

    public Card1291_069_BACK() {
        super(Side.LIGHT, 7, TITLE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText(Card1291_069.PRINTED_GAMETEXT);
        addIcons(Icon.EPISODE_I);
    }

    private static int completeMissions(SwccgGame game, PhysicalCard self) {
        return Filters.countActive(game, self, Card1291_069.completeCloneArmyMission());
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.addAll(Card1291_069.remainderOfGameModifiers(self));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, null, -1, opponent, true));
        modifiers.add(new TotalPowerModifier(self, Filters.any, new CompleteMissionEvaluator(self), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId takeId = GameTextActionId.LEGACY__AGGRESSIVE_NEGOTIATIONS__TAKE_CLONE_ARMY_CARD;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, takeId);
            action.setText("Take [Clone Army] card into hand from Reserve Deck");
            action.setActionMsg("Take a [Clone Army] character, starship or vehicle into hand from Reserve Deck");
            action.appendCost(new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                            Filters.and(Icon.CLONE_ARMY, Filters.or(Filters.character, Filters.starship, Filters.vehicle)), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId drawId = GameTextActionId.LEGACY__AGGRESSIVE_NEGOTIATIONS__DRAW_CARDS;
        if (TriggerConditions.cardFlipped(game, effectResult, self)
                && GameConditions.isOncePerGame(game, self, drawId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, drawId);
            action.setText("Place X cards from hand on Force Pile to draw up to X+1 cards");
            action.appendUsage(new OncePerGameEffect(action));
            int handSize = game.getGameState().getHand(playerId).size();
            if (handSize == 0) {
                action.appendEffect(drawUpTo(action, playerId, 0));
            } else {
                action.appendEffect(
                        new ChooseCardsFromHandEffect(action, playerId, 0, handSize) {
                            @Override
                            public String getChoiceText(int numCardsToChoose) {
                                return "Choose X cards to place on bottom of Force Pile";
                            }

                            @Override
                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                for (PhysicalCard card : selectedCards) {
                                    action.appendEffect(
                                            new PutCardFromHandOnBottomOfForcePileEffect(action, playerId, Filters.sameCardId(card), false));
                                }
                                action.appendEffect(drawUpTo(action, playerId, selectedCards.size()));
                            }
                        });
            }
            return Collections.singletonList(action);
        }
        return null;
    }

    private static PassthruEffect drawUpTo(final Action action, final String playerId, final int x) {
        return new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(final SwccgGame game) {
                int max = Math.min(x + 1, game.getGameState().getReserveDeckSize(playerId));
                if (max <= 0) {
                    return;
                }
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose number of cards to draw from Reserve Deck", 0, max, max) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        game.getGameState().sendMessage(playerId + " chooses to draw " + result + " card(s) from Reserve Deck");
                                        if (result > 0) {
                                            action.appendEffect(new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, result));
                                        }
                                    }
                                }));
            }
        };
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        final String playerId = self.getOwner();

        if (TriggerConditions.isTableChanged(game, effectResult)) {
            RequiredGameTextTriggerAction loseAction = Card1291_069.makeRemainderLostAction(game, self, gameTextSourceCardId);
            if (loseAction != null) {
                actions.add(loseAction);
            }
            if (GameConditions.canBeFlipped(game, self)
                    && !GameConditions.canSpot(game, self, Card1291_069.completeCloneArmyMission())) {
                RequiredGameTextTriggerAction flip = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                flip.setSingletonTrigger(true);
                flip.setText("Flip");
                flip.setActionMsg(null);
                flip.appendEffect(new FlipCardEffect(flip, self));
                actions.add(flip);
            }
        }

        if (TriggerConditions.wonBattle(game, effectResult, playerId)) {
            final int y = completeMissions(game, self);
            if (y > 0) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Retrieve up to " + y + " Force");
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to retrieve", 0, y, y) {
                                    @Override
                                    public void decisionMade(int amount) throws DecisionResultInvalidException {
                                        game.getGameState().sendMessage(playerId + " chooses to retrieve " + amount + " Force");
                                        if (amount > 0) {
                                            action.appendEffect(new RetrieveForceEffect(action, playerId, amount));
                                        }
                                    }
                                }));
                actions.add(action);
            }
        }
        return actions;
    }

    private static class CompleteMissionEvaluator extends BaseEvaluator {
        private final int _sourcePermId;

        private CompleteMissionEvaluator(PhysicalCard source) {
            _sourcePermId = source.getPermanentCardId();
        }

        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
            SwccgGame game = gameState.getGame();
            PhysicalCard self = game.findCardByPermanentId(_sourcePermId);
            return completeMissions(game, self);
        }

        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
            return evaluateExpression(gameState, modifiersQuerying, cardAffected);
        }
    }
}
