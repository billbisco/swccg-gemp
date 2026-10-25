package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Atmospheric Assault
 */
public class Card1220_053 extends AbstractUsedInterrupt {
    public Card1220_053() {
        super(Side.DARK, 5, Title.Atmospheric_Assault, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("The TIEs patrolling Cloud City took advantage of Bespin's dense cloud cover.");
        setGameText("If your TIE is defending a battle alone at a cloud sector, add two destiny to power only. OR During battle, if you have a TIE present piloted by a character, opponent's starship present without a pilot character aboard is power -3.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.isDuringBattleAt(game, Filters.cloud_sector)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.TIE, Filters.defendingBattle, Filters.alone, Filters.at(Filters.cloud_sector)))
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add two destiny to power");
            action.allowResponses("Add two destiny to power only",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddDestinyToTotalPowerEffect(action, 2, playerId));
                        }
                    }
            );
            actions.add(action);
        }

        Filter starshipFilter = Filters.and(Filters.opponents(self), Filters.starship, Filters.presentInBattle,
                Filters.not(Filters.hasPiloting(self, Filters.character)));
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.TIE, Filters.presentInBattle, Filters.hasPiloting(self, Filters.character)))
                && GameConditions.canTarget(game, self, starshipFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent's starship power -3");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", starshipFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " power -3",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new PowerModifier(self, finalTarget, -3),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " power -3"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
