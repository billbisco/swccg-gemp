package com.gempukku.swccgo.cards.set1107.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtAndReorderTopCardsOForcePileEffect;
import com.gempukku.swccgo.cards.effects.PeekAtAndReorderTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.PeekAtAndReorderTopCardsOfUsedPileEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromTopOfForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInCardPileResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Frustration (V)
 */
public class Card1107_015 extends AbstractUsedOrLostInterrupt {
    public Card1107_015() {
        super(Side.DARK, 3, Title.Frustration, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Rrraaaarrr!'");
        setGameText("USED: If you just peeked at cards in your Life Force, peek at and reorder the top 3 cards of that deck or pile (may take one into hand). LOST: If Xizor and Guri are in a battle together, your total battle destiny is +5 and may retrieve 1 Force.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.justLookedAtCardsInOwnCardPile(game, effectResult, playerId)) {
            final Zone cardPile = ((LookedAtCardsInCardPileResult) effectResult).getCardPile();
            if ((cardPile == Zone.RESERVE_DECK || cardPile == Zone.FORCE_PILE || cardPile == Zone.USED_PILE)
                    && game.getGameState().getCardPileSize(playerId, cardPile) > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Peek at and reorder top 3 of " + cardPile.getHumanReadable());
                action.allowResponses("Peek at and reorder the top 3 cards of " + cardPile.getHumanReadable(),
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                if (cardPile == Zone.RESERVE_DECK) {
                                    action.appendEffect(new PeekAtAndReorderTopCardsOfReserveDeckEffect(action, playerId, 3));
                                }
                                else if (cardPile == Zone.FORCE_PILE) {
                                    action.appendEffect(new PeekAtAndReorderTopCardsOForcePileEffect(action, playerId, 3));
                                }
                                else {
                                    action.appendEffect(new PeekAtAndReorderTopCardsOfUsedPileEffect(action, playerId, 3));
                                }
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Take one of those cards into hand?") {
                                                    @Override
                                                    protected void yes() {
                                                        if (cardPile == Zone.RESERVE_DECK) {
                                                            action.appendEffect(new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                                                        }
                                                        else if (cardPile == Zone.FORCE_PILE) {
                                                            action.appendEffect(new TakeCardIntoHandFromTopOfForcePileEffect(action, playerId));
                                                        }
                                                        else {
                                                            action.appendEffect(new DrawCardIntoHandFromUsedPileEffect(action, playerId));
                                                        }
                                                    }
                                                    @Override
                                                    protected void no() {
                                                        game.getGameState().sendMessage(playerId + " chooses to not take a card into hand");
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

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.isDuringBattle(game)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Xizor)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Guri)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add 5 to total battle destiny");
            action.allowResponses("Make your total battle destiny +5 and retrieve 1 Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new TotalBattleDestinyModifier(self, 5, playerId, true),
                                            "Total battle destiny is +5"));
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new YesNoDecision("Retrieve 1 Force?") {
                                                @Override
                                                protected void yes() {
                                                    action.appendEffect(new RetrieveForceEffect(action, playerId, 1));
                                                }
                                                @Override
                                                protected void no() {
                                                    game.getGameState().sendMessage(playerId + " chooses to not retrieve Force");
                                                }
                                            }
                                    )
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
