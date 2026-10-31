package com.gempukku.swccgo.cards.set1109.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Interrupt
 * Subtype: Used
 * Title: Scout Recon
 */
public class Card1109_046 extends AbstractUsedInterrupt {
    public Card1109_046() {
        super(Side.DARK, 6, Title.Scout_Recon, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Familiarity with native terrain is essential to a scout garrison. Imperial command can also put this type of information to effective use.");
        setGameText("Find one of your missing characters. OR if your scout is in a battle, activate X Force, where X = the number of scouts you have in that battle. OR add 3 to your total battle destiny during a battle at a site where you have an Imperial and opponent has an alien.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId findId = GameTextActionId.LEGACY__SCOUT_RECON_V__FIND_CHARACTER;
        if (GameConditions.canSearchLostPile(game, playerId, self, findId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, findId);
            action.setText("Find a missing character");
            action.allowResponses("Take one of your missing characters into hand from Lost Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.and(Filters.your(self), Filters.character, Filters.missing), false));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.scout))) {
            final int scouts = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.scout, Filters.participatingInBattle));
            if (scouts > 0 && GameConditions.canActivateForce(game, playerId)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Activate " + scouts + " Force");
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new ActivateForceEffect(action, playerId, scouts));
                            }
                        }
                );
                actions.add(action);
            }
        }

        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Imperial))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.alien))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 3 to total battle destiny");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new TotalBattleDestinyModifier(self, 3, playerId),
                                            "Add 3 to total battle destiny"));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
