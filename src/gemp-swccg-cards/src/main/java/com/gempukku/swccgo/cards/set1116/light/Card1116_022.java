package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: A Jedi's Patience (V)
 */
public class Card1116_022 extends AbstractUsedOrLostInterrupt {
    public Card1116_022() {
        super(Side.LIGHT, 5, "A Jedi's Patience", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("USED: If a captive on table, activate 3 Force. LOST: Once per game, if a Dark Jedi present with Leia or a Jedi (even as a non-frozen captive), they duel. Both players draw 2 destiny; add ability. Higher total wins. Loser may not participate in battle or move this turn.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (GameConditions.canSpot(game, self, Filters.captive)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Activate 3 Force");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new ActivateForceEffect(action, playerId, 3));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
