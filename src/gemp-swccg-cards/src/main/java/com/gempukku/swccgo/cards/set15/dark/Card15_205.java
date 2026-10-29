package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set6.dark.Card6_149;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Scum And Villainy. */
public class Card15_205 extends Card6_149 {
    public Card15_205() {
        setLore("");
        setCredits(4);
        setGameText("Deploy on Audience Chamber. While all your ability on table is provided by aliens and independent starship pilots, your aliens and starships deploy -1 and you retrieve 2 Force whenever you initiate battle. (Immune to Alter if you control at least three Jabba's Palace sites.)");
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
