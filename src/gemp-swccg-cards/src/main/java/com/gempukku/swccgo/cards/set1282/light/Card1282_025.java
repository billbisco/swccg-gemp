package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyIKnowThatLaugh;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Subtype: Used
 * Title: You Will Take Me To Jabba Now (V)
 */
public class Card1282_025 extends AbstractUsedInterrupt {
    public Card1282_025() {
        super(Side.LIGHT, 5, "You Will Take Me To Jabba Now", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Et tu taka bu Jabba now.'");
        setGameText("When you play this Interrupt, may place a Rep stacked on your Objective in Used Pile. May deploy Seeking An Audience or an alien to Audience Chamber (for -1 Force) from Reserve Deck; reshuffle. OR Reveal up to 3 aliens stacked on I Know That Laugh or from hand (X = number of aliens revealed); activate X Force.");
    }

    private static List<PhysicalCard> aliensToReveal(SwccgGame game, PhysicalCard self, String playerId) {
        List<PhysicalCard> aliens = new ArrayList<PhysicalCard>(Filters.filter(game.getGameState().getHand(playerId), game, Filters.alien));
        for (PhysicalCard iKnowThatLaugh : Filters.filterActive(game, self, LegacyIKnowThatLaugh.yourIKnowThatLaugh(playerId))) {
            aliens.addAll(Filters.filter(game.getGameState().getStackedCards(iKnowThatLaugh), game, Filters.alien));
        }
        return aliens;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__YOU_WILL_TAKE_ME_TO_JABBA_NOW_V__DEPLOY_SEEKING_AN_AUDIENCE_OR_ALIEN;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            // Seeking An Audience
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy Seeking An Audience from Reserve Deck");
            action.allowResponses("Deploy Seeking An Audience from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            LegacyIKnowThatLaugh.appendMayPlaceRepInUsedPile(action, game, self, playerId);
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Seeking_An_Audience), true));
                        }
                    });
            actions.add(action);

            // An alien to Audience Chamber (for -1 Force)
            if (GameConditions.canSpotLocation(game, Filters.Audience_Chamber)) {
                final PlayInterruptAction action2 = new PlayInterruptAction(game, self, gameTextActionId);
                action2.setText("Deploy an alien to Audience Chamber from Reserve Deck");
                action2.allowResponses("Deploy an alien to Audience Chamber (for -1 Force) from Reserve Deck",
                        new RespondablePlayCardEffect(action2) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                LegacyIKnowThatLaugh.appendMayPlaceRepInUsedPile(action2, game, self, playerId);
                                action2.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action2, Filters.alien, Filters.Audience_Chamber, -1, true));
                            }
                        });
                actions.add(action2);
            }
        }

        // Reveal up to 3 aliens stacked on I Know That Laugh or from hand; activate X Force
        if (GameConditions.canActivateForce(game, playerId)
                && !aliensToReveal(game, self, playerId).isEmpty()) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reveal aliens stacked on I Know That Laugh or from hand");
            action.allowResponses("Reveal up to 3 aliens stacked on I Know That Laugh or from hand to activate that much Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            LegacyIKnowThatLaugh.appendMayPlaceRepInUsedPile(action, game, self, playerId);
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(final SwccgGame game) {
                                            List<PhysicalCard> aliens = aliensToReveal(game, self, playerId);
                                            if (aliens.isEmpty()) {
                                                return;
                                            }
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, playerId,
                                                            new ArbitraryCardsSelectionDecision("Choose up to 3 aliens to reveal", aliens, 1, Math.min(3, aliens.size())) {
                                                                @Override
                                                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                                                    List<PhysicalCard> revealed = getSelectedCardsByResponse(result);
                                                                    game.getGameState().sendMessage(playerId + " reveals " + GameUtils.getAppendedNames(revealed));
                                                                    for (PhysicalCard card : revealed) {
                                                                        game.getGameState().showCardOnScreen(card);
                                                                    }
                                                                    action.appendEffect(
                                                                            new ActivateForceEffect(action, playerId, revealed.size()));
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
