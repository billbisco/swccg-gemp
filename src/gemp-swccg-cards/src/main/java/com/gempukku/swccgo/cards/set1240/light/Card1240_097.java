package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.effects.AttackEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Interrupt
 * Subtype: Used
 * Title: Skull (V)
 */
public class Card1240_097 extends AbstractUsedInterrupt {
    public Card1240_097() {
        super(Side.LIGHT, 4, "Skull", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Bidlo Kwerve was involved in both the procurement and (posthumously) the loss of the rancor.");
        setGameText("During opponent's control phase, your pack attacks. Weapons may only target the lead creature. Pack may then move to an adjacent site. OR If a card was just 'eaten,' card's owner loses 2 Force. OR Use 3 Force to retrieve a creature.");
        addIcons(Icon.JABBAS_PALACE);
    }

    private Filter packFilter(String playerId, PhysicalCard lead) {
        return Filters.and(Filters.your(playerId), Filters.creature, Filters.sameTitle(lead), Filters.atSameSite(lead));
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
                                                final PhysicalCard finalLead = action.getPrimaryTargetCard(leadGroupId);
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                final Collection<PhysicalCard> packMembers = Filters.filterActive(game, self, packFilter(playerId, finalLead));
                                                final Filter pack = Filters.in(packMembers);
                                                // Weapons may only target the lead creature.
                                                final int leadCardId = finalLead.getCardId();
                                                Condition duringThisPacksAttack = new Condition() {
                                                    @Override
                                                    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                                                        AttackState attackState = gameState.getAttackState();
                                                        if (attackState == null) {
                                                            return false;
                                                        }
                                                        for (PhysicalCard attacker : attackState.getCardsAttacking()) {
                                                            if (attacker.getCardId() == leadCardId) {
                                                                return true;
                                                            }
                                                        }
                                                        return false;
                                                    }
                                                };
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new MayNotBeTargetedByWeaponsModifier(self, Filters.and(pack, Filters.not(finalLead)), duringThisPacksAttack),
                                                                "Weapons may only target " + GameUtils.getCardLink(finalLead)));
                                                action.appendEffect(new AttackEffect(action, finalTarget, finalLead));
                                                // Pack may then move to an adjacent site.
                                                action.appendEffect(
                                                        new PassthruEffect(action) {
                                                            @Override
                                                            protected void doPlayEffect(final SwccgGame game) {
                                                                final Collection<PhysicalCard> packCards = Filters.filterActive(game, self, pack);
                                                                if (packCards.isEmpty()) {
                                                                    return;
                                                                }
                                                                Filter destinations = Filters.and(Filters.site, Filters.adjacentSite(packCards.iterator().next()));
                                                                for (PhysicalCard creature : packCards) {
                                                                    destinations = Filters.and(destinations, creature.getBlueprint().getHabitatFilter(game, creature));
                                                                }
                                                                if (!Filters.canSpotFromTopLocationsOnTable(game, destinations)) {
                                                                    return;
                                                                }
                                                                action.appendEffect(
                                                                        new ChooseCardOnTableEffect(action, playerId, "Choose adjacent site to move pack to (or pass)", destinations, 0) {
                                                                            @Override
                                                                            protected void cardSelected(PhysicalCard site) {
                                                                                Collection<PhysicalCard> stillThere = Filters.filterActive(game, self, pack);
                                                                                if (!stillThere.isEmpty()) {
                                                                                    action.appendEffect(
                                                                                            new RelocateBetweenLocationsEffect(action, stillThere, site, true));
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                    }
                });
                actions.add(action);
            }
        }

        if (GameConditions.hasLostPile(game, playerId)
                && GameConditions.canUseForce(game, playerId, 3)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Use 3 Force to retrieve a creature");
            action.appendCost(new UseForceEffect(action, playerId, 3));
            action.allowResponses("Retrieve a creature",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new RetrieveCardEffect(action, playerId, Filters.creature));
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
