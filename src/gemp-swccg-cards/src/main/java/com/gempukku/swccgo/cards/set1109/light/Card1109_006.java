package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.cards.effects.PeekAtAndReorderTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Covert Landing
 */
public class Card1109_006 extends AbstractUsedOrLostInterrupt {
    public Card1109_006() {
        super(Side.LIGHT, 4, Title.Covert_Landing, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("The stolen code allowed the Rebels to land their strike team on the Endor surface undetected - or so they thought.");
        setGameText("USED: During opponent's move phase, if Bunker 'blown away,' peek at top four cards of a player's Reserve Deck; replace in any order. LOST: During opponent's move phase, deploy a Rebel scout of ability < 5 from Reserve Deck to an Endor site opponent does not occupy; reshuffle.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.MOVE)
                && GameConditions.isBlownAway(game, Filters.Bunker)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Peek at top four cards of Reserve Deck");
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            action.allowResponses("Peek at top four cards of " + cardPileOwner + "'s Reserve Deck",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new PeekAtAndReorderTopCardsOfReserveDeckEffect(action, cardPileOwner, 4));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.OVS__COVERT_LANDING__DEPLOY_SCOUT;
        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.MOVE)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Deploy Rebel scout from Reserve Deck");
            action.allowResponses("Deploy a Rebel scout of ability < 5 to an Endor site opponent does not occupy from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(action,
                                            Filters.and(Filters.Rebel, Filters.scout, Filters.abilityLessThan(5)),
                                            Filters.and(Filters.Endor_site, Filters.not(Filters.occupies(opponent))),
                                            true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
