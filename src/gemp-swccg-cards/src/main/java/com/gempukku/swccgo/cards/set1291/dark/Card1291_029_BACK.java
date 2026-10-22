package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
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
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Objective
 * Title: Separatist Uprising / At War With Itself
 */
public class Card1291_029_BACK extends AbstractObjective {
    public Card1291_029_BACK() {
        super(Side.DARK, 7, "At War With Itself", ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText(Card1291_029.PACK_GAMETEXT);
        addIcons(Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.addAll(Card1291_029.remainderOfGameModifiers(self));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, null, -1, opponent, true));
        modifiers.add(new TotalPowerModifier(self, Filters.any, new CompleteSeparatistMissionEvaluator(self), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId takeId = GameTextActionId.LEGACY__AT_WAR_WITH_ITSELF__TAKE_SEPARATIST_CARD;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, takeId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, takeId);
            action.setText("Take [Separatist] card into hand from Reserve Deck");
            action.setActionMsg("Take a [Separatist] character, vehicle, or starship into hand from Reserve Deck");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                            Filters.and(Icon.SEPARATIST, Filters.or(Filters.character, Filters.vehicle, Filters.starship)), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId retrieveId = GameTextActionId.LEGACY__AT_WAR_WITH_ITSELF__RETRIEVE_FORCE;
        if (TriggerConditions.cardFlipped(game, effectResult, self)
                && GameConditions.isOncePerGame(game, self, retrieveId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, retrieveId);
            action.setText("Retrieve up to 3 Force");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new IntegerAwaitingDecision("Choose amount of Force to retrieve", 0, 3, 3) {
                                @Override
                                public void decisionMade(final int amountToRetrieve) throws DecisionResultInvalidException {
                                    game.getGameState().sendMessage(playerId + " chooses to retrieve " + amountToRetrieve + " Force");
                                    if (amountToRetrieve > 0) {
                                        action.appendEffect(
                                                new RetrieveForceEffect(action, playerId, amountToRetrieve) {
                                                    @Override
                                                    public boolean mayBeTakenIntoHand() {
                                                        return true;
                                                    }
                                                });
                                    }
                                }
                            }));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isTableChanged(game, effectResult)) {
            RequiredGameTextTriggerAction loseAction = Card1291_029.makeRemainderLostAction(game, self, gameTextSourceCardId);
            if (loseAction != null) {
                actions.add(loseAction);
            }
            if (GameConditions.canBeFlipped(game, self)
                    && !GameConditions.canSpot(game, self, Card1291_029.completeSeparatistMission())) {
                RequiredGameTextTriggerAction flip = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                flip.setSingletonTrigger(true);
                flip.setText("Flip");
                flip.setActionMsg(null);
                flip.appendEffect(new FlipCardEffect(flip, self));
                actions.add(flip);
            }
        }

        if (TriggerConditions.wonBattle(game, effectResult, playerId)) {
            int x = Filters.countActive(game, self, Card1291_029.completeSeparatistMission());
            if (x > 0) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose " + x + " Force");
                action.appendEffect(new LoseForceEffect(action, opponent, x));
                actions.add(action);
            }
        }
        return actions;
    }

    private static class CompleteSeparatistMissionEvaluator extends BaseEvaluator {
        private final int _sourcePermId;

        private CompleteSeparatistMissionEvaluator(PhysicalCard source) {
            _sourcePermId = source.getPermanentCardId();
        }

        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
            SwccgGame game = gameState.getGame();
            PhysicalCard self = game.findCardByPermanentId(_sourcePermId);
            return Filters.countActive(game, self, Card1291_029.completeSeparatistMission());
        }

        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
            return evaluateExpression(gameState, modifiersQuerying, cardAffected);
        }
    }
}
