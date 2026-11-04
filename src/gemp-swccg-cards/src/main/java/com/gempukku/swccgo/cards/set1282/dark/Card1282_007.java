package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.LegacyRevealTopCardsSplitIntoHandAndUsedPileEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardFromVoidOutOfPlayEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: You Swindled Me! (V)
 */
public class Card1282_007 extends AbstractUsedOrLostInterrupt {
    public Card1282_007() {
        super(Side.DARK, 4, "You Swindled Me!", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Needless to say, Watto was not happy about his loss.");
        setGameText("USED: Reveal the top four cards of your Reserve Deck and separate them into two piles of any size. Opponent places one pile in your hand and the other on top of your Used Pile in any order. OR Take Alter into hand from Reserve Deck; reshuffle. LOST: Place an opponent's just-played Interrupt out of play.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Reveal top four cards of Reserve Deck");
            // Allow response(s)
            action.allowResponses("Reveal the top four cards of Reserve Deck and separate them into two piles",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LegacyRevealTopCardsSplitIntoHandAndUsedPileEffect(action, playerId, 4));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__YOU_SWINDLED_ME_V__TAKE_ALTER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take Alter into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Alter, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.opponents(self), Filters.Interrupt))) {
            final RespondablePlayingCardEffect playingCardEffect = (RespondablePlayingCardEffect) effect;
            final PhysicalCard cardBeingPlayed = playingCardEffect.getCard();
            if (GameConditions.interruptCanBePlacedOutOfPlay(game, cardBeingPlayed)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Place " + GameUtils.getFullName(cardBeingPlayed) + " out of play");
                // Allow response(s)
                action.allowResponses("Place " + GameUtils.getCardLink(cardBeingPlayed) + " out of play",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s): the Interrupt goes out of play and does not resolve
                                action.appendEffect(
                                        new PlaceCardFromVoidOutOfPlayEffect(action, cardBeingPlayed));
                                action.appendEffect(
                                        new CancelCardBeingPlayedEffect(action, playingCardEffect));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
