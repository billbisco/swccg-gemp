package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set3.light.Card3_040;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Attack Pattern Delta. */
public class Card15_063 extends Card3_040 {
    public Card15_063() {
        setLore("");
        setCredits(1);
        setGameText("If a battle was just initiated at a site, identify your lead T-47 there. For remainder of this turn, all other T-47s at same site are power +1, are immune to attrition and cannot be targeted by weapons.");
        setAlternateDestiny(3);
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
        return 3f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNRESTRICTED;
    }
}
