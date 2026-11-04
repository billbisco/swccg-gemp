package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that gives the affected cards the Presence icon. The Presence icon is never added by the ordinary icon
 * modifiers (to avoid circular presence checks), so this has its own modifier type, read by Icons.getIconCount.
 */
public class GainsPresenceIconModifier extends AbstractModifier {

    /**
     * Creates a modifier that gives cards accepted by the filter the Presence icon.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public GainsPresenceIconModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Gains Presence icon", affectFilter, null, ModifierType.LEGACY__GAINS_PRESENCE_ICON, true);
    }
}
