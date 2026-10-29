package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Walker Sighting
 */
public class Card1220_158 extends AbstractUsedInterrupt {
    public Card1220_158() {
        super(Side.LIGHT, 3, Title.Walker_Sighting, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Echo station, 3TA. We have spotted Imperial walkers.' A Rebel tactic is to put as much ground as possible between walkers and Rebel troops, allowing time to prepare a defense.");
        setGameText("While an AT-AT or AT-ST on table, destiny +3. Take Concussion Grenade, Logistical Delay, or Panic into hand from Reserve Deck; reshuffle. (Immune to Sense.) OR If opponent's AT-AT or AT-ST on table, peek at the top card of their Reserve Deck; may use 2 Force to make it lost.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, new OnTableCondition(self, Filters.or(Filters.AT_AT, Filters.AT_ST)), 3));
        return modifiers;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId takeId = GameTextActionId.LEGACY__WALKER_SIGHTING_V__TAKE_CARD;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId);
            action.setText("Take card into hand from Reserve Deck");
            action.setImmuneTo(Title.Sense);
            action.allowResponses("Take Concussion Grenade, Logistical Delay, or Panic into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Concussion_Grenade, Filters.Logistical_Delay, Filters.Panic), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.or(Filters.AT_AT, Filters.AT_ST)))
                && GameConditions.hasReserveDeck(game, opponent)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at top of opponent's Reserve Deck");
            action.allowResponses("Peek at the top card of opponent's Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, opponent) {
                                        @Override
                                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, playerId,
                                                            new YesNoDecision("Do you want to use 2 Force to make " + GameUtils.getCardLink(peekedAtCard) + " lost?") {
                                                                @Override
                                                                protected void yes() {
                                                                    if (GameConditions.canUseForce(game, playerId, 2)) {
                                                                        action.appendEffect(
                                                                                new UseForceEffect(action, playerId, 2));
                                                                        action.appendEffect(
                                                                                new LoseCardFromTopOfReserveDeckEffect(action, opponent, peekedAtCard));
                                                                    }
                                                                }
                                                                @Override
                                                                protected void no() {
                                                                    game.getGameState().sendMessage(playerId + " chooses to not make top card of opponent's Reserve Deck lost");
                                                                }
                                                            }
                                                    ));
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
