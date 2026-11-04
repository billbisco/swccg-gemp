package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ClearTargetedCardsEffect;
import com.gempukku.swccgo.cards.effects.SetTargetedCardEffect;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.ExpansionSet;
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
 * Title: Desperate Counter (V)
 */
public class Card1274_005 extends AbstractImmediateEffect {
    public Card1274_005() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Desperate Counter", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Empire relied on the skill of its pilot corps to defend the Death Star reactor core from attack.");
        setGameText("Unless Imperial Propaganda on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 4 (ignore other modifiers); opponent may take any one card into hand from Reserve Deck; reshuffle. (Immune to Control.) OR Use 2 Force to deploy on table and stack a just-played Interrupt here.");
        addIcons(Icon.DEATH_STAR_II, Icon.GRABBER);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Reduce Force loss mode (deploys for free; no deploy cost is printed for it)
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                && !GameConditions.canSpot(game, self, Filters.Imperial_Propaganda)) {
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, true, 0, null, null, null, null, null, false, 0, Filters.none, null);
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
        // 'Grab' mode: use 2 Force to deploy and stack a just-played Interrupt here
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Interrupt)) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.canBeGrabbed(game, self, cardBeingPlayed)
                    && GameConditions.canUseForce(game, playerId, 2)) {
                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
                if (action != null) {
                    action.appendBeforeCost(
                            new UseForceEffect(action, playerId, 2));
                    action.setText("Deploy to 'grab' " + GameUtils.getFullName(cardBeingPlayed));
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
            // Reduce Force loss mode
            if (self.getWhileInPlayData() != null && self.getWhileInPlayData().getForceLossState() != null) {
                ForceLossState forceLossState = self.getWhileInPlayData().getForceLossState();
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setPerformingPlayer(playerId);
                action.setText("Reduce Force loss by 4");
                action.appendUsage(
                        new OncePerForceLossEffect(action));
                action.appendCost(
                        new SetWhileInPlayDataEffect(action, self, null));
                action.appendEffect(
                        new ReduceForceLossEffect(action, playerId, 4, 0, false, forceLossState));
                action.appendEffect(
                        new PlayoutDecisionEffect(action, opponent,
                                new YesNoDecision("Do you want to take any one card into hand from Reserve Deck?") {
                                    @Override
                                    protected void yes() {
                                        action.appendEffect(
                                                new TakeCardIntoHandFromReserveDeckEffect(action, opponent, Filters.any, true));
                                    }
                                }));
                actions.add(action);
            }

            // 'Grab' mode
            PhysicalCard cardToGrab = self.getTargetedCard(game.getGameState(), TargetId.IMMEDIATE_EFFECT_TARGET_1);
            if (cardToGrab != null && GameConditions.canBeGrabbed(game, self, cardToGrab)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setPerformingPlayer(playerId);
                action.setText("'Grab' " + GameUtils.getFullName(cardToGrab));
                action.setActionMsg("'Grab' " + GameUtils.getCardLink(cardToGrab));
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
