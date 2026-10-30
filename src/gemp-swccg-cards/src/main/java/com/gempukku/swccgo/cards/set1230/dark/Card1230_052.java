package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeOneCardIntoHandFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Wounded Wookiee
 */
public class Card1230_052 extends AbstractUsedInterrupt {
    public Card1230_052() {
        super(Side.DARK, 2, Title.Wounded_Wookiee, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("When blaster fire from the barge's gun hit Chewie's skiff, his leg was injured by shrapnel. This setback distracted the Rebels, causing them to momentarily lose their advantage.");
        setGameText("If opponent's character was just hit during battle, add one destiny to your total power (two if character was a Wookiee); take cards drawn this way into hand. OR If you just lost X Force to a Force drain, draw up to X cards from Reserve Deck.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.justHit(game, effectResult, Filters.and(Filters.opponents(self), Filters.character))
                && GameConditions.isDuringBattle(game)
                && effectResult instanceof HitResult) {
            PhysicalCard hit = ((HitResult) effectResult).getCardHit();
            final int destinies = (hit != null && Filters.Wookiee.accepts(game, hit)) ? 2 : 1;
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add " + destinies + " destiny to total power");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId, destinies) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            if (totalDestiny != null) {
                                                action.appendEffect(
                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                new TotalPowerModifier(self, Filters.battleLocation, totalDestiny, playerId),
                                                                "Add destiny to total power"));
                                            }
                                            if (destinyCardDraws != null) {
                                                for (PhysicalCard drawn : destinyCardDraws) {
                                                    if (drawn != null) {
                                                        action.appendEffect(
                                                                new TakeOneCardIntoHandFromOffTableEffect(action, playerId, drawn, "Takes destiny card into hand") {
                                                                    @Override
                                                                    protected void afterCardTakenIntoHand() {
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        if (TriggerConditions.justLostForceFromForceDrainAt(game, effectResult, playerId, Filters.any, false)
                && effectResult instanceof LostForceResult) {
            final int x = ((LostForceResult) effectResult).getAmountOfForceLost();
            if (x > 0 && GameConditions.hasReserveDeck(game, playerId)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Draw up to " + x + " cards");
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new IntegerAwaitingDecision("Choose number of cards to draw", 1, x, x) {
                                                    @Override
                                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                                        action.appendEffect(
                                                                new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, result));
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
