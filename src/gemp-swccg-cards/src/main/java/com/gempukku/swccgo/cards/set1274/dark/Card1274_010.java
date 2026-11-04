package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToRemoveJustLostCardFromLostPileResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Your Powers Are Weak, Old Man (V)
 */
public class Card1274_010 extends AbstractUsedOrLostInterrupt {
    public Card1274_010() {
        super(Side.DARK, 5, "Your Powers Are Weak, Old Man", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("You should not have come back.");
        setGameText("USED: Cancel an attempt to remove a just-lost character from owner's Lost Pile (unless it is being placed out of play or retrieved). LOST: If you just deployed a character to same site as a Jedi, take any one card from your Used Pile into hand; reshuffle.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // USED: Placing out of play and retrieval do not go through this "about to remove" step, so they are excluded.
        if (TriggerConditions.isAboutToRemoveJustLostCardFromLostPile(game, effectResult, Filters.and(Filters.character, Filters.canBeTargetedBy(self)))) {
            final AboutToRemoveJustLostCardFromLostPileResult result = (AboutToRemoveJustLostCardFromLostPileResult) effectResult;
            final PhysicalCard character = result.getCardToRemoveFromLostPile();
            if (result.getPreventableCardEffect() != null) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Cancel attempt to remove " + GameUtils.getFullName(character) + " from Lost Pile");
                action.allowResponses("Cancel attempt to remove " + GameUtils.getCardLink(character) + " from Lost Pile",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new PreventEffectOnCardEffect(action, result.getPreventableCardEffect(), character,
                                                "Cancels attempt to remove " + GameUtils.getCardLink(character) + " from Lost Pile"));
                            }
                        });
                actions.add(action);
            }
        }

        // LOST
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__YOUR_POWERS_ARE_WEAK_OLD_MAN_V__TAKE_CARD_FROM_USED_PILE;
        if (TriggerConditions.justDeployed(game, effectResult, playerId, Filters.character)
                && GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {
            PhysicalCard deployed = ((PlayCardResult) effectResult).getPlayedCard();
            if (GameConditions.canSpot(game, self, Filters.and(Filters.Jedi, Filters.atSameSite(deployed)))) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
                action.setText("Take a card from Used Pile into hand");
                action.allowResponses("Take any one card from Used Pile into hand",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
                            }
                        });
                actions.add(action);
            }
        }
        return actions;
    }
}
