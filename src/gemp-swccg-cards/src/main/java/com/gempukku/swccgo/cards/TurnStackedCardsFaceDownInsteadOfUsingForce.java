package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.conditions.PlayersTurnCondition;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.AbstractGameTextAction;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.MaySubstituteForUsingForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Game text "During your turn ... Instead of using X Force, may turn X cards here face down." (It Is The Future You
 * See, Jedi mode). While active, each face-up card stacked on the source card stands in for 1 Force whenever its owner
 * uses Force during their turn (deploys, game text costs, etc.). Works like Beggar: a "before using Force" trigger asks
 * how many cards to turn face down, and that much less Force is used. The trigger is required only when the player
 * cannot otherwise pay (own Force Pile plus any opponent's Force they may use).
 */
public final class TurnStackedCardsFaceDownInsteadOfUsingForce {
    private TurnStackedCardsFaceDownInsteadOfUsingForce() {
    }

    /** The modifier that counts the face-up cards stacked on self as Force available to its owner during their turn. */
    public static Modifier modifier(final PhysicalCard self) {
        return new MaySubstituteForUsingForceModifier(self, new PlayersTurnCondition(self.getOwner()), new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                return faceUpCards(gameState.getGame(), self).size();
            }
        }, self.getOwner());
    }

    /** The optional trigger: turn up to (Force still to use from own Force Pile) cards face down. */
    public static List<OptionalGameTextTriggerAction> optionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        UseForceEffect useForceEffect = usingForce(game, effect, self);
        if (useForceEffect == null || !playerId.equals(self.getOwner()))
            return null;
        int max = Math.min(faceUpCards(game, self).size(), ownForceToUse(useForceEffect));
        if (max <= 0)
            return null;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
        action.setRepeatableTrigger(true);
        action.setText("Turn cards face down instead of using Force");
        appendDecision(game, action, useForceEffect, self, 1, max);
        return Collections.singletonList(action);
    }

    /** The required trigger: the player cannot pay the rest with their own Force Pile and any opponent's Force. */
    public static List<RequiredGameTextTriggerAction> requiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        UseForceEffect useForceEffect = usingForce(game, effect, self);
        if (useForceEffect == null || useForceEffect.isSubstitutionDeclined())
            return null;
        String playerId = self.getOwner();
        int ownForceToUse = ownForceToUse(useForceEffect);
        int shortfall = ownForceToUse - game.getGameState().getForcePile(playerId).size();
        int max = Math.min(faceUpCards(game, self).size(), ownForceToUse);
        if (shortfall <= 0 || max <= 0)
            return null;
        // Opponent's Force the player may still use (e.g. via Beggar) can cover the rest instead; then 0 is allowed.
        int opponentsForceStillAvailable = Math.max(0, game.getModifiersQuerying().getOpponentsForceAvailableToUse(game.getGameState(), playerId)
                - useForceEffect.getAmountForOpponentToUse());
        int min = Math.min(max, Math.max(0, shortfall - opponentsForceStillAvailable));
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setRepeatableTrigger(true);
        action.setText("Turn cards face down instead of using Force");
        appendDecision(game, action, useForceEffect, self, min, max);
        return Collections.singletonList(action);
    }

    private static void appendDecision(final SwccgGame game, AbstractGameTextAction action, final UseForceEffect useForceEffect, final PhysicalCard self, final int min, final int max) {
        final String playerId = self.getOwner();
        String text = "Choose number of cards to turn face down instead of using Force" + (min == 0 ? " (0 to use other Force)" : "");
        action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                        new IntegerAwaitingDecision(text, min, max, max) {
                            @Override
                            public void decisionMade(int result) throws DecisionResultInvalidException {
                                List<PhysicalCard> faceUp = new ArrayList<PhysicalCard>(faceUpCards(game, self));
                                int amount = Math.max(0, Math.min(Math.min(result, max), Math.min(faceUp.size(), ownForceToUse(useForceEffect))));
                                if (amount == 0) {
                                    useForceEffect.setSubstitutionDeclined();
                                    return;
                                }
                                GameState gameState = game.getGameState();
                                for (int i = 0; i < amount; i++) {
                                    gameState.relocateCardAsStacked(faceUp.get(i), self, true, false);
                                }
                                useForceEffect.addAmountSubstituted(amount);
                                gameState.sendMessage(playerId + " turns " + amount + " card" + GameUtils.s(amount) + " on "
                                        + GameUtils.getCardLink(self) + " face down instead of using " + amount + " Force");
                            }
                        }));
    }

    private static UseForceEffect usingForce(SwccgGame game, Effect effect, PhysicalCard self) {
        if (!TriggerConditions.isUsingForce(game, effect, self.getOwner())
                || !GameConditions.isDuringYourTurn(game, self.getOwner()))
            return null;
        UseForceEffect useForceEffect = (UseForceEffect) effect;
        // A payment limited to the Force Pile cards (e.g. Greedo's "use all Force Pile cards") may not be substituted
        return useForceEffect.canOnlyUseOwnForce() ? null : useForceEffect;
    }

    private static int ownForceToUse(UseForceEffect useForceEffect) {
        return useForceEffect.getTotalAmountOfForceToUse() - useForceEffect.getAmountForOpponentToUse();
    }

    private static List<PhysicalCard> faceUpCards(SwccgGame game, PhysicalCard self) {
        return new ArrayList<PhysicalCard>(Filters.filter(game.getGameState().getStackedCards(self), game, Zone.STACKED));
    }
}
