package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardsEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Lost
 * Title: Rebel Reinforcements
 */
public class Card1210_160 extends AbstractLostInterrupt {
    public Card1210_160() {
        super(Side.LIGHT, 4, Title.Rebel_Reinforcements, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Rebels rely on hidden 'cells' of undercover operatives. Striking from hidden bases, troops or starfighters can arrive in a battle zone at any time.");
        setGameText("Once per game, if opponent has more cards with ability on table than you do, draw destiny and retrieve up to that number of Rebel characters and/or Rebel starships (may take one retrieved card into hand).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId retrieveActionId = GameTextActionId.LEGACY__REBEL_REINFORCEMENTS_V__RETRIEVE_REBELS;
        int yourAbilityCards = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.hasAbilityOrHasPermanentPilotWithAbility));
        int opponentAbilityCards = Filters.countActive(game, self, Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility));
        if (GameConditions.isOncePerGame(game, self, retrieveActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, retrieveActionId)
                && opponentAbilityCards > yourAbilityCards) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, retrieveActionId);
            action.setText("Draw destiny to retrieve Rebels");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Draw destiny and retrieve up to that number of Rebel characters and/or Rebel starships",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            if (totalDestiny != null && totalDestiny > 0) {
                                                final Filter retrieveFilter = Filters.or(Filters.and(Filters.Rebel, Filters.character), Filters.Rebel_starship);
                                                action.appendEffect(
                                                        new RetrieveCardsEffect(action, playerId, totalDestiny, true, retrieveFilter) {
                                                            @Override
                                                            protected void cardsRetrieved(Collection<PhysicalCard> retrievedCards) {
                                                                if (retrievedCards == null || retrievedCards.isEmpty()) {
                                                                    return;
                                                                }
                                                                final Collection<PhysicalCard> retrieved = new LinkedList<PhysicalCard>(retrievedCards);
                                                                action.appendEffect(
                                                                        new ChooseCardsFromUsedPileEffect(action, playerId, 0, 1, Filters.in(retrieved)) {
                                                                            @Override
                                                                            public String getChoiceText(int numCardsToChoose) {
                                                                                return "Choose a retrieved card to take into hand";
                                                                            }

                                                                            @Override
                                                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                                                for (PhysicalCard card : selectedCards) {
                                                                                    action.appendEffect(
                                                                                            new TakeCardIntoHandFromUsedPileEffect(action, playerId, card, true));
                                                                                }
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
