package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfCardPileEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeOneCardIntoHandFromOffTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Interrupt
 * Subtype: Used
 * Title: Keep Your Eyes Open
 */
public class Card1106_013 extends AbstractUsedInterrupt {
    public Card1106_013() {
        super(Side.LIGHT, 5, Title.Keep_Your_Eyes_Open, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Look, don't worry. Everything's gonna be fine. Trust me.'");
        setGameText("Peek at the top card of your Force Pile or Reserve Deck; may take that card into hand.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (!GameConditions.hasForcePile(game, playerId) && !GameConditions.hasReserveDeck(game, playerId)) {
            return null;
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Peek at top card of Force Pile or Reserve Deck");
        action.appendTargeting(
                new ChooseExistingCardPileEffect(action, playerId, playerId, Filters.or(Zone.FORCE_PILE, Zone.RESERVE_DECK)) {
                    @Override
                    protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                        action.allowResponses("Peek at the top card of " + cardPile.getHumanReadable(),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        action.appendEffect(
                                                new PeekAtTopCardOfCardPileEffect(action, playerId, cardPileOwner, cardPile) {
                                                    @Override
                                                    protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
                                                        if (peekedAtCards.isEmpty()) {
                                                            return;
                                                        }
                                                        final PhysicalCard card = peekedAtCards.get(0);
                                                        final String intoHand = "Take card into hand";
                                                        final String leave = "Leave card where it is";
                                                        action.appendEffect(
                                                                new PlayoutDecisionEffect(action, playerId, new MultipleChoiceAwaitingDecision("Choose what to do with the card", new String[]{intoHand, leave}) {
                                                                    @Override
                                                                    protected void validDecisionMade(int index, String result) {
                                                                        if (intoHand.equals(result)) {
                                                                            action.appendEffect(
                                                                                    new TakeOneCardIntoHandFromOffTableEffect(action, playerId, card, "Takes card into hand") {
                                                                                        @Override
                                                                                        protected void afterCardTakenIntoHand() {
                                                                                        }
                                                                                    });
                                                                        }
                                                                        else {
                                                                            action.appendEffect(
                                                                                    new SendMessageEffect(action, playerId + " leaves the card where it is"));
                                                                        }
                                                                    }
                                                                }));
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
        return Collections.singletonList(action);
    }
}
