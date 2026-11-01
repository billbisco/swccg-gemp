package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttackEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Interrupt
 * Subtype: Used
 * Title: Skull (V)
 */
public class Card1115_028 extends AbstractUsedInterrupt {
    public Card1115_028() {
        super(Side.LIGHT, 4, "Skull", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Bidlo Kwerve was involved in both the procurement and (posthumously) the loss of the rancor.");
        setGameText("During opponent's control phase, your pack attacks. Weapons may only target the lead creature. Pack may then move to an adjacent site. OR If a card was just 'eaten,' card's owner loses 2 Force. OR Use 3 Force to retrieve a creature.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                && !GameConditions.isDuringAttack(game)
                && !GameConditions.isDuringBattle(game)) {
            Collection<PhysicalCard> leads = new LinkedList<PhysicalCard>();
            for (PhysicalCard creature : Filters.filterActive(game, self, Filters.and(Filters.your(playerId), Filters.creature))) {
                int packSize = Filters.countActive(game, self, Filters.and(Filters.your(playerId), Filters.creature,
                        Filters.sameTitle(creature), Filters.atSameSite(creature)));
                if (packSize >= 2 && GameConditions.canSpot(game, self, Filters.nonCreatureCanBeAttackedByCreature(creature, false))) {
                    leads.add(creature);
                }
            }
            if (!leads.isEmpty()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Your pack attacks");
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose lead creature", Filters.in(leads)) {
                    @Override
                    protected void cardTargeted(final int leadGroupId, final PhysicalCard lead) {
                        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose card to attack",
                                Filters.nonCreatureCanBeAttackedByCreature(lead, false)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.allowResponses("Have " + GameUtils.getCardLink(lead) + " attack",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                PhysicalCard finalLead = action.getPrimaryTargetCard(leadGroupId);
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                action.appendEffect(new AttackEffect(action, finalTarget, finalLead));
                                            }
                                        });
                            }
                        });
                    }
                });
                actions.add(action);
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.SKULL__PLACE_CARD_OUT_OF_PLAY_FROM_LOST_PILE;
        if (GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 3)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve a creature");
            action.appendCost(new UseForceEffect(action, playerId, 3));
            action.allowResponses("Take a creature into hand from Lost Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.creature, false));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.justEatenBy(game, effectResult, Filters.any, Filters.any)) {
            final PhysicalCard eaten = ((EatenResult) effectResult).getCardEaten();
            final String owner = eaten.getOwner();
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Owner of eaten card loses 2 Force");
            action.allowResponses(GameUtils.getFullName(eaten) + "'s owner loses 2 Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new LoseForceEffect(action, owner, 2));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
