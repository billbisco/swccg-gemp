package com.gempukku.swccgo.cards.set1210.dark;

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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardsEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Lost
 * Title: Imperial Reinforcements
 */
public class Card1210_061 extends AbstractLostInterrupt {
    public Card1210_061() {
        super(Side.DARK, 4, Title.Imperial_Reinforcements, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial stormtroopers deploy in 8-10 trooper squads. Reinforcements are typically held in reserve according to standard Imperial operating procedures.");
        setGameText("Once per game, if opponent has more cards with ability on table than you, draw destiny and retrieve up to that number of Imperials and/or Imperial starships (may take one retrieved card into hand). OR Cancel K'lor'slug.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Klorslug))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId retrieveActionId = GameTextActionId.LEGACY__IMPERIAL_REINFORCEMENTS_V__RETRIEVE_IMPERIALS;
        int yourAbilityCards = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.hasAbilityOrHasPermanentPilotWithAbility));
        int opponentAbilityCards = Filters.countActive(game, self, Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility));
        if (GameConditions.isOncePerGame(game, self, retrieveActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, retrieveActionId)
                && opponentAbilityCards > yourAbilityCards) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, retrieveActionId);
            action.setText("Draw destiny to retrieve Imperials");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Draw destiny and retrieve up to that number of Imperials and/or Imperial starships",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            if (totalDestiny != null && totalDestiny > 0) {
                                                final Filter retrieveFilter = Filters.or(Filters.Imperial, Filters.Imperial_starship);
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

        if (GameConditions.canTargetToCancel(game, self, Filters.title(Title.Klorslug))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Klorslug), Title.Klorslug);
            actions.add(action);
        }

        return actions;
    }
}
