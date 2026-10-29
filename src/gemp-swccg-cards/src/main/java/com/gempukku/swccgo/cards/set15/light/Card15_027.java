package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set9.light.Card9_032;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Close Air Support. */
public class Card15_027 extends Card9_032 {
    public Card15_027() {
        setLore("");
        setCredits(2);
        setGameText("Deploy on table. While your Z-95 occupies a system or cloud sector, once per turn your scout at a related exterior site may add one battle destiny (if Z-95 is Tala 1 or Tala 2, may also add one destiny to total power only).");
        setAlternateDestiny(5);
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
        return 5f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
