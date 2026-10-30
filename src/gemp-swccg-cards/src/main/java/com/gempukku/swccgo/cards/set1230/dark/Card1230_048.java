package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Sacrifice
 */
public class Card1230_048 extends AbstractUsedInterrupt {
    public Card1230_048() {
        super(Side.DARK, 4, Title.Sacrifice, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba's minions could be expected to be sacrificed to save the Hutt, to destroy one of the Hutts enemies or to provide the Hutt and his minions with a good laugh.");
        setGameText("If opponent just initiated a battle, target your non-immune to Alter Effect on table. Draw destiny. Place target on Used Pile and activate up to X Force if destiny < X, where X = target's destiny number.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        Filter targetFilter = Filters.and(Filters.your(self), Filters.Effect, Filters.not(Filters.immune_to_Alter));
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.canTarget(game, self, targetFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target Effect and draw destiny");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Effect", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            action.appendEffect(
                                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
                                                            final int x = (int) game.getModifiersQuerying().getDestiny(game.getGameState(), finalTarget);
                                                            if (totalDestiny != null && totalDestiny < x && x > 0 && GameConditions.canActivateForce(game, playerId)) {
                                                                action.appendEffect(
                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                new IntegerAwaitingDecision("Choose Force to activate", 1, x, x) {
                                                                                    @Override
                                                                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                                                                        action.appendEffect(
                                                                                                new ActivateForceEffect(action, playerId, result));
                                                                                    }
                                                                                }
                                                                        )
                                                                );
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                            );
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
