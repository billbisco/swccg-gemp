package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ClearTargetedCardsEffect;
import com.gempukku.swccgo.cards.effects.SetTargetedCardEffect;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Subtype: Immediate
 * Title: Hindsight (V)
 */
public class Card1274_018 extends AbstractImmediateEffect {
    public Card1274_018() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Hindsight", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I'm backwards! You fleabitten furball...only an overgrown mophead like you would be stupid enough--'");
        setGameText("Unless Imperial Atrocity on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 4 (ignore other modifiers); opponent may take any one card into hand from Reserve Deck; reshuffle. Immune to Control. OR Use 2 Force to deploy on table and stack one just-played Interrupt here.");
        addIcons(Icon.GRABBER);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                && !GameConditions.canSpot(game, self, Filters.Imperial_Atrocity)) {
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
            if (action != null) {
                action.setText("Deploy to reduce Force loss by 4");
                action.appendBeforeCost(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(game.getGameState().getTopForceLossState())));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Interrupt)) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.canBeGrabbed(game, self, cardBeingPlayed)
                    && GameConditions.canUseForce(game, playerId, 2)) {

                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
                if (action != null) {
                    action.setText("Use 2 Force to deploy and stack " + GameUtils.getFullName(cardBeingPlayed));
                    action.appendBeforeCost(
                            new UseForceEffect(action, playerId, 2));
                    // Target the card to stack
                    action.appendTargeting(
                            new SetTargetedCardEffect(action, self, TargetId.IMMEDIATE_EFFECT_TARGET_1, null, cardBeingPlayed, Filters.samePermanentCardId(cardBeingPlayed)));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            // Mode 1: reduce Force loss by 4
            if (self.getWhileInPlayData() != null
                    && self.getWhileInPlayData().getForceLossState() != null) {
                ForceLossState forceLossState = self.getWhileInPlayData().getForceLossState();

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__HINDSIGHT_V__REDUCE_FORCE_LOSS);
                action.setPerformingPlayer(playerId);
                action.setText("Reduce Force loss by 4");
                action.appendUsage(
                        new OncePerForceLossEffect(action));
                action.appendCost(
                        new SetWhileInPlayDataEffect(action, self, null));
                // Perform result(s)
                if (GameConditions.canReduceForceLoss(game)) {
                    action.appendEffect(
                            new ReduceForceLossEffect(action, playerId, 4, 0, false, forceLossState));
                }
                if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, opponent, self, GameTextActionId.LEGACY__HINDSIGHT_V__OPPONENT_TAKES_CARD)) {
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, opponent,
                                    new YesNoDecision("Do you want to take any one card into hand from Reserve Deck?") {
                                        @Override
                                        protected void yes() {
                                            action.appendEffect(
                                                    new TakeCardIntoHandFromReserveDeckEffect(action, opponent, Filters.any, true));
                                        }
                                    }));
                }
                actions.add(action);
            }

            // Mode 2: stack the just-played Interrupt here
            PhysicalCard cardToGrab = self.getTargetedCard(game.getGameState(), TargetId.IMMEDIATE_EFFECT_TARGET_1);
            if (cardToGrab != null && GameConditions.canBeGrabbed(game, self, cardToGrab)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setPerformingPlayer(playerId);
                action.setText("Stack " + GameUtils.getFullName(cardToGrab) + " here");
                action.setActionMsg("Stack " + GameUtils.getCardLink(cardToGrab) + " on " + GameUtils.getCardLink(self));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromVoidEffect(action, cardToGrab, self));
                action.appendEffect(
                        new ClearTargetedCardsEffect(action, self));
                actions.add(action);
            }
        }
        return actions;
    }
}
