package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set3.dark.Card3_131;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Our First Catch Of The Day. */
public class Card15_268 extends Card3_131 {
    public Card15_268() {
        setLore("");
        setCredits(2);
        setGameText("Add 2 to a tractor beam destiny draw. OR Cancel The First Transport Is Away! or Hyper Escape.");
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
        return Uniqueness.UNRESTRICTED;
    }
}
