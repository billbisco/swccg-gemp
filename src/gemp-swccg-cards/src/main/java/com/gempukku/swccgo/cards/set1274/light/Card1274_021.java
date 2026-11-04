package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedForCancelingEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CancelCardOnTableResult;
import com.gempukku.swccgo.logic.timing.results.LostCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Interrupt
 * Subtype: Used
 * Title: Lucky Shot (V)
 */
public class Card1274_021 extends AbstractUsedInterrupt {
    public Card1274_021() {
        super(Side.LIGHT, 4, "Lucky Shot", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("AT-ATs are nearly impervious to infantry fire, but occasionally a blaster bolt hits between the plating. It's one shot in a million, but such odds are a familiar to the Rebellion.");
        setGameText("If opponent's Effect of any kind was just lost or canceled due to its own game text, place it out of play. OR Until end of turn, Do They Have A Code Clearance? may not modify forfeit. OR Cancel Close Call. OR Cancel Outflank (you may activate 1 Force).");
    }

    /**
     * Gets the opponent's Effect of any kind that was just lost (or canceled) from table by its own game text.
     */
    private PhysicalCard getEffectJustLostDueToOwnGameText(SwccgGame game, EffectResult effectResult, String opponent) {
        if (!TriggerConditions.justLost(game, effectResult, Filters.and(Filters.owner(opponent), Filters.Effect_of_any_Kind))) {
            return null;
        }
        PhysicalCard card = ((LostFromTableResult) effectResult).getCard();
        PhysicalCard source = null;
        if (effectResult instanceof LostCardFromTableResult) {
            source = ((LostCardFromTableResult) effectResult).getSourceCard();
        }
        else if (effectResult instanceof CancelCardOnTableResult) {
            source = ((CancelCardOnTableResult) effectResult).getSourceCard();
        }
        if (source != null && source.getCardId() == card.getCardId()) {
            return card;
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        final PhysicalCard effectLost = getEffectJustLostDueToOwnGameText(game, effectResult, opponent);
        if (effectLost != null) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place " + GameUtils.getFullName(effectLost) + " out of play");
            // Allow response(s)
            action.allowResponses("Place " + GameUtils.getCardLink(effectLost) + " out of play",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameUtils.getZoneFromZoneTop(effectLost.getZone()) == Zone.LOST_PILE) {
                                action.appendEffect(
                                        new PlaceCardOutOfPlayFromOffTableEffect(action, effectLost));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (GameConditions.canSpot(game, self, Filters.Do_They_Have_A_Code_Clearance)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Do They Have A Code Clearance? may not modify forfeit");
            // Allow response(s)
            action.allowResponses("Make Do They Have A Code Clearance? unable to modify forfeit until end of turn",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new ModifyGameTextModifier(self, Filters.Do_They_Have_A_Code_Clearance, ModifyGameTextType.DO_THEY_HAVE_A_CODE_CLEARANCE__DOESNT_MODIFY_FORFEIT),
                                            "Do They Have A Code Clearance? may not modify forfeit until end of turn"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (TriggerConditions.isPlayingCard(game, effect, Filters.Close_Call)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Outflank))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel " + GameUtils.getFullName(respondableEffect.getCard()));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                        @Override
                        protected void cardTargetedToBeCanceled(final PhysicalCard targetedCard) {
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelCardBeingPlayedEffect(action, respondableEffect));
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, playerId,
                                                            new YesNoDecision("Do you want to activate 1 Force?") {
                                                                @Override
                                                                protected void yes() {
                                                                    action.appendEffect(
                                                                            new ActivateForceEffect(action, playerId, 1));
                                                                }
                                                            }));
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
