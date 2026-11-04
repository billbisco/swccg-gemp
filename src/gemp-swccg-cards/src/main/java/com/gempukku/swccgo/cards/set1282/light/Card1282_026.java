package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyIKnowThatLaugh;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalWeaponDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToCompleteDrawingDestinyResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Subtype: Used
 * Title: Under Jabba's Protection
 */
public class Card1282_026 extends AbstractUsedInterrupt {
    public Card1282_026() {
        super(Side.LIGHT, 4, "Under Jabba's Protection", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setGameText("When you play this Interrupt, may place a Rep on your Objective in Used Pile. Lose X cards stacked on I Know That Laugh to reduce a weapon or battle destiny total by X. OR Use 1 Force to take a card stacked on I Know That Laugh into hand (or stack top card of Lost Pile on it).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final PhysicalCard iKnowThatLaugh = Filters.findFirstActive(game, self, LegacyIKnowThatLaugh.yourIKnowThatLaugh(playerId));
        if (iKnowThatLaugh == null
                || !GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            return actions;
        }

        // Use 1 Force to take a card stacked on I Know That Laugh into hand
        if (GameConditions.hasStackedCards(game, iKnowThatLaugh)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take a card stacked on I Know That Laugh into hand");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Take a card stacked on I Know That Laugh into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            LegacyIKnowThatLaugh.appendMayPlaceRepInUsedPile(action, game, self, playerId);
                            action.appendEffect(
                                    new TakeStackedCardIntoHandEffect(action, playerId, iKnowThatLaugh));
                        }
                    });
            actions.add(action);
        }

        // (or stack top card of Lost Pile on it)
        if (GameConditions.hasLostPile(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Stack top card of Lost Pile on I Know That Laugh");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Stack top card of Lost Pile on I Know That Laugh",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            LegacyIKnowThatLaugh.appendMayPlaceRepInUsedPile(action, game, self, playerId);
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            List<PhysicalCard> lostPile = game.getGameState().getLostPile(playerId);
                                            if (!lostPile.isEmpty()) {
                                                PhysicalCard topCard = lostPile.get(0);
                                                action.appendEffect(
                                                        new StackOneCardFromLostPileEffect(action, topCard, iKnowThatLaugh, !LegacyIKnowThatLaugh.stacksFaceUp(topCard), false, false));
                                            }
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);
        final PhysicalCard iKnowThatLaugh = Filters.findFirstActive(game, self, LegacyIKnowThatLaugh.yourIKnowThatLaugh(playerId));
        if (iKnowThatLaugh == null || !GameConditions.hasStackedCards(game, iKnowThatLaugh)) {
            return actions;
        }
        // Opponent's weapon or battle destiny draw is about to complete: the point where its total can be modified
        boolean aboutToCompleteOpponentsDraw = effectResult.getType() == EffectResult.Type.DRAWING_DESTINY_COMPLETE
                && opponent.equals(effectResult.getPerformingPlayerId());
        final boolean weaponDestiny = aboutToCompleteOpponentsDraw
                && ((AboutToCompleteDrawingDestinyResult) effectResult).getDestinyType() == DestinyType.WEAPON_DESTINY;
        final boolean battleDestiny = aboutToCompleteOpponentsDraw
                && ((AboutToCompleteDrawingDestinyResult) effectResult).getDestinyType() == DestinyType.BATTLE_DESTINY;

        if (weaponDestiny || battleDestiny) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText(weaponDestiny ? "Reduce weapon destiny total" : "Reduce battle destiny total");
            action.allowResponses("Lose X cards stacked on I Know That Laugh to reduce " + (weaponDestiny ? "weapon" : "battle") + " destiny total by X",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            LegacyIKnowThatLaugh.appendMayPlaceRepInUsedPile(action, game, self, playerId);
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            int stacked = game.getGameState().getStackedCards(iKnowThatLaugh).size();
                                            if (stacked < 1) {
                                                return;
                                            }
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, playerId,
                                                            new IntegerAwaitingDecision("Choose number of cards stacked on I Know That Laugh to lose", 1, stacked, stacked) {
                                                                @Override
                                                                public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                                    action.appendEffect(
                                                                            new PutStackedCardsInLostPileEffect(action, playerId, result, result, iKnowThatLaugh));
                                                                    if (weaponDestiny) {
                                                                        action.appendEffect(
                                                                                new ModifyTotalWeaponDestinyEffect(action, -result));
                                                                    }
                                                                    else {
                                                                        action.appendEffect(
                                                                                new ModifyTotalBattleDestinyEffect(action, opponent, -result));
                                                                    }
                                                                }
                                                            }));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}
