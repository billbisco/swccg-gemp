package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected sites, when they leave table (and would otherwise be lost), to be placed in their
 * owner's Used Pile together with all cards there (instead of the Lost Pile).
 */
public class SiteAndCardsThereToUsedPileWhenLeavesTableModifier extends AbstractModifier {

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param siteFilter the filter for affected sites
     */
    public SiteAndCardsThereToUsedPileWhenLeavesTableModifier(PhysicalCard source, Filterable siteFilter) {
        this(source, siteFilter, null);
    }

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param siteFilter the filter for affected sites
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public SiteAndCardsThereToUsedPileWhenLeavesTableModifier(PhysicalCard source, Filterable siteFilter, Condition condition) {
        super(source, "When leaves table, it and all cards there go to Used Pile", siteFilter, condition,
                ModifierType.SITE_AND_CARDS_THERE_TO_USED_PILE_WHEN_LEAVES_TABLE);
    }
}
