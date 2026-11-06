package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Virtual Block 3: Our Only Hope (V). */
public class Card1230_107 extends AbstractUsedOrLostInterrupt {
    public Card1230_107() {
        super(Side.LIGHT, 4, "Our Only Hope", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("'The Emperor knew, as I did, if Anakin were to have any offspring, they would be a threat to him.'");
        setGameText("If your [Death Star II] objective on table,USED: Deploy Luke from Reserve Deck; reshuffle. OR During your move phase, relocate Luke to a site opponent occupies. LOST: If your Rebel of ability > 2 just won a battle or duel, take any one card into hand from Reserve Deck; reshuffle (if Rebel is Luke, may also retrieve any one card).");
        addIcons(Icon.DEATH_STAR_II);
    }

    private boolean hasObjective(SwccgGame game, PhysicalCard self) {
        return GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Objective, Icon.DEATH_STAR_II));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        if (!hasObjective(game, self))
            return null;
        List<PlayInterruptAction> actions = new LinkedList<>();
        GameTextActionId deployId = GameTextActionId.LEGACY__OUR_ONLY_HOPE__DEPLOY_LUKE;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId, true, false)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, deployId, CardSubtype.USED);
            action.setText("Deploy Luke from Reserve Deck");
            action.allowResponses("Deploy Luke from Reserve Deck", new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Luke, true));
                }
            });
            actions.add(action);
        }

        final Filter sites = Filters.and(Filters.site, Filters.occupies(game.getOpponent(playerId)));
        Filter lukeFilter = Filters.and(Filters.Luke, Filters.canBeRelocatedToLocation(sites, 0));
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.canTarget(game, self, lukeFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Relocate Luke");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Luke", lukeFilter) {
                @Override
                protected void cardTargeted(final int lukeGroup, final PhysicalCard luke) {
                    action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose site opponent occupies",
                            Filters.and(sites, Filters.locationCanBeRelocatedTo(luke, 0))) {
                        @Override
                        protected void cardSelected(final PhysicalCard site) {
                            action.addAnimationGroup(luke, site);
                            action.appendCost(new PayRelocateBetweenLocationsCostEffect(action, playerId, luke, site, 0));
                            action.allowResponses("Relocate " + GameUtils.getCardLink(luke) + " to " + GameUtils.getCardLink(site),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalLuke = targetingAction.getPrimaryTargetCard(lukeGroup);
                                            action.appendEffect(new RelocateBetweenLocationsEffect(action, finalLuke, site));
                                        }
                                    });
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game,
                                                                      EffectResult effectResult, final PhysicalCard self) {
        Filter winner = Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2));
        GameTextActionId takeId = GameTextActionId.LEGACY__OUR_ONLY_HOPE__TAKE_ANY_CARD;
        if (!hasObjective(game, self)
                || !(TriggerConditions.wonBattle(game, effectResult, winner) || TriggerConditions.wonDuel(game, effectResult, winner))
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId))
            return null;
        final boolean lukeWon = TriggerConditions.wonBattle(game, effectResult, Filters.and(winner, Filters.Luke))
                || TriggerConditions.wonDuel(game, effectResult, Filters.and(winner, Filters.Luke));
        final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId, CardSubtype.LOST);
        action.setText("Take a card into hand from Reserve Deck");
        action.allowResponses("Take any one card into hand from Reserve Deck", new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.any, true));
                if (lukeWon)
                    action.appendEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Retrieve any one card?") {
                        @Override
                        protected void yes() {
                            action.appendEffect(new RetrieveCardEffect(action, playerId));
                        }
                    }));
            }
        });
        return Collections.singletonList(action);
    }
}
