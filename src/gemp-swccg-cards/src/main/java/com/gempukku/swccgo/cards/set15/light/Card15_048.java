package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_062;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Revolution. */
public class Card15_048 extends Card1_062 {
    public Card15_048() {
        setLore("");
        setCredits(4);
        setGameText("Use 3 Force to deploy on any location. Rotate the location so that icons and game texts switch direction. (If Expand the Empire present, it also switches.) If Revolution later canceled, location rotates back again.");
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
        return Uniqueness.UNIQUE;
    }
}
