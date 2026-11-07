package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that lets the word 'Effect' in the game text of the affected cards be treated as some other card (or cards),
 * for example "'Effect' on Alter may be treated as 'Mantellian Savrip.'" Cards accepted by the treated-as filter may then
 * be chosen by the affected card wherever it chooses an Effect (see Filters.treatedAsEffectOnCard).
 */
public class TreatEffectOnCardAsModifier extends AbstractModifier {
    private final Filter _treatedAsFilter;

    /**
     * Creates a modifier that lets 'Effect' on cards accepted by the affect filter be treated as cards accepted by the
     * treated-as filter.
     * @param source the source of the modifier
     * @param affectFilter the cards whose game text 'Effect' may be treated as something else
     * @param treatedAsFilter the cards that 'Effect' may be treated as
     */
    public TreatEffectOnCardAsModifier(PhysicalCard source, Filterable affectFilter, Filterable treatedAsFilter) {
        this(source, affectFilter, null, treatedAsFilter);
    }

    /**
     * Creates a modifier that lets 'Effect' on cards accepted by the affect filter be treated as cards accepted by the
     * treated-as filter.
     * @param source the source of the modifier
     * @param affectFilter the cards whose game text 'Effect' may be treated as something else
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param treatedAsFilter the cards that 'Effect' may be treated as
     */
    public TreatEffectOnCardAsModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable treatedAsFilter) {
        super(source, "'Effect' may be treated as another card", affectFilter, condition, ModifierType.TREAT_EFFECT_ON_CARD_AS);
        _treatedAsFilter = Filters.and(treatedAsFilter);
    }

    /**
     * Gets the filter for the cards that 'Effect' may be treated as.
     * @return the filter
     */
    public Filter getTreatedAsFilter() {
        return _treatedAsFilter;
    }
}
