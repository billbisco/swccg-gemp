package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardFromVoidOutOfPlayEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Either Way, You Win (V)
 */
public class Card1282_024 extends AbstractUsedOrLostInterrupt {
    public Card1282_024() {
        super(Side.LIGHT, 4, "Either Way, You Win", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Deal!'");
        setGameText("USED: Reveal the top four cards of your Reserve Deck and separate them into two piles of any size. Opponent places one pile in your hand and the other on top of your Used Pile in any order. OR Take Control into hand from Reserve Deck; reshuffle. LOST: Place an opponent's just-played Interrupt out of play.");
        addIcons(Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // USED: Reveal the top four cards of Reserve Deck and separate them into two piles
        if (GameConditions.hasReserveDeck(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Reveal top four cards of Reserve Deck");
            action.allowResponses("Reveal the top four cards of Reserve Deck and separate them into two piles",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            List<PhysicalCard> reserveDeck = game.getGameState().getReserveDeck(playerId);
                                            final List<PhysicalCard> topCards = new ArrayList<PhysicalCard>(reserveDeck.subList(0, Math.min(4, reserveDeck.size())));
                                            game.getGameState().sendMessage(playerId + " reveals " + GameUtils.getAppendedNames(topCards) + " from top of Reserve Deck");
                                            chooseFirstPile(action, game, playerId, topCards);
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        // USED: Take Control into hand from Reserve Deck
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__EITHER_WAY_YOU_WIN_V__TAKE_CONTROL;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take Control into hand from Reserve Deck");
            action.allowResponses("Take Control into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Control, true));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    private static void chooseFirstPile(final PlayInterruptAction action, final SwccgGame game, final String playerId, final List<PhysicalCard> topCards) {
        action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                        new ArbitraryCardsSelectionDecision("Choose cards for the first pile (the rest form the second pile)", topCards, 0, topCards.size()) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                final List<PhysicalCard> firstPile = getSelectedCardsByResponse(result);
                                final List<PhysicalCard> secondPile = new ArrayList<PhysicalCard>(topCards);
                                secondPile.removeAll(firstPile);
                                opponentChoosesPileForHand(action, game, playerId, firstPile, secondPile);
                            }
                        }));
    }

    private static String pileText(List<PhysicalCard> pile) {
        return pile.isEmpty() ? "no cards" : GameUtils.getAppendedNames(pile);
    }

    private static void opponentChoosesPileForHand(final PlayInterruptAction action, final SwccgGame game, final String playerId, final List<PhysicalCard> firstPile, final List<PhysicalCard> secondPile) {
        final String opponent = game.getOpponent(playerId);
        action.appendEffect(
                new PlayoutDecisionEffect(action, opponent,
                        new MultipleChoiceAwaitingDecision("Choose the pile to place in " + playerId + "'s hand (the other goes on top of Used Pile)",
                                new String[]{"First pile: " + pileText(firstPile), "Second pile: " + pileText(secondPile)}) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                List<PhysicalCard> handPile = index == 0 ? firstPile : secondPile;
                                List<PhysicalCard> usedPile = index == 0 ? secondPile : firstPile;
                                GameState gameState = game.getGameState();
                                for (PhysicalCard card : handPile) {
                                    if (GameUtils.getZoneFromZoneTop(card.getZone()) == Zone.RESERVE_DECK) {
                                        gameState.removeCardsFromZone(Collections.singleton(card));
                                        gameState.addCardToZone(card, Zone.HAND, playerId);
                                    }
                                }
                                if (!handPile.isEmpty()) {
                                    gameState.sendMessage(opponent + " places " + GameUtils.getAppendedNames(handPile) + " in " + playerId + "'s hand");
                                }
                                opponentPlacesOnUsedPile(action, game, playerId, new ArrayList<PhysicalCard>(usedPile));
                            }
                        }));
    }

    private static void opponentPlacesOnUsedPile(final PlayInterruptAction action, final SwccgGame game, final String playerId, final List<PhysicalCard> remaining) {
        if (remaining.isEmpty()) {
            return;
        }
        final String opponent = game.getOpponent(playerId);
        action.appendEffect(
                new PlayoutDecisionEffect(action, opponent,
                        new ArbitraryCardsSelectionDecision("Choose the next card to place on top of " + playerId + "'s Used Pile", remaining, 1, 1) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                PhysicalCard card = getSelectedCardsByResponse(result).get(0);
                                GameState gameState = game.getGameState();
                                if (GameUtils.getZoneFromZoneTop(card.getZone()) == Zone.RESERVE_DECK) {
                                    gameState.removeCardsFromZone(Collections.singleton(card));
                                    gameState.addCardToTopOfZone(card, Zone.USED_PILE, playerId);
                                    gameState.sendMessage(opponent + " places " + GameUtils.getCardLink(card) + " on top of " + playerId + "'s Used Pile");
                                }
                                remaining.remove(card);
                                opponentPlacesOnUsedPile(action, game, playerId, remaining);
                            }
                        }));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // LOST: Place an opponent's just-played Interrupt out of play
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.opponents(self), Filters.Interrupt))) {
            final PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.interruptCanBePlacedOutOfPlay(game, cardBeingPlayed)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Place " + GameUtils.getFullName(cardBeingPlayed) + " out of play");
                action.allowResponses("Place " + GameUtils.getCardLink(cardBeingPlayed) + " out of play",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new PlaceCardFromVoidOutOfPlayEffect(action, cardBeingPlayed));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
