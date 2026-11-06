package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put the specified card from void in Lost Pile.
 */
public class PutCardFromVoidInLostPileEffect extends PutOneCardFromVoidInCardPileEffect {

    /**
     * Creates an effect that causes the player to put the specified card from void in Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param card the card
     */
    public PutCardFromVoidInLostPileEffect(Action action, String playerId, PhysicalCard card) {
        this(action, playerId, card, false);
    }

    /** Places the card on the specified end of Lost Pile, retaining normal discard results. */
    public PutCardFromVoidInLostPileEffect(Action action, String playerId, PhysicalCard card, boolean bottom) {
        super(action, card, Zone.LOST_PILE, bottom, ((playerId != null) ? playerId : card.getOwner())
                + " puts " + GameUtils.getCardLink(card) + (bottom ? " on bottom of Lost Pile" : " in Lost Pile"));
    }
}
