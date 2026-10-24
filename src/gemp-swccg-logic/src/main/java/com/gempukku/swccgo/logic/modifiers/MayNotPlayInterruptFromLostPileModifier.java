package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the specified player from playing Interrupts from Lost Pile.
 */
public class MayNotPlayInterruptFromLostPileModifier extends AbstractModifier {

    public MayNotPlayInterruptFromLostPileModifier(PhysicalCard source, Condition condition, String playerId) {
        super(source, "May not play Interrupts from Lost Pile", null, condition, ModifierType.MAY_NOT_PLAY_INTERRUPT_FROM_LOST_PILE);
        _playerId = playerId;
    }
}
