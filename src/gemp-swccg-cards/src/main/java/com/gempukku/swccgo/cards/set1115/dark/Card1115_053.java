package com.gempukku.swccgo.cards.set1115.dark;

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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttackEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Interrupt
 * Subtype: Used
 * Title: Let's Pass On That (V)
 */
public class Card1115_053 extends AbstractUsedInterrupt {
    public Card1115_053() {
        super(Side.DARK, 4, "Let's Pass On That", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Uu a'kingsa riika pagh bagla bis kachata weenow con bantha poodoo.'");
        setGameText("During opponent's control phase, your ferocious creature attacks. At end of turn, if that creature is at an unoccupied site and did not move, opponent loses 1 Force and you retrieve 1 Force. OR Use 3 Force to retrieve a creature.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                && !GameConditions.isDuringAttack(game)
                && !GameConditions.isDuringBattle(game)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.creature))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Ferocious creature attacks");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose creature",
                    Filters.and(Filters.your(playerId), Filters.creature)) {
                @Override
                protected void cardTargeted(final int creatureGroup, final PhysicalCard creature) {
                    if (!GameConditions.canSpot(game, self, Filters.nonCreatureCanBeAttackedByCreature(creature, false))) {
                        return;
                    }
                    action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose card to attack",
                            Filters.nonCreatureCanBeAttackedByCreature(creature, false)) {
                        @Override
                        protected void cardTargeted(final int targetGroup, PhysicalCard targetedCard) {
                            action.allowResponses("Have " + GameUtils.getCardLink(creature) + " attack",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(new AttackEffect(action,
                                                    action.getPrimaryTargetCard(targetGroup),
                                                    action.getPrimaryTargetCard(creatureGroup)));
                                        }
                                    });
                        }
                    });
                }
            });
            actions.add(action);
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
}
