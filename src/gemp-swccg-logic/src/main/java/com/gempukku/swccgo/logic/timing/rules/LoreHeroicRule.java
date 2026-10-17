package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Lore "Heroic" is the Heroic characteristic only when the format option is on
 * (Legacy and Anything Goes). The list is blueprint ids, kept next to the rule.
 * R2-D2 (Artoo-Detoo), its (V), and that (V)'s alternate image.
 */
public class LoreHeroicRule implements Rule {
    static final Set<String> LORE_HEROIC_BLUEPRINT_IDS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "2_14",
            "201_6",
            "201_6^"
    )));

    private final ModifiersEnvironment _modifiersEnvironment;

    public LoreHeroicRule(ModifiersEnvironment modifiersEnvironment) {
        _modifiersEnvironment = modifiersEnvironment;
    }

    public void applyRule() {
        _modifiersEnvironment.addAlwaysOnModifier(
                new KeywordModifier(null, loreHeroicPrinting(), Keyword.HEROIC));
    }

    private static Filter loreHeroicPrinting() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                String id = physicalCard.getBlueprintId(true);
                if (id == null) {
                    return false;
                }
                if (LORE_HEROIC_BLUEPRINT_IDS.contains(id)) {
                    return true;
                }
                return LORE_HEROIC_BLUEPRINT_IDS.contains(id.replace("*", "").replace("^", ""));
            }
        };
    }
}
