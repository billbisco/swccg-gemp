package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: The Signal
 */
public class Card1230_110 extends AbstractUsedOrStartingInterrupt {
    public Card1230_110() {
        super(Side.LIGHT, 5, "The Signal", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("With a quick motion of his hand, Luke signaled Artoo and dramatically changed the situation.");
        setGameText("USED: Use 1 Force to take a character weapon into hand from Reserve Deck; reshuffle. STARTING: Take into hand and/or deploy from Reserve Deck up to three Effects of any kind with 'Jedi' in lore or game text. Place this Interrupt in Reserve Deck.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THE_SIGNAL_V__UPLOAD_CHARACTER_WEAPON;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take character weapon into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Take a character weapon into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.character_weapon, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Take Effects into hand or deploy from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Take into hand and/or deploy from Reserve Deck up to three Effects of any kind with 'Jedi' in lore or game text",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        Filter jediInLoreOrGameText = Filters.or(Filters.loreContains("Jedi"), Filters.gameTextContains("Jedi"));
                        // Perform result(s)
                        action.appendEffect(
                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Effect_of_any_Kind, jediInLoreOrGameText), false) {
                                    @Override
                                    protected void cardTakenIntoHand(final PhysicalCard card) {
                                        if (Filters.deployable(self, null, true, 0).accepts(game, card)) {
                                            action.insertEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Deploy " + GameUtils.getCardLink(card) + "?") {
                                                @Override
                                                protected void yes() {
                                                    action.insertEffect(new DeployCardFromHandEffect(action, card, true));
                                                }
                                            }));
                                        }
                                    }
                                });
                        action.appendEffect(
                                new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 0, 1, Filters.and(Filters.Effect_of_any_Kind, jediInLoreOrGameText), false) {
                                    @Override
                                    protected void cardsTakenIntoHand(Collection<PhysicalCard> cards) {
                                        if (!cards.isEmpty()) {
                                            final PhysicalCard card = cards.iterator().next();
                                            if (Filters.deployable(self, null, true, 0).accepts(game, card)) {
                                                action.insertEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Deploy " + GameUtils.getCardLink(card) + "?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.insertEffect(new DeployCardFromHandEffect(action, card, true));
                                                    }
                                                }));
                                            }
                                        }
                                    }
                                });
                        action.appendEffect(
                                new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 0, 1, Filters.and(Filters.Effect_of_any_Kind, jediInLoreOrGameText), false) {
                                    @Override
                                    protected void cardsTakenIntoHand(Collection<PhysicalCard> cards) {
                                        if (!cards.isEmpty()) {
                                            final PhysicalCard card = cards.iterator().next();
                                            if (Filters.deployable(self, null, true, 0).accepts(game, card)) {
                                                action.insertEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Deploy " + GameUtils.getCardLink(card) + "?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.insertEffect(new DeployCardFromHandEffect(action, card, true));
                                                    }
                                                }));
                                            }
                                        }
                                    }
                                });

                        action.appendEffect(
                                new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}
