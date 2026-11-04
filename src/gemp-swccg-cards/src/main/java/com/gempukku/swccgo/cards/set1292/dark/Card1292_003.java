package com.gempukku.swccgo.cards.set1292.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 9.2
 * Type: Interrupt
 * Subtype: Lost
 * Title: I Have You Now (V)
 */
public class Card1292_003 extends AbstractLostInterrupt {
    public Card1292_003() {
        super(Side.DARK, 5, Title.I_Have_You_Now, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Several fighters have broken off from the main group. Come with me.' Darth Vader targets his TIE fighter's fire-linked blaster cannons at the Rebel pilots in the trench.");
        setGameText("If Vader is in battle with an opponent's character of ability > 2, add one battle destiny (if with Anakin or Luke, all of your destiny draws are +1 for remainder of battle).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        Filter opponentsCharacterOfAbilityMoreThan2 = Filters.and(Filters.owner(opponent), Filters.character, Filters.abilityMoreThan(2));

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.owner(playerId), Filters.Vader))
                && GameConditions.isDuringBattleWithParticipant(game, opponentsCharacterOfAbilityMoreThan2)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.owner(opponent), Filters.or(Filters.Anakin, Filters.Luke)))) {
                                action.appendEffect(
                                        new AddUntilEndOfBattleModifierEffect(action,
                                                new DestinyWhenDrawnForDestinyModifier(self, Filters.owner(playerId), 1),
                                                "Makes all of " + playerId + "'s destiny draws +1"));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
