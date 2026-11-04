package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that stacks a specific card face down on a specified card, taking it from its owner's hand or from the
 * void (where an Interrupt is while it is being played). Used for "stack that card here face down" on a card whose
 * play is being stopped, e.g. Where Are You Taking This ... Thing? (Virtual Block 7.4) and Nabrun Leids.
 */
public class LegacyStackCardFaceDownFromHandOrVoidEffect extends AbstractSuccessfulEffect {
    private final PhysicalCard _card;
    private final PhysicalCard _stackOn;

    /**
     * Creates an effect that stacks a card face down on a specified card.
     * @param action the action performing this effect
     * @param card the card to stack
     * @param stackOn the card to stack the card on
     */
    public LegacyStackCardFaceDownFromHandOrVoidEffect(Action action, PhysicalCard card, PhysicalCard stackOn) {
        super(action);
        _card = card;
        _stackOn = stackOn;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        Zone zone = _card.getZone();
        if ((zone == Zone.HAND || zone == Zone.VOID) && _stackOn.getZone().isInPlay()) {
            GameState gameState = game.getGameState();
            gameState.sendMessage(_stackOn.getOwner() + " stacks " + GameUtils.getCardLink(_card) + " face down on " + GameUtils.getCardLink(_stackOn));
            gameState.removeCardsFromZone(Collections.singleton(_card));
            gameState.stackCard(_card, _stackOn, true, false, false);
        }
    }
}
