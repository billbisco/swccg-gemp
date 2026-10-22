package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that lets imprisoned captives released at the affected locations choose to 'Escape' (normally an
 * imprisoned captive that is released must 'Rally').
 */
public class ImprisonedCaptivesReleasedMayEscapeModifier extends AbstractModifier {

    /**
     * Creates a modifier that lets imprisoned captives released at the affected locations choose to 'Escape'.
     * @param source the source of the modifier
     * @param locationFilter the locations where released imprisoned captives may 'Escape'
     */
    public ImprisonedCaptivesReleasedMayEscapeModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null);
    }

    /**
     * Creates a modifier that lets imprisoned captives released at the affected locations choose to 'Escape'.
     * @param source the source of the modifier
     * @param locationFilter the locations where released imprisoned captives may 'Escape'
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ImprisonedCaptivesReleasedMayEscapeModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        super(source, "Imprisoned captives released here may 'Escape'", locationFilter, condition, ModifierType.IMPRISONED_CAPTIVES_RELEASED_MAY_ESCAPE, true);
    }
}
