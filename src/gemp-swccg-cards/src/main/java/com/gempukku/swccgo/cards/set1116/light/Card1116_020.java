package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Interrupt
 * Subtype: Lost
 * Title: Wookiee Roar (V)
 */
public class Card1116_020 extends AbstractLostInterrupt {
    public Card1116_020() {
        super(Side.LIGHT, 3, Title.Wookiee_Roar, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("If a battle was just initiated involving a Wookiee, add one destiny to your total power (two if alone). That Wookiee is defense value +3 for remainder of turn. OR If you have 8 or more cards in hand, cancel a Force drain.");
        addIcons(Icon.A_NEW_HOPE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.Wookiee))) {
            final boolean alone = GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.Wookiee, Filters.alone));
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add destiny to total power");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new AddDestinyToTotalPowerEffect(action, alone ? 2 : 1));
                    action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                            new DefenseValueModifier(self, Filters.and(Filters.your(playerId), Filters.Wookiee, Filters.participatingInBattle), 3),
                            "Wookiee is defense value +3"));
                }
            });
            actions.add(action);
        }
        if (GameConditions.canCancelForceDrain(game, self)
                && game.getGameState().getHand(playerId).size() >= 8) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Force drain");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new CancelForceDrainEffect(action));
                }
            });
            actions.add(action);
        }
        return actions;
    }
}
