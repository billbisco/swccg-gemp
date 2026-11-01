package com.gempukku.swccgo.cards.set1114.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Our Only Hope
 */
public class Card1114_009 extends AbstractUsedOrLostInterrupt {
    public Card1114_009() {
        super(Side.LIGHT, 4, Title.Our_Only_Hope, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setGameText("If your [Death Star II] objective on table, USED: Deploy Luke from Reserve Deck; reshuffle. OR during your move phase, relocate Luke to a site opponent occupies. LOST: If your Rebel of ability > 2 just won a battle or duel, take any one card into hand from Reserve Deck; reshuffle (if Rebel is Luke, may also retrieve any one card).");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (!Filters.canSpot(game, self, Filters.and(Filters.your(self), Filters.Objective, Icon.DEATH_STAR_II))) {
            return actions;
        }
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, com.gempukku.swccgo.common.GameTextActionId.LEGACY__THERE_IS_NO_TRY_V__DEPLOY_LASER_GATE_OR_POLITICAL_EFFECT)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Deploy Luke from Reserve Deck");
            action.allowResponses("Deploy Luke from Reserve Deck", new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Luke, true));
                }
            });
            actions.add(action);
        }
        if (GameConditions.isDuringYourPhase(game, playerId, com.gempukku.swccgo.common.Phase.MOVE)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Luke))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Relocate Luke");
            action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose Luke", Filters.and(Filters.your(self), Filters.Luke)) {
                @Override
                protected void cardSelected(final PhysicalCard luke) {
                    action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose site opponent occupies", Filters.and(Filters.site, Filters.occupies(game.getOpponent(playerId)))) {
                        @Override
                        protected void cardSelected(PhysicalCard site) {
                            action.appendEffect(new RelocateBetweenLocationsEffect(action, luke, site));
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (!TriggerConditions.wonBattle(game, effectResult, Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2)))
                && !TriggerConditions.wonDuel(game, effectResult, Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2)))) {
            return null;
        }
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
        action.setText("Take a card into hand from Reserve Deck");
        action.allowResponses("Take any one card into hand from Reserve Deck", new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.any, true));
                if (Filters.canSpot(game, self, Filters.and(Filters.Luke, Filters.abilityMoreThan(2)))) {
                    action.appendEffect(new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.any, false));
                }
            }
        });
        return java.util.Collections.singletonList(action);
    }
}
