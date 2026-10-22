package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that limits Force retrieval by the affected cards' game text to once per turn (Underworld Contacts &
 * Uncharted Settlements 1283_21: "you may retrieve Force with [Yerka Mig] only once per turn"). The affected card's
 * retrieval action checks for this modifier.
 */
public class MayRetrieveForceWithCardOnlyOncePerTurnModifier extends AbstractModifier {

    /**
     * Creates a modifier that limits Force retrieval by the affected cards' game text to once per turn.
     * @param source the source of the modifier
     * @param affectFilter the cards whose Force retrieval is limited
     */
    public MayRetrieveForceWithCardOnlyOncePerTurnModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that limits Force retrieval by the affected cards' game text to once per turn.
     * @param source the source of the modifier
     * @param affectFilter the cards whose Force retrieval is limited
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayRetrieveForceWithCardOnlyOncePerTurnModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May retrieve Force with it only once per turn", affectFilter, condition, ModifierType.MAY_RETRIEVE_FORCE_WITH_CARD_ONLY_ONCE_PER_TURN, true);
    }
}
