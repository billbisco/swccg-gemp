package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;

/**
 * VS12 Heroic: whenever a Heroic card is targeted by a weapon, add 1 to total weapon destiny.
 * Targeting restriction lives in {@link com.gempukku.swccgo.logic.Heroic#restrictWeaponTargets}.
 */
public class HeroicRule implements Rule {
    private ModifiersEnvironment _modifiersEnvironment;

    public HeroicRule(ModifiersEnvironment modifiersEnvironment) {
        _modifiersEnvironment = modifiersEnvironment;
    }

    public void applyRule() {
        _modifiersEnvironment.addAlwaysOnModifier(
                new TotalWeaponDestinyModifier(null, Filters.any, 1, Keyword.HEROIC));
    }
}
