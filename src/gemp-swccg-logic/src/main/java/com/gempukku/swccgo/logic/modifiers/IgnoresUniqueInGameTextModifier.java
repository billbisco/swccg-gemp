package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that makes the affected cards ignore 'unique (•)' requirements in their own game text.
 * Cards that honor it check {@link com.gempukku.swccgo.filters.LegacyFilters#ignoresUniqueInGameText}.
 */
public class IgnoresUniqueInGameTextModifier extends AbstractModifier {

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the cards whose game text ignores 'unique (•)'
     */
    public IgnoresUniqueInGameTextModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Ignores 'unique (•)' in game text", affectFilter, null, ModifierType.IGNORES_UNIQUE_IN_GAME_TEXT, true);
    }
}
