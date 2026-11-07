package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;

/** A system is liberated while this source's modifier is active, without a stacked card. */
public final class LiberatedSystemModifier extends AbstractModifier {
    public LiberatedSystemModifier(PhysicalCard source, PhysicalCard system) {
        super(source, "Is 'liberated'", system, null, ModifierType.LIBERATED_SYSTEM, true);
    }
}
