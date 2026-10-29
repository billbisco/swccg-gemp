package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set6.light.Card6_055;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of I Must Be Allowed To Speak. */
public class Card15_036 extends Card6_055 {
    public Card15_036() {
        setLore("");
        setCredits(2);
        setGameText("Deploy on a Jabba's Palace site. Luke may deploy at this site regardless of presence or location deployment restrictions. When he is deployed here, relocate Effect to Luke, he is immune to attrition while on Tatooine. (Immune to Alter.)");
        setAlternateDestiny(4);
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
        return 4f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
