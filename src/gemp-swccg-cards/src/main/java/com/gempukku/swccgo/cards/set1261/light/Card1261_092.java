package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Antilles Maneuver & Rebel Reinforcements, final Light master page 32. */
public class Card1261_092 extends AbstractUsedOrLostInterrupt {
    private static final GameTextActionId DEPLOY = GameTextActionId.ANTILLES_MANEUVER__DOWNLOAD_REBEL_ABOARD_YOUR_STARSHIP;
    private static final GameTextActionId RETRIEVE = GameTextActionId.LEGACY__REBEL_REINFORCEMENTS_V__RETRIEVE_REBELS;
    private static final Filter REBEL_CREW = Filters.and(Filters.Rebel, Filters.character, Filters.abilityLessThan(3), Filters.not(Filters.admiral));
    private static final Filter REBEL_RETRIEVAL = Filters.or(Filters.and(Filters.Rebel, Filters.character), Filters.Rebel_starship);

    public Card1261_092() {
        super(Side.LIGHT, 5, "Antilles Maneuver & Rebel Reinforcements", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setTitleComponents(new TitleComponent("Antilles Maneuver", Uniqueness.UNIQUE), new TitleComponent("Rebel Reinforcements", Uniqueness.UNIQUE));
        setGameText("USED: Cancel Lateral Damage (Immune to Sense.) OR During opponent's deploy phase, deploy a Rebel of ability < 3 (except an admiral) aboard your starship from Reserve Deck; reshuffle. LOST: Once per game, if opponent has more cards with ability on table than you, draw destiny and retrieve up to that number of Rebels and/or Rebel starships (may take one retrieved card into hand).");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        if (!TriggerConditions.isPlayingCard(game, effect, Filters.Lateral_Damage)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            return null;
        }
        PlayInterruptAction action = cancelAction(game, self);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        if (GameConditions.canTargetToCancel(game, self, Filters.Lateral_Damage)) {
            PlayInterruptAction action = cancelAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Lateral_Damage, Title.Lateral_Damage);
            actions.add(action);
        }
        Filter yourShip = Filters.and(Filters.your(self), Filters.starship);
        if (GameConditions.isDuringOpponentsPhase(game, self, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, Filters.or(yourShip, Filters.siteOfStarshipOrVehicle(yourShip)))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, DEPLOY, true, false)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, DEPLOY, CardSubtype.USED);
            action.setText("Deploy a Rebel from Reserve Deck aboard your starship");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new DeployCardAboardFromReserveDeckEffect(action, REBEL_CREW, yourShip, true));
                }
            });
            actions.add(action);
        }

        int yours = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.hasAbilityOrHasPermanentPilotWithAbility));
        int theirs = Filters.countActive(game, self, Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility));
        if (theirs > yours && GameConditions.isOncePerGame(game, self, RETRIEVE)
                && GameConditions.canSearchLostPile(game, playerId, self, RETRIEVE)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, RETRIEVE, CardSubtype.LOST);
            action.setText("Draw destiny to retrieve Rebels");
            action.appendUsage(new OncePerGameEffect(action));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards, List<Float> values, Float total) {
                            if (total != null && total > 0) {
                                action.appendEffect(new RetrieveCardsEffect(action, playerId, total, true, REBEL_RETRIEVAL) {
                                    @Override
                                    protected void cardsRetrieved(Collection<PhysicalCard> retrieved) {
                                        if (retrieved != null && !retrieved.isEmpty()) {
                                            offerHandChoice(action, playerId, List.copyOf(retrieved));
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }

    private PlayInterruptAction cancelAction(SwccgGame game, PhysicalCard self) {
        PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
        action.setImmuneTo(Title.Sense);
        return action;
    }

    private void offerHandChoice(PlayInterruptAction action, String playerId, List<PhysicalCard> retrieved) {
        action.appendEffect(new ChooseCardsFromUsedPileEffect(action, playerId, 0, 1, Filters.in(retrieved)) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                return "Choose a retrieved card to take into hand";
            }

            @Override
            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selected) {
                for (PhysicalCard card : selected) {
                    action.appendEffect(new TakeCardIntoHandFromUsedPileEffect(action, playerId, card, true));
                }
            }
        });
    }
}
