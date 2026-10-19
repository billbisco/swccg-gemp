package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Immunity to cards matching a Filter (for example only the Premium printing of a title).
 */
public class ImmuneToCardMatchingFilterModifier extends AbstractModifier {
    private Filter _immuneToFilter;

    public ImmuneToCardMatchingFilterModifier(PhysicalCard source, Filterable affectFilter, Filterable immuneToFilter) {
        this(source, affectFilter, null, immuneToFilter);
    }

    public ImmuneToCardMatchingFilterModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable immuneToFilter) {
        super(source, null, affectFilter, condition, ModifierType.IMMUNE_TO_TITLE, true);
        _immuneToFilter = Filters.and(immuneToFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Immune to matching cards";
    }

    @Override
    public boolean isImmuneToCardModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, SwccgBuiltInCardBlueprint permanentWeapon) {
        return _immuneToFilter.accepts(gameState, modifiersQuerying, card)
                || (permanentWeapon != null && _immuneToFilter.accepts(gameState, modifiersQuerying, permanentWeapon));
    }

    @Override
    public boolean isImmuneToCardTitleModifier(GameState gameState, ModifiersQuerying modifiersQuerying, String cardTitle) {
        return false;
    }
}
