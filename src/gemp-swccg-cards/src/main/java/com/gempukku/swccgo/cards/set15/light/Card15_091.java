package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set9.light.Card9_052;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Insertion Planning. */
public class Card15_091 extends Card9_052 {
    public Card15_091() {
        setLore("");
        setCredits(3);
        setGameText("If your scout is in battle at an exterior planet site, subtract 3 from opponent's total battle destiny. OR Target opponent's character aboard an open vehicle moving to your scout's site. Draw destiny. If destiny +2 > ability, target goes to Used Pile.");
        setAlternateDestiny(6);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) {
                addIcon(icon, 0);
            }
        }
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.REFLECTIONS_GOLD;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNKNOWN;
    }

    @Override
    public Float getDestiny() {
        return 6f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
