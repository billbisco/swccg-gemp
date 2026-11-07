package com.gempukku.swccgo.cards.set1240.dark;

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
import com.gempukku.swccgo.filters.LegacyCreatureFilters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AttackEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Interrupt
 * Subtype: Used
 * Title: Let's Pass On That
 */
public class Card1240_045 extends AbstractUsedInterrupt {
    public Card1240_045() {
        super(Side.DARK, 4, "Let's Pass On That", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLore("'Uu a'kingsa riika pagh bagla bis kachata weenow con bantha poodoo.'");
        setGameText("During opponent's control phase, your ferocious creature attacks. At end of turn, if that creature is at an unoccupied site and did not move, opponent loses 1 Force and you retrieve 1 Force. OR Use 3 Force to retrieve a creature.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // During opponent's control phase, your ferocious creature attacks.
        final Filter creatureThatCanAttack = Filters.and(Filters.your(playerId), LegacyCreatureFilters.ferocious, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpot(game, self, Filters.nonCreatureCanBeAttackedByCreature(physicalCard, false));
            }
        });
        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                && !GameConditions.isDuringAttack(game)
                && !GameConditions.isDuringBattle(game)
                && GameConditions.canSpot(game, self, creatureThatCanAttack)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Have ferocious creature attack");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose creature", creatureThatCanAttack) {
                @Override
                protected void cardTargeted(final int creatureGroup, final PhysicalCard creature) {
                    action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose card to attack",
                            Filters.nonCreatureCanBeAttackedByCreature(creature, false)) {
                        @Override
                        protected void cardTargeted(final int targetGroup, PhysicalCard targetedCard) {
                            action.addAnimationGroup(creature);
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Have " + GameUtils.getCardLink(creature) + " attack " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalCreature = action.getPrimaryTargetCard(creatureGroup);
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroup);
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnActionProxyEffect(action,
                                                            new EndOfTurnCheck(self, finalCreature, playerId, opponent)));
                                            action.appendEffect(
                                                    new AttackEffect(action, finalTarget, finalCreature));
                                        }
                                    });
                        }
                    });
                }
            });
            actions.add(action);
        }

        // OR Use 3 Force to retrieve a creature.
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.hasLostPile(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Retrieve a creature");
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

    /**
     * At end of turn, if the attacking creature is at an unoccupied site and did not move, opponent loses 1 Force and
     * the creature's owner retrieves 1 Force.
     */
    private static class EndOfTurnCheck extends AbstractActionProxy {
        private final int _selfPermCardId;
        private final int _creaturePermCardId;
        private final String _playerId;
        private final String _opponent;
        private boolean _moved;

        private EndOfTurnCheck(PhysicalCard self, PhysicalCard creature, String playerId, String opponent) {
            _selfPermCardId = self.getPermanentCardId();
            _creaturePermCardId = creature.getPermanentCardId();
            _playerId = playerId;
            _opponent = opponent;
        }

        @Override
        public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
            PhysicalCard self = game.findCardByPermanentId(_selfPermCardId);
            PhysicalCard creature = game.findCardByPermanentId(_creaturePermCardId);
            if (creature == null) {
                return null;
            }
            if (TriggerConditions.moved(game, effectResult, Filters.samePermanentCardId(creature))) {
                _moved = true;
                return null;
            }
            if (TriggerConditions.isEndOfEachTurn(game, effectResult)
                    && !_moved
                    && Filters.onTable.accepts(game, creature)
                    && Filters.at(Filters.and(Filters.site, Filters.unoccupied)).accepts(game, creature)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, self.getCardId());
                action.setText("Make opponent lose 1 Force and retrieve 1 Force");
                action.setActionMsg("Make " + _opponent + " lose 1 Force and retrieve 1 Force due to " + GameUtils.getCardLink(creature));
                action.appendEffect(new LoseForceEffect(action, _opponent, 1));
                action.appendEffect(new RetrieveForceEffect(action, _playerId, 1));
                return Collections.singletonList(action);
            }
            return null;
        }
    }
}
