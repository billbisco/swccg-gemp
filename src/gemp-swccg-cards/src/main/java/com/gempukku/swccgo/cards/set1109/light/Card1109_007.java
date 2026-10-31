package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Interrupt
 * Subtype: Used
 * Title: Endor Celebration
 */
public class Card1109_007 extends AbstractUsedInterrupt {
    public Card1109_007() {
        super(Side.LIGHT, 5, Title.Endor_Celebration, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Rebel presence on Endor meant that the Ewoks would be able to live free from the Empire's tyranny.");
        setGameText("Cancel an Effect with 'occupation' in title. (Immune to Sense.) OR During your draw phase, if you have a Rebel scout alone at Back Door, draw destiny. Activate or retrieve 5 Force and place this Interrupt on bottom of Lost Pile if destiny < the number of your scouts on table.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.canTargetToCancel(game, self, Filters.and(Filters.Effect, Filters.titleContains("occupation")))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.and(Filters.Effect, Filters.titleContains("occupation")), "Effect with occupation in title");
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }

        if (GameConditions.isDuringYourPhase(game, self, Phase.DRAW)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Rebel, Filters.scout, Filters.alone, Filters.at(Filters.Back_Door)))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Draw destiny to activate or retrieve");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            GameState gameState = game.getGameState();
                                            int scouts = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.scout));
                                            if (totalDestiny == null) {
                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                return;
                                            }
                                            gameState.sendMessage("Destiny: " + totalDestiny + "  Scouts: " + scouts);
                                            if (totalDestiny < scouts) {
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, playerId,
                                                                new MultipleChoiceAwaitingDecision("Choose", new String[]{"Activate 5 Force", "Retrieve 5 Force"}) {
                                                                    @Override
                                                                    protected void validDecisionMade(int index, String result) {
                                                                        if (index == 0) {
                                                                            action.appendEffect(new ActivateForceEffect(action, playerId, 5));
                                                                        }
                                                                        else {
                                                                            action.appendEffect(new RetrieveForceEffect(action, playerId, 5));
                                                                        }
                                                                    }
                                                                }));
                                                action.appendEffect(
                                                        new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                            }
                                            else {
                                                gameState.sendMessage("Result: Failed");
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Effect, Filters.titleContains("occupation")))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            return Collections.singletonList(action);
        }
        return null;
    }
}
