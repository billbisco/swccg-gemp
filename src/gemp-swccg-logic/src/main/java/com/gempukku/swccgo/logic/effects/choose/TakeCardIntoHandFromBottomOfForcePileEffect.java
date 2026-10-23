package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the player to take the bottom card of Force pile into hand.
 */
public class TakeCardIntoHandFromBottomOfForcePileEffect extends DrawCardsIntoHandFromPileEffect {

    /**
     * Creates an effect that causes the player to take the bottom card of Force pile into hand.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public TakeCardIntoHandFromBottomOfForcePileEffect(Action action, String playerId) {
        super(action, playerId, 1, Zone.FORCE_PILE, true, true);
    }

    @Override
    public String getText(SwccgGame game) {
        return "Take bottom card of Force Pile into hand";
    }

    @Override
    protected void cardsDrawnIntoHand(Collection<PhysicalCard> cards) {
    }
}
