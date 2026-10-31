package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttackEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Interrupt
 * Subtype: Used
 * Title: Shoo! Shoo!
 */
public class Card1105_020 extends AbstractUsedInterrupt {
    public Card1105_020() {
        super(Side.LIGHT, 6, "Shoo! Shoo!", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Oh! Go away! Go away! Beastly thing!'");
        setGameText("Relocate an opponent's ferocious creature to a related location (habitat permitting). OR During any move phase, initiate an attack against a creature. OR Cancel an attempt by Reegesk to steal a card; Reegesk is lost. (Immune to Sense.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Sense);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        Action performingAction = effect.getAction();
        PhysicalCard source = performingAction != null ? performingAction.getActionSource() : null;
        if (source != null
                && Filters.Reegesk.accepts(game, source)
                && performingAction.getGameTextActionId() == GameTextActionId.REEGESK__TAKE_CARD_FROM_OPPONENTS_LOST_PILE_OR_CRASH_SITE_MEMORIAL) {
            final PhysicalCard reegesk = source;
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Reegesk steal; Reegesk is lost");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            performingAction.appendCost(
                                    new com.gempukku.swccgo.logic.timing.FailCostEffect(performingAction));
                            action.appendEffect(
                                    new LoseCardFromTableEffect(action, reegesk));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        Filter creatureFilter = Filters.and(Filters.opponents(playerId), Filters.creature, Filters.canBeTargetedBy(self));
        if (GameConditions.canTarget(game, self, creatureFilter)) {
            Collection<PhysicalCard> possibleTargets = new LinkedList<>();
            for (PhysicalCard creature : Filters.filterAllOnTable(game, creatureFilter)) {
                Filter habitat = creature.getBlueprint().getHabitatFilter(game, creature);
                if (Filters.canSpotFromTopLocationsOnTable(game, Filters.and(habitat, Filters.relatedLocation(creature), Filters.not(Filters.sameLocation(creature))))) {
                    possibleTargets.add(creature);
                }
            }
            if (!possibleTargets.isEmpty()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate opponent's ferocious creature");
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target an opponent's creature to relocate", Filters.in(possibleTargets)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedCreature) {
                        Filter habitat = targetedCreature.getBlueprint().getHabitatFilter(game, targetedCreature);
                        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target related location", Filters.and(habitat, Filters.relatedLocation(targetedCreature), Filters.not(Filters.sameLocation(targetedCreature)))) {
                            @Override
                            protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedLocation) {
                                action.allowResponses(new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        PhysicalCard finalCreature = action.getPrimaryTargetCard(targetGroupId1);
                                        PhysicalCard finalLocation = action.getPrimaryTargetCard(targetGroupId2);
                                        action.appendEffect(new RelocateBetweenLocationsEffect(action, finalCreature, finalLocation));
                                    }
                                });
                            }
                        });
                    }
                });
                actions.add(action);
            }
        }

        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)
                && !GameConditions.isDuringAttack(game)
                && !GameConditions.isDuringBattle(game)) {
            Filter attackable = Filters.and(Filters.creature, Filters.canBeTargetedBy(self));
            if (GameConditions.canTarget(game, self, attackable)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Initiate an attack against a creature");
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a creature to attack", attackable) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCreature) {
                        action.allowResponses(new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard finalCreature = action.getPrimaryTargetCard(targetGroupId);
                                action.appendEffect(new AttackEffect(action, finalCreature));
                            }
                        });
                    }
                });
                actions.add(action);
            }
        }

        return actions;
    }
}
