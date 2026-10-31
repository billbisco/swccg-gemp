package com.gempukku.swccgo.cards.set1111.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardsIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Interrupt
 * Subtype: Used
 * Title: I Don't Need Their Scum, Either
 */
public class Card1111_007 extends AbstractUsedInterrupt {
    public Card1111_007() {
        super(Side.LIGHT, 5, "I Don't Need Their Scum, Either", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("Of all the scum and villainy Lando had dealt with (pirates, smugglers, con-artists, thieves, swindlers, politicians and Imperial lackeys), he hated bounty hunters the most.");
        setGameText("Take a 'grabber' into hand (or play a Defensive Shield) from under your Starting Effect. OR Cancel Counter Assault, Hidden Weapons, Hutt Smooch, I'd Just As Soon Kiss A Wookiee, Overload or one 'react' involving an [Independent] starship.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null) {
            if (GameConditions.hasStackedCards(game, startingEffect, Filters.grabber)) {
                final PhysicalCard stackedOn = startingEffect;
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Take grabber into hand from Starting Effect");
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new TakeStackedCardsIntoHandEffect(action, playerId, 1, 1, stackedOn, Filters.grabber));
                    }
                });
                actions.add(action);
            }

            Filter shieldFilter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
            if (GameConditions.hasStackedCards(game, startingEffect, shieldFilter)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Play a Defensive Shield");
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, startingEffect, shieldFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                action.appendEffect(new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter cancelFilter = Filters.or(Filters.Counter_Assault, Filters.Hidden_Weapons, Filters.title(Title.Hutt_Smooch),
                Filters.Id_Just_As_Soon_Kiss_A_Wookiee, Filters.Overload);
        if (TriggerConditions.isPlayingCard(game, effect, cancelFilter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        if (TriggerConditions.isDeployingAsReact(game, effect, Filters.and(Filters.starship, Icon.INDEPENDENT))
                || TriggerConditions.isReact(game, effect) && involvesIndependentStarshipReact(game, effect)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel 'react'");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new CancelReactEffect(action));
                        }
                    });
            actions.add(action);
        }

        return actions;
    }

    private boolean involvesIndependentStarshipReact(SwccgGame game, Effect effect) {
        if (game.getGameState().getMoveAsReactState() != null) {
            return Filters.canSpot(game.getGameState().getMoveAsReactState().getCardsParticipatingInReact(), game, Filters.and(Filters.starship, Icon.INDEPENDENT));
        }
        return false;
    }
}
